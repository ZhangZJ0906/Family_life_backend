package com.example.Family_life_backend.service;



import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

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