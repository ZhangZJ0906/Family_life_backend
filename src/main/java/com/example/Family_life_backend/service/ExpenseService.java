package com.example.Family_life_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.dao.ExpenseDao;

@Service
public class ExpenseService {
	@Autowired
	private ExpenseDao expenseDao;

	public void addExpenseInfo() {

	}
}
