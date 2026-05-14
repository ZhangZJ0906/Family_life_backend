package com.example.Family_life_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Family_life_backend.service.ExpenseService;

@RestController
@RequestMapping(value = "/expense")
@CrossOrigin(origins = "http://localhost:4200")
public class ExpenseController {
	@Autowired
	private ExpenseService expenseService;



}
