package com.example.Family_life_backend.service;

import java.time.LocalDateTime;

import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.Manager.PresenceManager;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.request.ChatRequest;
import com.example.Family_life_backend.entity.GroupChatMessage;
import com.example.Family_life_backend.entity.notify;
import com.example.Family_life_backend.repositary.GroupChatRepository;

//發送訊息通知
@Service
public class ChatNotifyService {

	@Autowired
	private groupMemberDao groupMemberDao;

	@Autowired
	private NotifyDao notifyDao;

	@Autowired
	private NotifySocketService notifySocketService;

	@Autowired
	private PresenceManager presenceManager;
	
	@Autowired
	private GroupChatRepository groupChatRepositary;

	private static final ZoneId TAIWAN_ZONE = ZoneId.of("Asia/Taipei");

	@Async
	public void sendChatNotifications(ChatRequest request, String senderName) {

		GroupChatMessage replyMessage = groupChatRepositary.findById(request.getReplyId()).orElse(null);

		Long replyOwner = replyMessage.getSenderId();

		String content;

		LocalDateTime now = LocalDateTime.now(TAIWAN_ZONE);

		List<groupMembersDTO> members = groupMemberDao.getMembersByGroupId(request.getGroupId());

		Set<Long> onlineUsers = presenceManager.getOnlineUserSet(request.getGroupId());

		List<notify> notifications = new ArrayList<>();

		List<Long> userIds = new ArrayList<>();

		for (groupMembersDTO member : members) {

			Long userId = member.getUser_id();

			// 自己
			if (Objects.equals(userId, request.getSenderId())) {
				continue;
			}

			// 在線中，不通知
			if (onlineUsers.contains(userId)) {
				continue;
			}
			
			if (Objects.equals(member.getUser_id(), replyOwner)) {
			    content = senderName + " 已回覆你的訊息：" + request.getMessage();
			} else {
			    content = senderName + ": " + request.getMessage();
			}

			notify n = new notify();
			n.setSendId(request.getGroupId());
			n.setGetUserId(userId);
			n.setContent(content);
			n.setType("chat");
			n.setRead(false);
			n.setSendDate(now);

			notifications.add(n);
			userIds.add(userId);
		}

		// 🚀 1. batch insert（一次寫入）
		if (notifications.isEmpty()) {
			return;
		}
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
