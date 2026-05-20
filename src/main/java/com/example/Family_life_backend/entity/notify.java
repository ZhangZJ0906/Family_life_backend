package com.example.Family_life_backend.entity;

import java.sql.Date;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "notify")
public class notify {

	@Id
	@Column(name = "notify_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "send_user_id")
	private Long sendUserId;
	
	@Column(name = "get_user_id")
	private Long getUserId;
	
	@Column(name = "content")
	private String content;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "is_read")
	private boolean isRead;
	
	@Column(name = "send_date")
	private LocalDateTime sendDate;
	
	@Column(name = "target_group_id")
	private Long targetGroupId;
	
	@Column(name = "status")
	private String status;
	
	@Transient
	private String sender;
	
	@Transient
	private String avatar;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(Long sendUserId) {
		this.sendUserId = sendUserId;
	}

	public Long getGetUserId() {
		return getUserId;
	}

	public void setGetUserId(Long getUserId) {
		this.getUserId = getUserId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public LocalDateTime getSendDate() {
		return sendDate;
	}

	public void setSendDate(LocalDateTime sendDate) {
		this.sendDate = sendDate;
	}

	public Long getTargetGroupId() {
		return targetGroupId;
	}

	public void setTargetGroupId(Long targetGroupId) {
		this.targetGroupId = targetGroupId;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public notify(Long notifyId, Long sendUserId, Long getUserId, String content, String type, boolean isRead, LocalDateTime sendDate, 
			String status, Long targetGroupId, String name, String avatar) {
		super();
		this.id = notifyId;
		this.sendUserId = sendUserId;
		this.getUserId = getUserId;
		this.content = content;
		this.type = type;
		this.isRead = isRead;
		this.sendDate = sendDate;
		this.targetGroupId = targetGroupId;
		this.status = status;
		this.sender = name;
		this.avatar = avatar;
	}

	public notify() {
		super();
		// TODO Auto-generated constructor stub
	}
}
