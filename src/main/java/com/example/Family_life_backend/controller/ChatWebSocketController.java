package com.example.Family_life_backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.Family_life_backend.entity.GroupChatMessage;
import com.example.Family_life_backend.entity.UserInfo;
import com.example.Family_life_backend.repositary.GroupChatRepository;
import com.example.Family_life_backend.repositary.UserRepository;
import com.example.Family_life_backend.request.ChatRequest;
import com.example.Family_life_backend.response.ChatMessageResponse;

@Controller
@CrossOrigin(origins = "*")
public class ChatWebSocketController {

	private final SimpMessagingTemplate messagingTemplate;
	private final GroupChatRepository repository;
	private final UserRepository userRepository;

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
}