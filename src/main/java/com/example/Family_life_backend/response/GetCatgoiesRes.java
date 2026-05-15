package com.example.Family_life_backend.response;

import java.util.Map;

public class GetCatgoiesRes extends BasicRes {
	private Map<Integer, String> categoiesMap;




	public GetCatgoiesRes(String message, int code, Map<Integer, String> categoiesMap) {
		super(message, code);
		this.categoiesMap = categoiesMap;
	}

	public Map<Integer, String> getCategoiesMap() {
		return categoiesMap;
	}

	public void setCategoiesMap(Map<Integer, String> categoiesMap) {
		this.categoiesMap = categoiesMap;
	}

	public GetCatgoiesRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetCatgoiesRes(String message, int code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

}
