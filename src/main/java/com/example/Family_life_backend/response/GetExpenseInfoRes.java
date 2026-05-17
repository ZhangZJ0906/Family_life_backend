package com.example.Family_life_backend.response;

import java.util.List;

import com.example.Family_life_backend.enity.Expense;

public class GetExpenseInfoRes extends BasicRes {
	private List<Expense> list;

	public List<Expense> getList() {
		return list;
	}

	public void setList(List<Expense> list) {
		this.list = list;
	}

	public GetExpenseInfoRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetExpenseInfoRes(String message, int code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public GetExpenseInfoRes(String message, int code, List<Expense> list) {
		super(message, code);
		this.list = list;
	}

}
