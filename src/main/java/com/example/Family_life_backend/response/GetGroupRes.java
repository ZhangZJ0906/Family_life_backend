package com.example.Family_life_backend.response;

import java.util.List;

import com.example.Family_life_backend.DTO.groupDTO;

public class GetGroupRes extends BasicResponse {
	private List<groupDTO> groupList;

	private List<Integer> publicInventory;

//	private List<String> creater;

	public List<groupDTO> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<groupDTO> groupList) {
		this.groupList = groupList;
	}

	public List<Integer> getPublicInventory() {
		return publicInventory;
	}

	public void setPublicInventory(List<Integer> publicInventory) {
		this.publicInventory = publicInventory;
	}

//	public List<String> getCreater() {
//		return creater;
//	}
//
//	public void setCreater(List<String> creater) {
//		this.creater = creater;
//	}

	public GetGroupRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetGroupRes(String message, int code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public GetGroupRes(String message, int code, List<groupDTO> groupList, List<Integer> publicInventory) {
		super(message, code);
		this.groupList = groupList;
		this.publicInventory = publicInventory;
//		this.creater = creater;
	}

}