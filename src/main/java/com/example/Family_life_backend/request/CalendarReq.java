package com.example.Family_life_backend.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CalendarReq {

	private Long groupId;
	@NotNull(message = "createdBy 不可為空")
	private Long createdBy;
	@NotBlank(message = "活動名稱不可為空")
	private String title;
	
	private String description;
	@NotNull(message = "活動時間不可為空")
	private LocalDateTime eventTime;
	
	private LocalDateTime endTime;
	
	private Integer notifyBefore;

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
	
	public LocalDateTime getEndTime() {
	    return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
	    this.endTime = endTime;
	}

	public Integer getNotifyBefore() {
		return notifyBefore;
	}

	public void setNotifyBefore(Integer notifyBefore) {
		this.notifyBefore = notifyBefore;
	}
}
