package com.example.Family_life_backend.response;

public class CalendarRes {

	private int code;
    private String message;
    private Object data;

    public CalendarRes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public CalendarRes(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
