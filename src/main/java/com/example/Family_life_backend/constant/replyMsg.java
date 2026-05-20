package com.example.Family_life_backend.constant;

public enum replyMsg {
	SUCCESS(200 , "success"), 
	EMAIL_ERROR(400, "email error!"), 
	NAME_ERROR(400, "name error!"),
	AGE_ERROR(400, "age error!"),
	EMAIL_NOTEXIST(404, "email not exist!"),
	STRAT_DATE_IS_AFTER_ENDDATE(400, "Start date must before End date!"),
	STRAT_DATE_IS_AFTER_TODAY(400, "Start date is after today"),
	TYPE_ERROR(400, "Type Error"),
	OPTIONS_IS_EMPTY(400, "Options is empty"),
	OPTIONS_PARSER_ERROR(400, "Options transfer error"),
	QUIZ_ID_ERROR(400, "Quiz id error"),
	QUIZ_ID_MISMATCH(400, "Quiz id mismatch"),
	QUIZ_NOT_FOUND(404, "quiz not found"),
	ANSWER_IS_REQUIRED(404, "answer is required"),
	ONLY_ONE_ANS_ALLOWED(400, "only one answer allowed"),
	OPTION_ANSWER_MISMATCH(400, "options answers mismatch"),
	ANSWERS_PARSER_ERROR(400, "answer parser error"),
	QUIZ_UPDATE_NOT_ALLOWED(400, "quiz update not allowed"),
	USER_ID_EXIST(400, "this id is exist in this group"),
	USER_ID_NOT_EXIST(404, "user_id not exist"),
	GROUP_NOT_EXIST(404, "group not exist"),
	MEMBER_IS_INVITED(400, "member is invited");
	private int code;
	
	private String message;

	private replyMsg(int code, String message) {
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
