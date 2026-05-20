package com.example.Family_life_backend.response;

import java.util.List;

import com.example.Family_life_backend.entity.group;

public class GetGroupRes extends BasicResponse{
	private List<group> groupList;

	public List<group> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<group> groupList) {
		this.groupList = groupList;
	}
	

	public GetGroupRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetGroupRes(String message, int code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public GetGroupRes(String message, int code, List<group> groupList) {
		super(message, code);
		this.groupList = groupList;
	}
	
	
}
