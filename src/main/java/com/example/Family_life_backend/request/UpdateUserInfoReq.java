package com.example.Family_life_backend.request;

import com.example.Family_life_backend.constants.ValidMessage;

import jakarta.validation.constraints.Min;

public class UpdateUserInfoReq {

	@Min(value = 1, message = ValidMessage.USER_ID_ERROR)
	private int userId;

	private String userName;

	private String avatar;

	private boolean notify;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public boolean isNotify() {
		return notify;
	}

	public void setNotify(boolean notify) {
		this.notify = notify;
	}

}
