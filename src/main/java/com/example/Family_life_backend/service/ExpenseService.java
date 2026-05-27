package com.example.Family_life_backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.dao.ExpenseDao;
import com.example.Family_life_backend.dao.ItemsDao;
import com.example.Family_life_backend.dao.UserInfoDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.entity.Expense;
import com.example.Family_life_backend.entity.Items;
import com.example.Family_life_backend.entity.UserInfo;
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

	@Autowired
	private UserInfoDao userDao;

//沒有group 用自己去查
	public GetExpenseInfoRes getExpenseInfo(Long groupId, Long userId) {
		if (userId == null || userId <= 0) {
			return new GetExpenseInfoRes("userId 錯誤", 400);
		}
// 查私人
		if (groupId == 0L /* 0L 為強制轉形成 Long */) {
			List<Expense> result = expenseDao.findPersonalExpenses(userId);
			return buildRes(result, null);
		}

		int isMember = groupMemberDao.checkUserIdExistInGroup(groupId, userId);
		if (isMember <= 0) {
			return new GetExpenseInfoRes("你不是該群組成員", 400);
		}
// 查群組 
		List<Expense> result = expenseDao.findExpenses(groupId, null);
		if (result == null || result.isEmpty()) {
			return buildRes(result, null);// 等於群組 媒人消費
		}

		List<Long> userIds = result.stream().map(Expense::getUserId).filter(Objects::nonNull) // 💡 修正原本寫錯的 filter 變數
				.distinct().collect(Collectors.toList());

		Map<Long, UserInfo> userMap = null;
		if (!userIds.isEmpty()) {
			List<UserInfo> userList = userDao.getSelfInfoByIds(userIds);

			userMap = userList.stream().collect(Collectors.toMap(//
					user -> Long.valueOf(user.getUserId()), //
					user -> {
						user.setPwd(null); // 💡 直接把密碼欄位清空
						return user;
					},
					(existing, replacement) -> existing));
		}

		return buildRes(result, userMap);
	}

	private GetExpenseInfoRes buildRes(List<Expense> result, Map<Long, UserInfo> userMap) {
		if (result == null) {
			result = new ArrayList<>();
		}

		List<Long> itemIds = result.stream().map(Expense::getRelatedItemId).filter(Objects::nonNull).distinct()
				.collect(Collectors.toList());

		Map<Long, Items> itemMap = new HashMap<>();

		if (!itemIds.isEmpty()) {
			List<Items> itemList = itemsDao.getItemById(itemIds);
			itemMap = itemList.stream().collect(Collectors.toMap(item -> Long.valueOf(item.getId()), item -> item,
					(existing, replacement) -> existing));
		}


		return new GetExpenseInfoRes("成功", 200, result, itemMap, userMap);
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
