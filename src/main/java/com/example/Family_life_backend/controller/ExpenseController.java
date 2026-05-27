package com.example.Family_life_backend.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Family_life_backend.request.AddExpensesInfoReq;
import com.example.Family_life_backend.request.UpdateExpensesInfoReq;
import com.example.Family_life_backend.response.BasicRes;
import com.example.Family_life_backend.response.GetExpenseInfoRes;
import com.example.Family_life_backend.service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/expense")
@CrossOrigin(origins = "http://localhost:4200")
public class ExpenseController {
	@Autowired
	private ExpenseService expenseService;

//可查 私人 或是群組
	@GetMapping("/getInfo")
	public GetExpenseInfoRes getExpenInfo(@RequestParam(value = "groupId") Long groupId,
			@RequestParam(value = "userId", required = false) Long userId) {

		return expenseService.getExpenseInfo(groupId, userId);
	}

	@PostMapping("/addInfo")
	public BasicRes addExpenInfo(@Valid @RequestBody AddExpensesInfoReq req) {

		return expenseService.addExpenseInfo(req);
	}

	@PostMapping("/updateInfo")
	public BasicRes updateExpenInfo(@Valid @RequestBody UpdateExpensesInfoReq req) {

		return expenseService.updateExpenseInfo(req);
	}

	@PostMapping("/deleteInfo")
	public BasicRes deleteExpenseInfo(@RequestBody ArrayList<Integer> id) {

		return expenseService.deleteExpenseInfo(id);
	}

}
