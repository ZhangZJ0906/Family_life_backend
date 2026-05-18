package com.example.Family_life_backend.constants;

public enum ReplyMessage {

	SUCCESS (200, "Success"),//
	EMAIL_ERROR (400, "Email Error"),//
	EMAIL_NOT_FOUND (404, "Email not found"),//
	EMAIL_EXISTS (400, "Email already exists"),//
	USER_ID_ERROR (400, "User id Error"),//
	USER_NOT_FOUND (404, "User not found"),//
	PASSWORD_ERROR (400, "Password Error"),//
	OLD_PASSWORD_ERROR (400, "Old password Error"),//
	USER_INFO_ERROR (400, "User info Error"),//
	TITLE_ERROR (400, "Title Error"),//
	CREATOR_ID_ERROR (400, "Creator id Error"),//
	PURCHASE_ITEM_ERROR (400, "Purchase item Error");//

	private int code;

	private String message;

	private ReplyMessage(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
	
}
