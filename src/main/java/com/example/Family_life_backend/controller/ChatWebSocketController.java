package com.example.Family_life_backend.controller;

import com.example.Family_life_backend.entity.GroupChatMessage;
import com.example.Family_life_backend.repositary.GroupChatRepository;
import com.example.Family_life_backend.request.ChatRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private GroupChatRepository repository;

    @MessageMapping("/chat.send")  // /app/chat.send
    public void send(ChatRequest request) {

        GroupChatMessage msg = new GroupChatMessage();

        msg.setGroupId(request.getGroupId());
        msg.setSenderId(request.getSenderId());
        msg.setMessage(request.getMessage());
        msg.setCreateTime(LocalDateTime.now());

        // 存 DB
        repository.save(msg);

        // 🔥 推送給該群組所有人
        messagingTemplate.convertAndSend(
            "/topic/group/" + request.getGroupId(),
            msg
        );
    }
}