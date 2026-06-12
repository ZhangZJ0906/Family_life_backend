package com.example.Family_life_backend.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.Family_life_backend.entity.GroupChatMessage;
import com.example.Family_life_backend.entity.GroupChatRead;
import com.example.Family_life_backend.entity.UserInfo;
import com.example.Family_life_backend.repositary.GroupChatReadRepository;
import com.example.Family_life_backend.repositary.GroupChatRepository;
import com.example.Family_life_backend.repositary.UserRepository;
import com.example.Family_life_backend.response.ChatMessageResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

	private final GroupChatRepository repository;

	private final UserRepository userRepository;

	private final GroupChatReadRepository readRepository;

	private final SimpMessagingTemplate messagingTemplate;

	public MessageController(GroupChatRepository repository, UserRepository userRepository,
			GroupChatReadRepository readRepository, SimpMessagingTemplate messagingTemplate) {

		this.repository = repository;
		this.userRepository = userRepository;
		this.readRepository = readRepository;
		this.messagingTemplate = messagingTemplate;
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

	@PostMapping("/read/{groupId}")
	public void markRead(@PathVariable("groupId") Long groupId, @RequestParam("userId") Long userId) {

		List<GroupChatMessage> messages = repository.findByGroupId(groupId);

		for (GroupChatMessage msg : messages) {

			boolean exists = readRepository.existsByMessageIdAndUserId(msg.getId(), userId);

			if (!exists) {

				GroupChatRead read = new GroupChatRead();
				read.setMessageId(msg.getId());
				read.setUserId(userId);
				read.setReadTime(LocalDateTime.now());

				readRepository.save(read);
			}
		}
	}

	@PostMapping("/upload")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("groupId") Long groupId,
			@RequestParam("senderId") Long senderId) throws Exception {

		String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

		String fileName = UUID.randomUUID() + ".jpg";

		Path path = Paths.get("uploads/" + fileName);

		Files.createDirectories(path.getParent());

		Files.copy(file.getInputStream(), path);

		// 1. 存 DB
		GroupChatMessage msg = new GroupChatMessage();
		msg.setGroupId(groupId);
		msg.setSenderId(senderId);
		msg.setImageUrl(baseUrl + "/uploads/" + fileName);

		GroupChatMessage saved = repository.save(msg);

		// 2. 查 user
		UserInfo user = userRepository.findById(senderId).orElse(null);

		// 3. 組 WebSocket DTO
		ChatMessageResponse dto = new ChatMessageResponse();

		dto.setId(saved.getId());
		dto.setGroupId(groupId);
		dto.setSenderId(senderId);
		dto.setImageUrl(saved.getImageUrl()); // ⭐重點
		dto.setType(msg.getImageUrl() != null ? "IMAGE" : "MESSAGE");

		if (user != null) {
			dto.setSenderName(user.getUserName());
			dto.setSenderAvatar(user.getAvatar());
		}

		// 4. ⭐ WebSocket 推播（關鍵）
		messagingTemplate.convertAndSend("/topic/group/" + groupId, dto);

		return ResponseEntity.ok(dto);
	}

}