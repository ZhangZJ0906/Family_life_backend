package com.example.Family_life_backend.Manager;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class WsSessionManager {

	// sessionId -> userId
	private final ConcurrentHashMap<String, Long> sessionUserMap = new ConcurrentHashMap<>();

	// groupId -> sessionIds
	private final ConcurrentHashMap<Long, Set<String>> groupSessions = new ConcurrentHashMap<>();

	public void addSession(Long groupId, String sessionId, Long userId) {

		sessionUserMap.put(sessionId, userId);

		groupSessions.computeIfAbsent(groupId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
	}

	public void removeSession(String sessionId) {

		Long userId = sessionUserMap.remove(sessionId);

		groupSessions.forEach((groupId, sessions) -> {
			sessions.remove(sessionId);
		});
	}

	public List<Long> getOnlineUsers(Long groupId) {

		Set<String> sessions = groupSessions.getOrDefault(groupId, Set.of());

		return sessions.stream().map(sessionUserMap::get).filter(Objects::nonNull).distinct().toList();
	}
}
