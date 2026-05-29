package com.example.Family_life_backend.request;

import com.example.Family_life_backend.entity.GroupMembers;

import jakarta.validation.Valid;

public class groupMemberReq extends GroupMembers {
	private Long sendUserId;

	private String Email;

	public Long getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(Long sendUserId) {
		this.sendUserId = sendUserId;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

}
