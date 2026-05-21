package com.example.Family_life_backend.respond;

public class getUserInfoRes extends BasicResponse {

	private Long userId;

	private String name;

	private String Email;

	private String avatar;

	private boolean isNotifyByEndDate;

	private boolean isNotifyByEmail;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return this.Email;
	}

	public void setEmail(String email) {
		this.Email = email;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
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

	public getUserInfoRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public getUserInfoRes(String message, int code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public getUserInfoRes(String message, int code, Long userId, String name, String email, String avatar, boolean isNotifyByEndDate,
			boolean isNotifyByEmail) {
		super(message, code);
		this.userId = userId;
		this.name = name;
		this.Email = email;
		this.avatar = avatar;
		this.isNotifyByEndDate = isNotifyByEndDate;
		this.isNotifyByEmail = isNotifyByEmail;
	}

}
