package com.example.Family_life_backend.EventListener;

import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.Family_life_backend.Registry.OnlineUserRegistry;

@Component
public class WebSocketEventListener {

	private final SimpMessagingTemplate messagingTemplate;
	private final OnlineUserRegistry registry;

	public WebSocketEventListener(SimpMessagingTemplate messagingTemplate, OnlineUserRegistry registry) {

		this.messagingTemplate = messagingTemplate;
		this.registry = registry;
	}

	@EventListener
	public void handleConnect(SessionConnectEvent event) {

		String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();

		registry.add(sessionId);

		messagingTemplate.convertAndSend("/topic/group/global", Map.of("type", "ONLINE", "count", registry.count()));
	}

	@EventListener
	public void handleDisconnect(SessionDisconnectEvent event) {

		String sessionId = event.getSessionId();

		registry.remove(sessionId);

		messagingTemplate.convertAndSend("/topic/group/global", Map.of("type", "ONLINE", "count", registry.count()));
	}
}