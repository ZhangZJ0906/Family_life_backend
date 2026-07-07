package com.example.Family_life_backend.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
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
	
	private static final int DEFAULT_MESSAGE_LIMIT = 10;
	
	private static final int MAX_MESSAGE_LIMIT = 20;
	
	private static final int DEFAULT_READ_LIMIT = 40;
	
	private static final int MAX_READ_LIMIT = 200;

	public MessageController(GroupChatRepository repository, UserRepository userRepository,
			GroupChatReadRepository readRepository, SimpMessagingTemplate messagingTemplate) {

		this.repository = repository;
		this.userRepository = userRepository;
		this.readRepository = readRepository;
		this.messagingTemplate = messagingTemplate;
	}

	@GetMapping("/{groupId}")
	public ResponseEntity<?> getMessages(
	        @PathVariable("groupId") Long groupId,
	        @RequestParam("userId") Long userId,
	        @RequestParam(value = "beforeId", required = false) Long beforeId,
	        @RequestParam(value = "limit", required = false) Integer limit) {

		int pageSize = normalizeLimit(limit, DEFAULT_MESSAGE_LIMIT, MAX_MESSAGE_LIMIT);

	    // 多查 1 筆是為了判斷 hasMore。
	    // 例如前端要 50 筆，後端查 51 筆；如果真的有第 51 筆，就代表還有下一頁。
	    List<GroupChatMessage> messagesDesc = repository.findPageBeforeId(
	            groupId,
	            beforeId,
	            PageRequest.of(0, pageSize + 1)
	    );

	    boolean hasMore = messagesDesc.size() > pageSize;

	    if (hasMore) {
	        messagesDesc = messagesDesc.subList(0, pageSize);
	    }

	    // DB 查出來是 id DESC，也就是新到舊。
	    // 前端聊天室通常需要舊到新，所以這裡反轉。
	    List<GroupChatMessage> messages = new ArrayList<>(messagesDesc);
	    Collections.reverse(messages);

	    // 下一頁游標：目前這頁最舊的訊息 id。
	    Long nextBeforeId = messages.isEmpty() ? null : messages.get(0).getId();

		Long currentUserId = userId;
		// =========================
		// 1. batch users
		// =========================
		List<Long> senderIds = messages.stream().map(GroupChatMessage::getSenderId).distinct().toList();

		Map<Long, UserInfo> userMap = userRepository.findAllById(senderIds).stream()
				.collect(Collectors.toMap(u -> (long) u.getUserId(), Function.identity()));

		// =========================
		// 2. readByMe batch（🔥重點）
		// =========================
		List<Long> messageIds = messages.stream().map(GroupChatMessage::getId).toList();

		List<GroupChatRead> myReads = readRepository.findByMessageIdInAndUserId(messageIds, userId);

		Set<Long> readSet = myReads.stream().map(GroupChatRead::getMessageId).collect(Collectors.toSet());

		// 新增：一次查出每則訊息的已讀人數
		Map<Long, Long> readCountMap;

		if (messageIds.isEmpty()) {
			readCountMap = Map.of();
		} else {
			readCountMap = readRepository.countByMessageIds(messageIds)
					.stream()
					.collect(Collectors.toMap(
							GroupChatReadCountDTO::getMessageId,
							GroupChatReadCountDTO::getCount
					));
		}

		// =========================
		// 2. batch reply messages
		// =========================
		List<Long> replyIds = messages.stream().map(GroupChatMessage::getReplyId).filter(Objects::nonNull).distinct()
				.toList();

		Map<Long, GroupChatMessage> replyMap = replyIds.isEmpty() ? Map.of()
				: repository.findAllById(replyIds).stream()
						.collect(Collectors.toMap(GroupChatMessage::getId, Function.identity()));

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

			dto.setReadByMe(readSet.contains(msg.getId()));
			dto.setReadCount(
			readCountMap.getOrDefault(msg.getId(), 0L)
	);

			dto.setReplyId(msg.getReplyId());
			System.out.println("id: " + msg.getId() + "isrecall: " + msg.getRecalled());
			dto.setRecalled(msg.getRecalled());

			// =====================
//			// sender info
//			// =====================
			if (user != null) {

				dto.setSenderName(user.getUserName());

				dto.setSenderAvatar(user.getAvatar());
			}

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

//			// =====================
//			// read count
//			// =====================
			

			return dto;

		}).toList();

		Map<String, Object> response = new HashMap<>();
		response.put("messages", result);
		response.put("hasMore", hasMore);
		response.put("nextBeforeId", nextBeforeId);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/read/{groupId}")
		public void markRead(
				@PathVariable("groupId") Long groupId,
				@RequestParam("userId") Long userId,
				@RequestParam(value = "upToMessageId", required = false) Long upToMessageId,
				@RequestParam(value = "limit", required = false) Integer limit) {

			System.out.println("===== mark read =====");
			System.out.println("groupId = " + groupId);
			System.out.println("userId = " + userId);
			System.out.println("upToMessageId = " + upToMessageId);

			int readLimit =
					normalizeLimit(limit, DEFAULT_READ_LIMIT, MAX_READ_LIMIT);

			List<GroupChatMessage> messages =
					repository.findUnreadMessagesForUser(
							groupId,
							userId,
							upToMessageId,
							PageRequest.of(0, readLimit)
					);

			System.out.println("unread messages count = " + messages.size());

			if (messages.isEmpty()) {
				return;
			}

			List<GroupChatRead> newReads = new ArrayList<>();

			for (GroupChatMessage msg : messages) {

				boolean alreadyRead =
						readRepository.existsByMessageIdAndUserId(
								msg.getId(),
								userId
						);

				if (alreadyRead) {
					continue;
				}

				GroupChatRead read = new GroupChatRead();
				read.setMessageId(msg.getId());
				read.setUserId(userId);
				read.setReadTime(
						LocalDateTime.now(
								ZoneId.of("Asia/Taipei")
						)
				);

				newReads.add(read);
			}

			if (newReads.isEmpty()) {
				return;
			}

			readRepository.saveAll(newReads);
			readRepository.flush();

			System.out.println("saved reads count = " + newReads.size());

			List<Long> messageIds = newReads.stream()
					.map(GroupChatRead::getMessageId)
					.toList();

			Map<Long, Long> countMap =
					readRepository.countByMessageIds(messageIds)
							.stream()
							.collect(Collectors.toMap(
									GroupChatReadCountDTO::getMessageId,
									GroupChatReadCountDTO::getCount
							));

			for (Long messageId : messageIds) {
				Long count = countMap.getOrDefault(messageId, 0L);

				messagingTemplate.convertAndSend(
						"/topic/group/" + groupId,
						Map.of(
								"type", "READ",
								"messageId", messageId,
								"readCount", count
						)
				);
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

		if (seconds > 86400) {
			return ResponseEntity.badRequest().body("訊息超過一天無法收回");
		}

		msg.setRecalled(true);

		repository.save(msg);

		messagingTemplate.convertAndSend("/topic/group/" + msg.getGroupId(),
				Map.of("type", "RECALL", "messageId", msg.getId()));

		return ResponseEntity.ok().build();
	}
	
	//避免一次撈太多聊天訊息
	private int normalizeLimit(Integer limit, int defaultLimit, int maxLimit) {
	    if (limit == null || limit <= 0) {
	        return defaultLimit;
	    }
	    return Math.min(limit, maxLimit);
	}

}