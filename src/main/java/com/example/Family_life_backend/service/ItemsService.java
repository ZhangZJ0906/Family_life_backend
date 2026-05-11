package com.example.Family_life_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.dao.ItemsDao;
import com.example.Family_life_backend.enity.Items;
import com.example.Family_life_backend.response.GetItemsRes;

@Service
public class ItemsService {
	@Autowired
	private ItemsDao itemDao;

	public GetItemsRes getItems(int groupId) {

		List<Items> list = itemDao.getItem(groupId);
		if (list.size() < 0) {
			return new GetItemsRes("失敗", 400);
		}
		return new GetItemsRes("成功", 200, list);

	}
}
