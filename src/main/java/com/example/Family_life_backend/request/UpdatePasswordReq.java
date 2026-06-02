package com.example.Family_life_backend.request;

public class UpdatePasswordReq {
	private String email;
	private String password;

	public UpdatePasswordReq(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public UpdatePasswordReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	// Getters and Setters
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
