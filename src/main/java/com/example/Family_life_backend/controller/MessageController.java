package com.example.Family_life_backend.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Family_life_backend.DTO.GroupChatReadCountDTO;
import com.example.Family_life_backend.entity.GroupChatMessage;
import com.example.Family_life_backend.entity.GroupChatRead;
import com.example.Family_life_backend.entity.UserInfo;
import com.example.Family_life_backend.repositary.GroupChatReadRepository;
import com.example.Family_life_backend.repositary.GroupChatRepository;
import com.example.Family_life_backend.repositary.UserRepository;
import com.example.Family_life_backend.response.ChatMessageResponse;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:4200")
//@CrossOrigin(origins = "*")
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
	public ResponseEntity<?> getMessages(@PathVariable("groupId") Long groupId, @RequestParam("userId") Long userId) {

		List<GroupChatMessage> messages = repository.findByGroupIdOrderByCreateTimeAsc(groupId);

		Long currentUserId = userId;
		// =========================
		// 1. batch users
		// =========================
		List<Long> senderIds = messages.stream().map(GroupChatMessage::getSenderId).distinct().toList();

		Map<Long, UserInfo> userMap = userRepository.findAllById(senderIds).stream()
				.collect(Collectors.toMap(u -> (long) u.getUserId(), Function.identity()));

		// =========================
		// 2. batch reply messages
		// =========================
		List<Long> replyIds = messages.stream().map(GroupChatMessage::getReplyId).filter(Objects::nonNull).distinct()
				.toList();

		Map<Long, GroupChatMessage> replyMap = replyIds.isEmpty() ? Map.of()
				: repository.findAllById(replyIds).stream()
						.collect(Collectors.toMap(GroupChatMessage::getId, Function.identity()));

		// =========================
		// 3. batch read count
		// =========================
		List<Long> messageIds = messages.stream().map(GroupChatMessage::getId).toList();

		Map<Long, Long> readCountMap = messageIds.isEmpty() ? Map.of()
				: readRepository.countByMessageIds(messageIds).stream().collect(
						Collectors.toMap(GroupChatReadCountDTO::getMessageId, GroupChatReadCountDTO::getCount));

		// =========================
		// 4. build DTO
		// =========================
		List<ChatMessageResponse> result = messages.stream().map(msg -> {

			UserInfo user = userMap.get(msg.getSenderId());

			ChatMessageResponse dto = new ChatMessageResponse();

			dto.setId(msg.getId());
			dto.setGroupId(msg.getGroupId());
			dto.setSenderId(msg.getSenderId());
			dto.setMessage(msg.getMessage());
			dto.setCreateTime(msg.getCreateTime());
			dto.setImageUrl(msg.getImageUrl());
			dto.setType(msg.getImageUrl() != null ? "IMAGE" : "MESSAGE");

			dto.setReadByMe(readRepository.existsByMessageIdAndUserId(msg.getId(), currentUserId));

			dto.setReplyId(msg.getReplyId());

			// =====================
			// reply message
			// =====================
			if (msg.getReplyId() != null) {

				GroupChatMessage reply = replyMap.get(msg.getReplyId());

				if (reply != null) {

					ChatMessageResponse replyDto = new ChatMessageResponse();

					replyDto.setId(reply.getId());
					replyDto.setMessage(reply.getMessage());
					replyDto.setSenderId(reply.getSenderId());

					UserInfo replyUser = userMap.get(reply.getSenderId());

					if (replyUser != null) {
						replyDto.setSenderName(replyUser.getUserName());
					}

					dto.setReplyMessage(replyDto);
				}
			}

			// =====================
			// sender info
			// =====================
			if (user != null) {

				dto.setSenderName(user.getUserName());

				dto.setSenderAvatar(user.getAvatar());
			}

			// =====================
			// read count
			// =====================
			dto.setReadCount(readCountMap.getOrDefault(msg.getId(), 0L));

			return dto;

		}).toList();

		return ResponseEntity.ok(Map.of("messages", result));
	}

	@PostMapping("/read/{groupId}")
	public void markRead(@PathVariable("groupId") Long groupId, @RequestParam("userId") Long userId) {

		// 1️⃣ 撈群組訊息
		List<GroupChatMessage> messages = repository.findByGroupId(groupId);

		if (messages.isEmpty())
			return;

		// 2️⃣ 取 messageId list
		List<Long> messageIds = messages.stream().map(GroupChatMessage::getId).toList();

		// 3️⃣ 一次查已讀
		List<GroupChatRead> reads = readRepository.findByMessageIdInAndUserId(messageIds, userId);

		Set<Long> readIds = reads.stream().map(GroupChatRead::getMessageId).collect(Collectors.toSet());

		// 4️⃣ 找未讀（排除自己訊息 + 已讀）
		List<GroupChatRead> newReads = new ArrayList<>();

		for (GroupChatMessage msg : messages) {

			// 自己訊息不算已讀
			if (msg.getSenderId().equals(userId)) {
				continue;
			}

			// 已讀 skip
			if (readIds.contains(msg.getId())) {
				continue;
			}

			GroupChatRead read = new GroupChatRead();
			read.setMessageId(msg.getId());
			read.setUserId(userId);
			read.setReadTime(LocalDateTime.now());

			newReads.add(read);
		}

		// 5️⃣ 批次寫入
		if (!newReads.isEmpty()) {
			readRepository.saveAll(newReads);
		}

		// 6️⃣ 一次查 count（避免 N 次 SQL）
		Map<Long, Long> countMap = readRepository.countByMessageIds(messageIds).stream()
				.collect(Collectors.toMap(GroupChatReadCountDTO::getMessageId, GroupChatReadCountDTO::getCount));

		// 7️⃣ 推 WS（只推必要資料）
		for (GroupChatMessage msg : messages) {

			if (msg.getSenderId().equals(userId)) {
				continue;
			}

			Long count = countMap.getOrDefault(msg.getId(), 0L);

			messagingTemplate.convertAndSend("/topic/group/" + groupId,
					Map.of("type", "READ", "messageId", msg.getId(), "readCount", count));
		}
	}

	@PostMapping("/upload")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("groupId") Long groupId,
			@RequestParam("senderId") Long senderId) throws Exception {

		System.out.println("來到聊天圖片上傳了");
		System.out.println("file = " + file.getOriginalFilename());
		System.out.println("groupId = " + groupId);
		System.out.println("senderId = " + senderId);

		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("圖片不可為空");
		}

		// 取得副檔名
		String originalName = file.getOriginalFilename();
		String ext = ".jpg";

		if (originalName != null && originalName.contains(".")) {
			ext = originalName.substring(originalName.lastIndexOf("."));
		}

		String fileName = UUID.randomUUID() + ext;

		// Docker container 裡的資料夾
		Path uploadDir = Paths.get("/app/uploads");

		// 建立資料夾
		Files.createDirectories(uploadDir);

		// 最終檔案路徑
		Path path = uploadDir.resolve(fileName);

		// 只 copy 一次
		Files.copy(file.getInputStream(), path);

		// 1. 存 DB
		GroupChatMessage msg = new GroupChatMessage();
		msg.setGroupId(groupId);
		msg.setSenderId(senderId);
		msg.setImageUrl("/uploads/" + fileName);

		GroupChatMessage saved = repository.save(msg);

		// 2. 查 user
		UserInfo user = userRepository.findById(senderId).orElse(null);

		// 3. 組 WebSocket DTO
		ChatMessageResponse dto = new ChatMessageResponse();

		dto.setId(saved.getId());
		dto.setGroupId(saved.getGroupId());
		dto.setSenderId(saved.getSenderId());
		dto.setImageUrl(saved.getImageUrl());
		dto.setCreateTime(saved.getCreateTime());
		dto.setType("IMAGE");

		if (user != null) {
			dto.setSenderName(user.getUserName());
			dto.setSenderAvatar(user.getAvatar());
		}

		// 4. WebSocket 推播
		messagingTemplate.convertAndSend("/topic/group/" + groupId, dto);

		return ResponseEntity.ok(dto);
	}

	// 收回訊息
	@PostMapping("/message/{id}/recall")
	public ResponseEntity<?> recallMessage(@PathVariable(value = "id") Long id) {

		System.out.println("===== recall start =====");
		System.out.println("messageId = " + id);

		GroupChatMessage msg = repository.findById(id).orElseThrow();

		long seconds = Duration.between(msg.getCreateTime(), LocalDateTime.now()).getSeconds();

		if (seconds > 120) {
			return ResponseEntity.badRequest().body("訊息超過2分鐘無法收回");
		}

		msg.setRecalled(true);

		repository.save(msg);

		messagingTemplate.convertAndSend("/topic/group/" + msg.getGroupId(),
				Map.of("type", "RECALL", "messageId", msg.getId()));

		return ResponseEntity.ok().build();
	}

}