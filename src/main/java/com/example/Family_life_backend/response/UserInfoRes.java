package com.example.Family_life_backend.response;

import java.time.LocalDate;

import com.example.Family_life_backend.entity.UserInfo;

public class UserInfoRes {

	private int userId;

	private String userName;

	private String email;

	private String avatar;

	private boolean notify;

	private LocalDate createdDate;

	private LocalDate updateDate;

	public UserInfoRes() {
		super();
	}

	public UserInfoRes(UserInfo userInfo) {
		this.userId = userInfo.getUserId();
		this.userName = userInfo.getUserName();
		this.email = userInfo.getEmail();
		this.avatar = userInfo.getAvatar();
		this.notify = userInfo.isNotify();
		this.createdDate = userInfo.getCreatedDate();
		this.updateDate = userInfo.getUpdateDate();
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDate getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(LocalDate updateDate) {
		this.updateDate = updateDate;
	}
}
