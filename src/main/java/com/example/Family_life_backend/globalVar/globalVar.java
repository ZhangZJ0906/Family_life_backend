package com.example.Family_life_backend.globalVar;

import org.springframework.stereotype.Component;

@Component
public class globalVar {
//	String Url = "https://labels-biz-sheep-concerning.trycloudflare.com/uploads/";
	String Url = "http://localhost:8080/uploads/";

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}

}
