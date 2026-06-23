package com.example.Family_life_backend.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
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
    
    private static final ZoneId TAIWAN_ZONE = ZoneId.of("Asia/Taipei");

    @Async
    public void sendChatNotifications(ChatRequest request, String senderName) {

        String content = senderName + ": " + request.getMessage();

        LocalDateTime now = LocalDateTime.now(TAIWAN_ZONE);

        List<groupMembersDTO> members =
                groupMemberDao.getMembersByGroupId(request.getGroupId());

        for (groupMembersDTO member : members) {

            if (Objects.equals(member.getUser_id(), request.getSenderId())) {
                continue;
            }

            notifyDao.sendChatNotify(
                    request.getGroupId(),
                    member.getUser_id(),
                    content,
                    "chat",
                    false,
                    now
            );

            int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());

            notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
        }
    }
}
