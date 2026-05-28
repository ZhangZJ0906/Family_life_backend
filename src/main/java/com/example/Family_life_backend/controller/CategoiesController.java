package com.example.Family_life_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Family_life_backend.response.GetCatgoiesRes;
import com.example.Family_life_backend.service.CategoiesServices;

@RestController
@RequestMapping(value = "/categories")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoiesController {
	
	@Autowired
	private CategoiesServices categoiesServices;
	
	@GetMapping(value = "/get")
	public GetCatgoiesRes getAll() {
		return categoiesServices.getall();
	}
	
}
