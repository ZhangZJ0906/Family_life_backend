package com.example.Family_life_backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.Family_life_backend.entity.GroupChatMessage;
import com.example.Family_life_backend.entity.UserInfo;
import com.example.Family_life_backend.repositary.GroupChatRepository;
import com.example.Family_life_backend.repositary.UserRepository;
import com.example.Family_life_backend.request.ChatRequest;
import com.example.Family_life_backend.response.ChatMessageResponse;

@Controller
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

		GroupChatMessage msg = new GroupChatMessage();

		msg.setGroupId(request.getGroupId());
		msg.setSenderId(request.getSenderId());
		msg.setMessage(request.getMessage());

		GroupChatMessage saved = repository.save(msg);

		UserInfo user = userRepository.findById(request.getSenderId()).orElse(null);

		ChatMessageResponse dto = new ChatMessageResponse();

		dto.setId(saved.getId());
		dto.setGroupId(saved.getGroupId());
		dto.setSenderId(saved.getSenderId());
		dto.setMessage(saved.getMessage());
		dto.setCreateTime(saved.getCreateTime());

		if (user != null) {

			dto.setSenderName(user.getUserName());

			dto.setSenderAvatar(user.getAvatar());

		}

		// ⭐ 推播給同群組所有人
		messagingTemplate.convertAndSend("/topic/group/" + saved.getGroupId(), dto);
	}
}