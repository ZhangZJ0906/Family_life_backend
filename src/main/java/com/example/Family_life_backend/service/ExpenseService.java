package com.example.Family_life_backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.dao.ExpenseDao;
import com.example.Family_life_backend.dao.ItemsDao;
import com.example.Family_life_backend.entity.Expense;
import com.example.Family_life_backend.entity.Items;
import com.example.Family_life_backend.request.AddExpensesInfoReq;
import com.example.Family_life_backend.request.UpdateExpensesInfoReq;
import com.example.Family_life_backend.response.BasicRes;
import com.example.Family_life_backend.response.GetExpenseInfoRes;

@Service
public class ExpenseService {
	@Autowired
	private ExpenseDao expenseDao;

	@Autowired
	private ItemsDao itemsDao;
//沒有group 用自己去查
	public GetExpenseInfoRes getExpenseInfo(Long groupId, Long userId) {

		if (groupId == null && userId == null) {
			return new GetExpenseInfoRes("groupId 或 userId 至少要有一個", 400);
		}

		List<Expense> result = expenseDao.findExpenses(groupId, userId);
		List<Long> itemIds = result.stream().map(Expense::getRelatedItemId).filter(Objects::nonNull).distinct()
				.collect(Collectors.toList());
		Map<Long, Items> itemMap = new HashMap<>();

		// 3. 批次查出物品，並轉成 Map
		if (!itemIds.isEmpty()) {
			List<Items> itemList = itemsDao.getItemById(itemIds);
			itemMap = itemList.stream().collect(Collectors.toMap(item -> Long.valueOf(item.getId()), // 強制轉成 Long
					item -> item, (existing, replacement) -> existing // 防呆：若有重複的 ID 則保留前一個
			));
		}


		return new GetExpenseInfoRes("成功", 200, result, itemMap);
	}

	public BasicRes addExpenseInfo(AddExpensesInfoReq req) {
		expenseDao.insertExpense(req.getGroupId(), req.getUserId(), req.getPrice(), req.getCategoryId(), //
				req.getRelatedItemId(), req.getRelatedItemName(), req.getExpenseDate(), req.getNote());
		return new BasicRes("成功", 200);
	}

	public BasicRes updateExpenseInfo(UpdateExpensesInfoReq req) {

		expenseDao.updateExpense(req.getId(), req.getGroupId(), req.getUserId(), req.getPrice(), req.getCategoryId(), //
				req.getRelatedItemId(), req.getRelatedItemName(), req.getExpenseDate(), req.getNote());
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
