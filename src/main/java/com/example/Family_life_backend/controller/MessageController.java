package com.example.Family_life_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Family_life_backend.entity.GroupChatMessage;
import com.example.Family_life_backend.entity.UserInfo;
import com.example.Family_life_backend.repositary.GroupChatRepository;
import com.example.Family_life_backend.repositary.UserRepository;
import com.example.Family_life_backend.response.ChatMessageResponse;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

	private final GroupChatRepository repository;

	private final UserRepository userRepository;

	public MessageController(GroupChatRepository repository, UserRepository userRepository) {

		this.repository = repository;
		this.userRepository = userRepository;
	}

	@GetMapping("/{groupId}")
	public ResponseEntity<?> getMessages(@PathVariable("groupId") Long groupId) {

		List<GroupChatMessage> messages = repository.findByGroupIdOrderByCreateTimeAsc(groupId);

		List<ChatMessageResponse> result = messages.stream().map(msg -> {

			UserInfo user = userRepository.findById(msg.getSenderId()).orElse(null);

			ChatMessageResponse dto = new ChatMessageResponse();

			dto.setId(msg.getId());
			dto.setGroupId(msg.getGroupId());
			dto.setSenderId(msg.getSenderId());
			dto.setMessage(msg.getMessage());
			dto.setCreateTime(msg.getCreateTime());

			if (user != null) {

				dto.setSenderName(user.getUserName());

				dto.setSenderAvatar(user.getAvatar());
			}

			return dto;

		}).toList();

		return ResponseEntity.ok(Map.of("messages", result));
	}
}