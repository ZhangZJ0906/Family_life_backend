package com.example.Family_life_backend.response;

import java.util.List;

import com.example.Family_life_backend.enity.Items;

public class GetItemsRes extends BasicRes {
	public List<Items> items;

	public List<Items> getItems() {
		return items;
	}

	public void setItems(List<Items> items) {
		this.items = items;
	}

	public GetItemsRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetItemsRes(String message, int code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public GetItemsRes(String message, int code, List<Items> items) {
		super(message, code);
		this.items = items;
	}

}
