package com.example.Family_life_backend.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
//後續還要加上 分類群組使用者查詢
		List<Items> list = itemDao.getItemByGroupId(groupId);

		if (list == null || list.isEmpty()) {
			return new GetItemsRes("失敗", 400);
		}
		// 2. 查詢資料庫取得位置資訊
		List<Object[]> locData = itemDao.getItemLocationList();
		List<Object[]> categoriesData = itemDao.getItemCategoriesList();

		// 3. 將 List<Object[]> 轉成 Map<Integer, String>，方便前端使用
		Map<Integer, String> locationMap = locData.stream().collect(Collectors.toMap(//
				obj -> ((Number) obj[0]).intValue(), // location_id
				obj -> (String) obj[1] // location_name
		));
		Map<Integer, String> categoriesMap = categoriesData.stream().collect(Collectors.toMap(//
				obj -> ((Number) obj[0]).intValue(), // id
				obj -> (String) obj[1] // name
		));

		return new GetItemsRes("成功", 200, list, locationMap, categoriesMap);

	}
}
