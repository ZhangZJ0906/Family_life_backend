package com.example.Family_life_backend.response;

import java.util.List;
import java.util.Map;

import com.example.Family_life_backend.entity.Expense;
import com.example.Family_life_backend.entity.Items;

public class GetExpenseInfoRes extends BasicRes {
	private List<Expense> list;
	private Map<Long, Items> itemMap;
	public List<Expense> getList() {
		return list;
	}

	public void setList(List<Expense> list) {
		this.list = list;
	}


	public Map<Long, Items> getItemMap() {
		return itemMap;
	}

	public void setItemMap(Map<Long, Items> itemMap) {
		this.itemMap = itemMap;
	}

	public GetExpenseInfoRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetExpenseInfoRes(String message, int code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public GetExpenseInfoRes(String message, int code, List<Expense> list, Map<Long, Items> itemMap) {
		super(message, code);
		this.list = list;
		this.itemMap = itemMap;
	}

}
