package com.example.Family_life_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.dao.ExpenseDao;
import com.example.Family_life_backend.enity.Expense;
import com.example.Family_life_backend.request.AddExpensesInfoReq;
import com.example.Family_life_backend.request.UpdateExpensesInfoReq;
import com.example.Family_life_backend.response.BasicRes;
import com.example.Family_life_backend.response.GetExpenseInfoRes;

@Service
public class ExpenseService {
	@Autowired
	private ExpenseDao expenseDao;

//沒有group 用自己去查
	public GetExpenseInfoRes getExpenseInfo(Long groupId, Long userId) {

		if (groupId == null && userId == null) {
			return new GetExpenseInfoRes("groupId 或 userId 至少要有一個", 400);
		}

		List<Expense> result;

		if (groupId != null) {

			result = expenseDao.findExpenses(groupId, userId);

		} else {

			result = expenseDao.findExpenses(groupId, userId);
		}

		return new GetExpenseInfoRes("成功", 200, result);
	}

	public BasicRes addExpenseInfo(AddExpensesInfoReq req) {
		expenseDao.insertExpense(req.getGroupId(), req.getUserId(), req.getPrice(), req.getCategoryId(), //
				req.getRelatedItemId(), req.getExpenseDate(), req.getNote());
		return new BasicRes("成功", 200);
	}

	public BasicRes updateExpenseInfo(UpdateExpensesInfoReq req) {

		expenseDao.updateExpense(req.getId(), req.getGroupId(), req.getUserId(), req.getPrice(), req.getCategoryId(), //
				req.getRelatedItemId(), req.getExpenseDate(), req.getNote());
		return new BasicRes("成功", 200);
	}

	public BasicRes deleteExpenseInfo(List<Integer> id) {
		if (id == null || id.isEmpty()) {
			return new BasicRes("id 參數錯誤", 400);

		}
		expenseDao.deleteExpense(id);
		return new BasicRes("成功", 200);
	}
}
