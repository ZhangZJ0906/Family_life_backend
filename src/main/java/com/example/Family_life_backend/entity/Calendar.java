package com.example.Family_life_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "calendar_events")
public class Calendar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "event_batch_id")
	private String eventBatchId;

	@Column(name = "group_id")
	private Long groupId;

	@Column(name = "created_by")
	private Long createdBy;

	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "event_time")
	private LocalDateTime eventTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	@Column(name = "notify_before")
	private Integer notifyBefore;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "assigned_user_id")
	private Long assignedUserId;

	@Column(name = "is_send_before_notify", columnDefinition = "TINYINT(1) DEFAULT 0")
	private Long isSendBeforeNotify;

	@Column(name = "is_send_start_notify", columnDefinition = "TINYINT(1) DEFAULT 0")
	private Long isSendStartNotify;

	@PrePersist
	public void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public String getEventBatchId() {
		return eventBatchId;
	}

	public void setEventBatchId(String eventBatchId) {
		this.eventBatchId = eventBatchId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getEventTime() {
		return eventTime;
	}

	public void setEventTime(LocalDateTime eventTime) {
		this.eventTime = eventTime;
	}

	public Integer getNotifyBefore() {
		return notifyBefore;
	}

	public void setNotifyBefore(Integer notifyBefore) {
		this.notifyBefore = notifyBefore;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public Long getAssignedUserId() {
		return assignedUserId;
	}

	public void setAssignedUserId(Long assignedUserId) {
		this.assignedUserId = assignedUserId;
	}

}
