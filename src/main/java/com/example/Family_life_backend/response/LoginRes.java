package com.example.Family_life_backend.response;

public class LoginRes extends BasicRes {

	private UserInfoRes user;

	public LoginRes() {
		super();
	}

	public LoginRes(int code, String message) {
		super(code, message);
	}

	public LoginRes(int code, String message, UserInfoRes user) {
		super(code, message);
		this.user = user;
	}

	public UserInfoRes getUser() {
		return user;
	}

	public void setUser(UserInfoRes user) {
		this.user = user;
	}
}
