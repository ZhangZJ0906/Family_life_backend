package com.example.Family_life_backend.respond;

public class BasicResponse {
	private String message;

	private int code;

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

	public BasicResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BasicResponse(String message, int code) {
		super();
		this.message = message;
		this.code = code;
	}

}
