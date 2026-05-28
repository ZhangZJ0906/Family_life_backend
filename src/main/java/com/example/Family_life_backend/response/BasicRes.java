package com.example.Family_life_backend.response;

public class BasicRes {

	private String message;

	private int code;

	public BasicRes(String message, int code) {
		super();
		this.message = message;
		this.code = code;
	}

	public BasicRes(int code, String message) {
		super();
		this.message = message;
		this.code = code;
	}

	public BasicRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
