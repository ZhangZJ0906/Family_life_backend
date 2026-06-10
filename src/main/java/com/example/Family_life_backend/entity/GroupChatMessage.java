package com.example.Family_life_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "group_chat_message")
public class GroupChatMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "group_id")
	private Long groupId;

	@Column(name = "sender_id")
	private Long senderId;

	@Column(name = "message")
	private String message;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "createTime")
	private LocalDateTime createTime = LocalDateTime.now();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public GroupChatMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GroupChatMessage(Long id, Long groupId, Long senderId, String message, String imageUrl,
			LocalDateTime createTime) {
		super();
		this.id = id;
		this.groupId = groupId;
		this.senderId = senderId;
		this.message = message;
		this.imageUrl = imageUrl;
		this.createTime = createTime;
	}

}
