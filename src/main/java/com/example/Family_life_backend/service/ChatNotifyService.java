package com.example.Family_life_backend.service;

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
        String content = senderName + ": " + request.getMessage();

        List<groupMembersDTO> members =
                groupMemberDao.getMembersByGroupId(request.getGroupId());

        for (groupMembersDTO member : members) {
            if (member.getUser_id() == request.getSenderId()) {
                continue;
            }

            notifyDao.sendChatNotify(
                    request.getGroupId(),
                    member.getUser_id(),
                    content,
                    "chat",
                    false
            );

            int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());

            notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
        }
    }
}
