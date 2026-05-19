package com.example.Family_life_backend.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.dao.CategoiesDao;
import com.example.Family_life_backend.dao.ItemsDao;
import com.example.Family_life_backend.dao.LocationDao;
import com.example.Family_life_backend.enity.Categories;
import com.example.Family_life_backend.enity.Items;
import com.example.Family_life_backend.enity.Location;
import com.example.Family_life_backend.request.ItemAddInfoReq;
import com.example.Family_life_backend.request.ItemUpdateReq;
import com.example.Family_life_backend.response.AddItemsInfoRes;
import com.example.Family_life_backend.response.BasicRes;
import com.example.Family_life_backend.response.GetItemsRes;

import jakarta.transaction.Transactional;

@Service
public class ItemsService {
	@Autowired
	private ItemsDao itemDao;
	@Autowired
	private LocationDao locationDao;
	@Autowired
	private CategoiesDao categoiesDao;

	public GetItemsRes getItems(List<Integer> groupId, Integer userId) {
		if (userId <= 0 || userId == null) {
			return new GetItemsRes("失敗 user Id 錯誤", 400);
		}
		// 後續還要加上 使用者查詢
		List<Items> list = itemDao.getItemByGroupId(groupId, userId);

		if (list == null) {
			return new GetItemsRes("失敗", 400);
		}
		// 2. 查詢資料庫取得位置資訊
		List<Location> locData = locationDao.getItemLocationList();
		List<Categories> categoriesData = categoiesDao.getItemCategoriesList();

		// 3. 將 List<Object[]> 轉成 Map<Integer, String>，方便前端使用
		Map<Integer, String> locationMap = locData.stream().collect(Collectors.toMap(//
				Location::getId, //
				Location::getName, //
				(existing, replacement) -> existing // 預防 existing 為舊的 replacement維新的 當新的等於舊的會去取舊的
		));
		Map<Integer, String> categoriesMap = categoriesData.stream().collect(Collectors.toMap(//
				Categories::getCategoryId, Categories::getCategoryName, (existing, replacement) -> existing));

		return new GetItemsRes("成功", 200, list, locationMap, categoriesMap);

	}

	@Transactional
	public AddItemsInfoRes saveItem(ItemAddInfoReq req) {
		// 處理群組邏輯：沒傳就給 0
		Integer finalGroupId = (req.getGroupId() != null) ? req.getGroupId() : 0;

		// 呼叫原生 SQL
		itemDao.insertItemNative(finalGroupId, req.getCategoryId(), req.getName(), req.getQuantity(), req.getUnit(),
				req.getLocationId(), req.getPrice(), req.getPurchaseDate(), req.getExpireDate(),
				req.getNotify() != null ? req.getNotify() : false, req.getNote(), req.getUserId(), req.getUnitPrice());
		return new AddItemsInfoRes("成功", 200);
	}

	@Transactional
	public BasicRes updateItem(ItemUpdateReq req) {
		// 處理群組邏輯：沒傳就給 0
		Integer finalGroupId = (req.getGroupId() != null) ? req.getGroupId() : 0;

		// 呼叫原生 SQL
		itemDao.updateItem(req.getId(), finalGroupId, req.getCategoryId(), req.getName(), req.getQuantity(),
				req.getUnit(), req.getLocationId(), req.getPrice(), req.getPurchaseDate(), req.getExpireDate(),
				req.getNotify() != null ? req.getNotify() : false, req.getNote(), req.getUnitPrice());
		return new BasicRes("成功", 200);
	}

	@Transactional
	public BasicRes deleteItem(List<Integer> id) {
		if (id == null || id.isEmpty()) {
			return new BasicRes("失敗：請提供要刪除的 ID 清單", 400);
		}
		for (int i : id) {
			if (i <= 0) {
				return new BasicRes("失敗 id 參數錯誤", 400);
			}
		}

		itemDao.deleteItemById(id);
		return new BasicRes("成功", 200);

	}
}
