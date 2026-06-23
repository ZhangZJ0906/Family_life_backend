package com.example.Family_life_backend.Manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.Family_life_backend.DTO.OnlineUserDTO;
import com.example.Family_life_backend.entity.UserInfo;
import com.example.Family_life_backend.repositary.UserRepository;

@Component
public class PresenceManager {

	private static class Presence {
		Long userId;
		Long groupId;
		long lastSeen;
	}

	// sessionId -> Presence
	private final ConcurrentHashMap<String, Presence> sessions = new ConcurrentHashMap<>();

	// cleanup 移除人後立即廣播 ONLINE
	private final SimpMessagingTemplate messagingTemplate;
	private final UserRepository userRepository;

	public PresenceManager(SimpMessagingTemplate messagingTemplate, UserRepository userRepository) {
		this.messagingTemplate = messagingTemplate;
		this.userRepository = userRepository;
	}

	// =========================
	// 1. heartbeat / update
	// =========================
	public void heartbeat(String sessionId, Long userId, Long groupId) {

		Presence p = new Presence();
		p.userId = userId;
		p.groupId = groupId;
		p.lastSeen = System.currentTimeMillis();

		sessions.put(sessionId, p);

		broadcastOnline(groupId);
	}

	// =========================
	// 2. remove session
	// =========================
	public void remove(String sessionId) {
		sessions.remove(sessionId);
	}

	// =========================
	// 3. get online users
	// =========================
	public List<Long> getOnlineUsers(Long groupId) {

		return sessions.values().stream().filter(p -> Objects.equals(p.groupId, groupId)).map(p -> p.userId).distinct()
				.toList();
	}

	// =========================
	// 4. TTL cleanup (核心)
	// =========================
	@Scheduled(fixedRate = 5000)
	public void cleanup() {

		long now = System.currentTimeMillis();
		long timeout = 15000;

		Set<Long> changedGroups = new HashSet<>();

		sessions.entrySet().removeIf(entry -> {

			Presence p = entry.getValue();

			boolean expired = now - p.lastSeen > timeout;

			if (expired) {

				System.out.println("remove user=" + p.userId);

				changedGroups.add(p.groupId);
			}

			return expired;
		});

		changedGroups.forEach(this::broadcastOnline);
	}

	private void broadcastOnline(Long groupId) {

		List<Long> userIds = getOnlineUsers(groupId);

		List<UserInfo> users = userRepository.findAllById(userIds);

		List<OnlineUserDTO> userList = users.stream()
				.map(u -> new OnlineUserDTO((long) u.getUserId(), u.getUserName(), u.getAvatar())).toList();

		messagingTemplate.convertAndSend("/topic/group/" + groupId,
				Map.of("type", "ONLINE", "count", userList.size(), "users", userList));

		System.out.println("broadcast group=" + groupId + " online=" + userList.size());
	}
}