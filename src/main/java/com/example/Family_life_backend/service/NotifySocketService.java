package com.example.Family_life_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotifySocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void pushUnreadCount(Long userId, int count) {

        messagingTemplate.convertAndSend(
                "/topic/notify/" + userId,
                Map.of(
                        "type", "UNREAD_COUNT",
                        "count", count
                )
        );
    }
}