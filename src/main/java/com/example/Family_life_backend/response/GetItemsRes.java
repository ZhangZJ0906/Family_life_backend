package com.example.Family_life_backend.response;

import java.util.List;
import java.util.Map;

import com.example.Family_life_backend.enity.Items;

public class GetItemsRes extends BasicRes {
	public List<Items> items;
	public Map<Integer, String> locationMap;
	public Map<Integer, String> categoriesMap;

	public List<Items> getItems() {
		return items;
	}

	public GetItemsRes(String message, int code, List<Items> items, Map<Integer, String> locationMap,
			Map<Integer, String> categoriesMap) {
		super(message, code);
		this.items = items;
		this.locationMap = locationMap;
		this.categoriesMap = categoriesMap;
	}

	public Map<Integer, String> getCategoriesMap() {
		return categoriesMap;
	}

	public void setCategoriesMap(Map<Integer, String> categoriesMap) {
		this.categoriesMap = categoriesMap;
	}

	public void setItems(List<Items> items) {
		this.items = items;
	}

	public GetItemsRes() {
		super();
		// TODO Auto-generated constructor stub
	}


	public GetItemsRes(String message, int code, List<Items> items, Map<Integer, String> locationMap) {
		super(message, code);
		this.items = items;
		this.locationMap = locationMap;
	}

	public Map<Integer, String> getLocationMap() {
		return locationMap;
	}

	public void setLocationMap(Map<Integer, String> locationMap) {
		this.locationMap = locationMap;
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
