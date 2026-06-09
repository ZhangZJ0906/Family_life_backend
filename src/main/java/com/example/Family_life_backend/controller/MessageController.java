package com.example.Family_life_backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Family_life_backend.repositary.GroupChatRepository;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

	private final GroupChatRepository repository;

	public MessageController(GroupChatRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/{groupId}")
	public ResponseEntity<?> getMessages(@PathVariable("groupId") Long groupId) {

		return ResponseEntity.ok(Map.of("messages", repository.findByGroupIdOrderByCreateTimeAsc(groupId)));
	}
}