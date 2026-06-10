package com.example.Family_life_backend.response;

import java.time.LocalDateTime;

public class ChatMessageResponse extends BasicResponse {

	private Long id;

	private Long groupId;

	private Long senderId;

	private String senderName;

	private String senderAvatar;

	private String message;

	private LocalDateTime createTime;

	private String imageUrl;

	private String type; // ⭐ 新增

	private Long readCount;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getReadCount() {
		return readCount;
	}

	public void setReadCount(Long readCount) {
		this.readCount = readCount;
	}

	public ChatMessageResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ChatMessageResponse(String message, int code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public ChatMessageResponse(Long id, Long groupId, Long senderId, String senderName, String senderAvatar,
			String message, LocalDateTime createTime, String imageUrl, String type, Long readCount) {
		super();
		this.id = id;
		this.groupId = groupId;
		this.senderId = senderId;
		this.senderName = senderName;
		this.senderAvatar = senderAvatar;
		this.message = message;
		this.createTime = createTime;
		this.imageUrl = imageUrl;
		this.type = type;
		this.readCount = readCount;
	}

}
