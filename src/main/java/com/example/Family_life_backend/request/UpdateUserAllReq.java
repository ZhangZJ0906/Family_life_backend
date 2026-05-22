package com.example.Family_life_backend.request;

import java.util.List;

import com.example.Family_life_backend.entity.PublicInventoryItem;

public class UpdateUserAllReq {

	private UpdateUserInfoReq userInfo;

	private List<PublicInventoryItem> publicInventoryList;

	public UpdateUserInfoReq getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UpdateUserInfoReq userInfo) {
		this.userInfo = userInfo;
	}

	public List<PublicInventoryItem> getPublicInventoryList() {
		return publicInventoryList;
	}

	public void setPublicInventoryList(List<PublicInventoryItem> publicInventoryList) {
		this.publicInventoryList = publicInventoryList;
	}

}