package com.example.Family_life_backend.service;

import java.time.LocalDateTime;

import java.time.ZoneId;
import java.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.request.ChatRequest;
import com.example.Family_life_backend.entity.notify;

//發送訊息通知
@Service
public class ChatNotifyService {
	
	@Autowired
    private groupMemberDao groupMemberDao;

    @Autowired
    private NotifyDao notifyDao;

    @Autowired
    private NotifySocketService notifySocketService;
    
    private static final ZoneId TAIWAN_ZONE = ZoneId.of("Asia/Taipei");

    @Async
    public void sendChatNotifications(ChatRequest request, String senderName) {

        String content = senderName + ": " + request.getMessage();
        LocalDateTime now = LocalDateTime.now(TAIWAN_ZONE);

        List<groupMembersDTO> members =
                groupMemberDao.getMembersByGroupId(request.getGroupId());
        
        List<notify> notifications = new ArrayList<>();
                
        List<Long> userIds = new ArrayList<>();

        for (groupMembersDTO member : members) {

            if (Objects.equals(member.getUser_id(), request.getSenderId())) {
                continue;
            }

            notify n = new notify();
            n.setSendId(request.getGroupId());
            n.setGetUserId(member.getUser_id());
            n.setContent(content);
            n.setType("chat");
            n.setRead(false);
            n.setSendDate(now);

            notifications.add(n);
            userIds.add(member.getUser_id());
        }

        // 🚀 1. batch insert（一次寫入）
        notifyDao.saveAll(notifications);

        // 🚀 2. batch unread count（一次查完）
        List<Object[]> counts = notifyDao.countUnreadByUserIds(userIds);

        Map<Long, Integer> countMap = new HashMap<>();
        for (Object[] row : counts) {
            countMap.put(((Number) row[0]).longValue(), ((Number) row[1]).intValue());
        }

        // 🚀 3. websocket push（不查 DB）
        for (Long userId : userIds) {
            int unread = countMap.getOrDefault(userId, 0);
            notifySocketService.pushUnreadCount(userId, unread);
        }
    }
}
