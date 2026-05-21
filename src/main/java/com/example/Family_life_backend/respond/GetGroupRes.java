package com.example.Family_life_backend.respond;

import java.util.List;

import com.example.Family_life_backend.entity.group;

public class GetGroupRes extends BasicResponse{
	private List<group> groupList;
	
	private List<Integer> publicInventory;

	public List<group> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<group> groupList) {
		this.groupList = groupList;
	}

	public List<Integer> getPublicInventory() {
		return publicInventory;
	}

	public void setPublicInventory(List<Integer> publicInventory) {
		this.publicInventory = publicInventory;
	}

	public GetGroupRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetGroupRes(String message, int code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public GetGroupRes(String message, int code, List<group> groupList, List<Integer> publicInventory) {
		super(message, code);
		this.groupList = groupList;
		this.publicInventory = publicInventory;
	}
	
	
}
