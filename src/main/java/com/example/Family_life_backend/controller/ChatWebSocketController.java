package com.example.Family_life_backend.controller;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.Family_life_backend.DTO.OnlineUserDTO;
import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.UserInfoDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.entity.GroupChatMessage;
import com.example.Family_life_backend.entity.UserInfo;
import com.example.Family_life_backend.repositary.GroupChatRepository;
import com.example.Family_life_backend.repositary.UserRepository;
import com.example.Family_life_backend.request.ChatEnterRequest;
import com.example.Family_life_backend.request.ChatRequest;
import com.example.Family_life_backend.response.ChatMessageResponse;
import com.example.Family_life_backend.service.EmailService;
import com.example.Family_life_backend.service.NotifySocketService;
import java.util.Map;

@Controller
@CrossOrigin(origins = "*")
public class ChatWebSocketController {

	private final SimpMessagingTemplate messagingTemplate;
	private final GroupChatRepository repository;
	private final UserRepository userRepository;

	@Autowired
	private groupMemberDao groupMemberDao;

	@Autowired
	private NotifyDao notifyDao;

//	@Autowired
//	private EmailService emailService;
//
//	@Autowired
//	private UserInfoDao userInfoDao;

	@Autowired
	private NotifySocketService notifySocketService;

	public ChatWebSocketController(SimpMessagingTemplate messagingTemplate, GroupChatRepository repository,
			UserRepository userRepository) {

		this.messagingTemplate = messagingTemplate;
		this.repository = repository;
		this.userRepository = userRepository;
	}

	@MessageMapping("/chat.send")
	public void send(ChatRequest request) {

		System.out.println("收到WS訊息：" + request.getMessage());

		GroupChatMessage msg = new GroupChatMessage();

		msg.setGroupId(request.getGroupId());
		msg.setSenderId(request.getSenderId());
		msg.setMessage(request.getMessage());
		// ⭐ 新增：reply
		msg.setReplyId(request.getReplyId());

		GroupChatMessage saved = repository.save(msg);

		// 2. 查發送者
		UserInfo sender = userRepository.findById(saved.getSenderId()).orElse(null);

		String senderName = sender != null ? sender.getUserName() : "未知使用者";

		String content = senderName + ": " + request.getMessage();

		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId(request.getGroupId());

		for (groupMembersDTO member : getGroupMembers) {
			if (member.getUser_id() != request.getSenderId()) {
				notifyDao.sendChatNotify(request.getGroupId(), member.getUser_id(), content, "chat", false);
				// 🔥 正確：要重新查 unread count
				int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());

//				if (userInfoDao.getEmailNotifyById(member.getUser_id()) == true) {
//					emailService.sendMail(userInfoDao.getEmailById(member.getUser_id()), "聊天", content);
//				}

				notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
			}
		}

		UserInfo user = userRepository.findById(request.getSenderId()).orElse(null);

		// =========================
		// ⭐ 先處理 reply（放這裡）
		// =========================
		ChatMessageResponse replyDto = null;

		if (saved.getReplyId() != null) {

			GroupChatMessage reply = repository.findById(saved.getReplyId()).orElse(null);

			if (reply != null) {

				replyDto = new ChatMessageResponse();
				replyDto.setId(reply.getId());
				replyDto.setMessage(reply.getMessage());
				replyDto.setSenderId(reply.getSenderId());

				UserInfo replyUser = userRepository.findById(reply.getSenderId()).orElse(null);

				if (replyUser != null) {
					replyDto.setSenderName(replyUser.getUserName());
				}
			}
		}

		// =========================
		// 再組主 dto
		// =========================
		ChatMessageResponse dto = new ChatMessageResponse();

		dto.setId(saved.getId());
		dto.setGroupId(saved.getGroupId());
		dto.setSenderId(saved.getSenderId());
		dto.setMessage(saved.getMessage());
		dto.setImageUrl(msg.getImageUrl());
		dto.setCreateTime(saved.getCreateTime());
		dto.setRecalled(msg.getRecalled());

		// ⭐ replyId
		dto.setReplyId(saved.getReplyId());
		dto.setReplyMessage(replyDto); // ⭐關鍵

		// ⭐ sender info
		if (user != null) {
			dto.setSenderName(user.getUserName());
			dto.setSenderAvatar(user.getAvatar());
		}

		// ⭐ default fields（重點）
		dto.setType(msg.getImageUrl() != null ? "IMAGE" : "MESSAGE");
		dto.setReadCount(0L);

		System.out.println("推播到 topic: /topic/group/" + saved.getGroupId());

		messagingTemplate.convertAndSend("/topic/group/" + saved.getGroupId(), dto);
	}

	// 在線上
	private static final ConcurrentHashMap<Long, Set<Long>> onlineUsers = new ConcurrentHashMap<>();

	private void broadcastOnline(Long groupId) {

		Set<Long> ids = onlineUsers.getOrDefault(groupId, Set.of());

		if (ids == null) {
			ids = Set.of();
		}

		List<Long> userIds = ids.stream().toList();

		if (userIds.isEmpty()) {
			return;
		}

		List<UserInfo> users = userRepository.findAllById(userIds);

		List<OnlineUserDTO> userList = users.stream()
				.map(u -> new OnlineUserDTO((long) u.getUserId(), u.getUserName(), u.getAvatar())).toList();

		messagingTemplate.convertAndSend("/topic/group/" + groupId,
				Map.of("type", "ONLINE", "count", userList.size(), "users", userList));
	}

	@MessageMapping("/chat.enter")
	public void enter(ChatEnterRequest request) {

		onlineUsers.computeIfAbsent(request.getGroupId(), k -> ConcurrentHashMap.newKeySet()).add(request.getUserId());

		broadcastOnline(request.getGroupId());
	}

	@MessageMapping("/chat.leave")
	public void leave(ChatEnterRequest request) {

		Set<Long> set = onlineUsers.get(request.getGroupId());

		if (set != null) {
			set.remove(request.getUserId());
		}

		broadcastOnline(request.getGroupId());
	}
}