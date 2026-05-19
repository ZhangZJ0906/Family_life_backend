package com.example.Family_life_backend.respond;

import java.util.List;

import com.example.Family_life_backend.DTO.UserNotifyDTO;
import com.example.Family_life_backend.entity.notify;

public class getNotifyRes extends BasicResponse {

	private List<UserNotifyDTO> notifies;

	public List<UserNotifyDTO> getNotifies() {
		return notifies;
	}

	public void setNotifies(List<UserNotifyDTO> notifies) {
		this.notifies = notifies;
	}

	public getNotifyRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public getNotifyRes(String message, int code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public getNotifyRes(String message, int code, List<UserNotifyDTO> notifies) {
		super(message, code);
		this.notifies = notifies;
	}

}