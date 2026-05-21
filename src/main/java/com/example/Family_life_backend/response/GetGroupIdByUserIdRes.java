package com.example.Family_life_backend.response;

import java.util.Map;

public class GetGroupIdByUserIdRes extends BasicResponse {
	private Map<Long, String> groupIdList;



	public Map<Long, String> getGroupIdList() {
		return groupIdList;
	}

	public void setGroupIdList(Map<Long, String> groupIdList) {
		this.groupIdList = groupIdList;
	}

	public GetGroupIdByUserIdRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetGroupIdByUserIdRes(String message, int code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public GetGroupIdByUserIdRes(String message, int code, Map<Long, String> groupIdList) {
		super(message, code);
		this.groupIdList = groupIdList;
	}





}
