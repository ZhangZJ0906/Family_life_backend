package com.example.Family_life_backend.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
import com.example.Family_life_backend.entity.notify;
import com.example.Family_life_backend.request.ChatRequest;

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
		List<notify> notifications = new ArrayList<>();
		List<Long> userIds = new ArrayList<>();

		for (groupMembersDTO member : members) {
			if (member.getUser_id().equals(request.getSenderId())) {
				continue;
			}

			notify notification = new notify();
			notification.setSendId(request.getSenderId());
			notification.setGetUserId(member.getUser_id());
			notification.setContent(content);
			notification.setType("chat");
			notification.setRead(false);
			notification.setSendDate(now);

			notifications.add(notification);
			userIds.add(member.getUser_id());
		}

		if (notifications.isEmpty()) {
			return;
		}

		notifyDao.saveAll(notifications);

		Map<Long, Integer> unreadMap = new HashMap<>();
		for (Object[] row : notifyDao.countUnreadByUserIds(userIds)) {
			unreadMap.put(((Number) row[0]).longValue(), ((Number) row[1]).intValue());
		}

		for (Long userId : userIds) {
			notifySocketService.pushUnreadCount(userId, unreadMap.getOrDefault(userId, 0));
		}
	}
}
