package com.example.Family_life_backend.globalVar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class globalVar {
//	String Url = "https://labels-biz-sheep-concerning.trycloudflare.com/uploads/";
	String Url;

	public String getUrl() {
		return "http://localhost:8081/uploads/";
	}

	public void setUrl(String url) {
		Url = url;
	}

}
