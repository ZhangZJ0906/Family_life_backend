package com.example.Family_life_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_chat_read")
public class GroupChatRead {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "message_id")
	private Long messageId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "read_time")
	private LocalDateTime readTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public LocalDateTime getReadTime() {
		return readTime;
	}

	public void setReadTime(LocalDateTime readTime) {
		this.readTime = readTime;
	}

	public GroupChatRead() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GroupChatRead(Long id, Long messageId, Long userId, LocalDateTime readTime) {
		super();
		this.id = id;
		this.messageId = messageId;
		this.userId = userId;
		this.readTime = readTime;
	}

}