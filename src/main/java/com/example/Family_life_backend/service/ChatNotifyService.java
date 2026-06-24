package com.example.Family_life_backend.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.request.ChatRequest;

//發送訊息通知
@Service
public class ChatNotifyService {
	
	@Autowired
    private groupMemberDao groupMemberDao;

    @Autowired
    private NotifyDao notifyDao;

    @Autowired
    private NotifySocketService notifySocketService;

    @Async
    public void sendChatNotifications(ChatRequest request, String senderName) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Taipei"));
        String content = senderName + ": " + request.getMessage();

        List<groupMembersDTO> members = groupMemberDao.getMembersByGroupId(request.getGroupId());

        for (groupMembersDTO member : members) {
            if (member.getUser_id().equals(request.getSenderId())) {
                continue;
            }

<<<<<<< HEAD
            notifyDao.sendGroupNameUpdateNotify(
                    request.getSenderId(),
                    member.getUser_id(),
                    content,
                    "chat",
                    false,
                    now
            );
=======
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
>>>>>>> origin/internet
        }
    }
}