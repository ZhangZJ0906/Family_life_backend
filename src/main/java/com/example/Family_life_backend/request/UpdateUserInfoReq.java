package com.example.Family_life_backend.request;

import com.example.Family_life_backend.constants.ValidMessage;

import jakarta.validation.constraints.Min;

public class UpdateUserInfoReq {

	@Min(value = 1, message = ValidMessage.USER_ID_ERROR)
	private int userId;

	private String userName;

	private String avatar;
	
	private String email;

	private boolean isNotifyByEndDate;

	private boolean isNotifyByEmail;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isNotifyByEndDate() {
		return isNotifyByEndDate;
	}

	public void setNotifyByEndDate(boolean isNotifyByEndDate) {
		this.isNotifyByEndDate = isNotifyByEndDate;
	}

	public boolean isNotifyByEmail() {
		return isNotifyByEmail;
	}

	public void setNotifyByEmail(boolean isNotifyByEmail) {
		this.isNotifyByEmail = isNotifyByEmail;
	}

}
