package com.example.Family_life_backend.request;

import java.util.ArrayList;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class DeleteExpensesReq {
	@NotEmpty(message = "消費紀錄ID不可為空")
	ArrayList<Integer> id;
	@NotNull(message = "群組ID不可為空")
	Long groupId;
	@NotNull(message = "使用者ID不可為空")
	Long userId;

	public ArrayList<Integer> getId() {
		return id;
	}

	public void setId(ArrayList<Integer> id) {
		this.id = id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public DeleteExpensesReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DeleteExpensesReq(ArrayList<Integer> id, Long groupId, Long userId) {
		super();
		this.id = id;
		this.groupId = groupId;
		this.userId = userId;
	}

}
