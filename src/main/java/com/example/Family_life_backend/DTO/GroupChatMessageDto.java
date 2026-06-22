package com.example.Family_life_backend.DTO;

import java.time.LocalDateTime;

public class GroupChatMessageDto {

	private Long id;
	private Long groupId;
	private Long senderId;
	private String senderName;
	private String senderAvatar;
	private String message;
	private LocalDateTime createTime;
	private String imageUrl;
	private Boolean recalled;
	private Long replyId;

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

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderAvatar() {
		return senderAvatar;
	}

	public void setSenderAvatar(String senderAvatar) {
		this.senderAvatar = senderAvatar;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Boolean getRecalled() {
		return recalled;
	}

	public void setRecalled(Boolean recalled) {
		this.recalled = recalled;
	}

	public Long getReplyId() {
		return replyId;
	}

	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}

	public GroupChatMessageDto(Long id, Long groupId, Long senderId, String senderName, String senderAvatar,
			String message, LocalDateTime createTime, String imageUrl, Boolean recalled, Long replyId) {
		this.id = id;
		this.groupId = groupId;
		this.senderId = senderId;
		this.senderName = senderName;
		this.senderAvatar = senderAvatar;
		this.message = message;
		this.createTime = createTime;
		this.imageUrl = imageUrl;
		this.recalled = recalled;
		this.replyId = replyId;
	}

	// getter / setter
}