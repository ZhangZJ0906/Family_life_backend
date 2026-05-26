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
import com.example.Family_life_backend.dao.groupMemberDao;
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
	private groupMemberDao groupMemberDao;
	@Autowired
	private ItemsDao itemsDao;

//沒有group 用自己去查
	public GetExpenseInfoRes getExpenseInfo(Long groupId, Long userId) {
		// 私人
		if (groupId == 0L) {
			if (userId == null || userId <= 0) {
				return new GetExpenseInfoRes("userId 錯誤", 400);
			}
			// 直接撈個人資料，不需要驗群組成員
			List<Expense> result = expenseDao.findPersonalExpenses(userId);
			return buildRes(result);
		}
		// 群組帳：驗成員身份後撈整個群組
		if (userId == null) {
			return new GetExpenseInfoRes("userId 錯誤", 400);
		}
		int isMember = groupMemberDao.checkUserIdExistInGroup(groupId, userId);
		if (isMember <= 0) {
			return new GetExpenseInfoRes("你不是該群組成員", 400);
		}
		List<Expense> result = expenseDao.findExpenses(groupId, null);
		return buildRes(result);
	}

	private GetExpenseInfoRes buildRes(List<Expense> result) {
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
