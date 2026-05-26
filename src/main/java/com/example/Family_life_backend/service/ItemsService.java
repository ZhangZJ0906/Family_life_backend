package com.example.Family_life_backend.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.dao.CategoiesDao;
import com.example.Family_life_backend.dao.ItemsDao;
import com.example.Family_life_backend.dao.LocationDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.entity.Categories;
import com.example.Family_life_backend.entity.Items;
import com.example.Family_life_backend.entity.Location;
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

	@Autowired
	private groupDao groupDao;

	@Autowired
	private groupMemberDao groupMemberDao;

	public GetItemsRes getItems(Integer groupId, Integer userId) {


	    List<Items> list = new ArrayList<>();

	    // groupId == null 代表私人物品，不需要驗群組成員
	    if (groupId == null) {
	        list = itemDao.getItemByGroupId(null, userId);
	    } else {
	        // groupId != null 代表群組物品，要驗證使用者是不是群組成員
	        int isMember = groupMemberDao.checkUserIdExistInGroup(
	                Long.valueOf(groupId),
	                Long.valueOf(userId)
	        );

	        if (isMember <= 0) {
	            return new GetItemsRes("你不是該群組成員", 400);
	        }

	        list = itemDao.getItemByGroupId(groupId, userId);
	    }

	    List<Location> locData = locationDao.getItemLocationList();
	    List<Categories> categoriesData = categoiesDao.getItemCategoriesList();

	    Map<Integer, String> locationMap = locData.stream().collect(Collectors.toMap(
	            Location::getId,
	            Location::getName,
	            (existing, replacement) -> existing
	    ));

	    Map<Integer, String> categoriesMap = categoriesData.stream().collect(Collectors.toMap(
	            Categories::getCategoryId,
	            Categories::getCategoryName,
	            (existing, replacement) -> existing
	    ));

	    return new GetItemsRes("成功", 200, list, locationMap, categoriesMap);
	}

	@Transactional
	public AddItemsInfoRes saveItem(ItemAddInfoReq req) {
		Integer finalGroupId = (req.getGroupId() != null) ? req.getGroupId() : 0;

		// 安全庫存量：沒填就給 0
		Integer finalSafeQuantity = req.getSafeQuantity() != null ? req.getSafeQuantity() : 0;

		String status = calcStatus(req.getQuantity(), finalSafeQuantity, req.getExpireDate());

		String remindMessage = calcRemindMessage(req.getQuantity(), finalSafeQuantity, req.getExpireDate());

		itemDao.insertItemNative(finalGroupId, req.getCategoryId(), req.getName(), req.getQuantity(), req.getUnit(),
				req.getLocationId(), req.getPrice(), req.getPurchaseDate(), req.getExpireDate(),
				req.getNotify() != null ? req.getNotify() : false, req.getNote(), req.getUserId(), req.getUnitPrice(),
				finalSafeQuantity, status, remindMessage);

		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId((long) finalGroupId);
		String content = groupDao.getSelfName((long) req.getUserId()) + "已新增" + req.getName() + "清單";

		if (finalGroupId != 0) {
			for (groupMembersDTO member : getGroupMembers) {
				if (member.getUser_id() != (long) req.getUserId()) {
					itemDao.addGroupItemNotify((long) finalGroupId, member.getUser_id(), content, "itemlist", false);
				}
			}
		}

		return new AddItemsInfoRes("成功", 200);
	}
	@Transactional
	public BasicRes updateItem(ItemUpdateReq req) {
		Integer finalGroupId = req.getGroupId();

		// 安全庫存量：沒填就給 0
		Integer finalSafeQuantity = req.getSafeQuantity() != null ? req.getSafeQuantity() : 0;

		String status = calcStatus(req.getQuantity(), finalSafeQuantity, req.getExpireDate());

		String remindMessage = calcRemindMessage(req.getQuantity(), finalSafeQuantity, req.getExpireDate());

		itemDao.updateItem(req.getId(), finalGroupId, req.getCategoryId(), req.getName(), req.getQuantity(),
				req.getUnit(), req.getLocationId(), req.getPrice(), req.getPurchaseDate(), req.getExpireDate(),
				req.getNotify() != null ? req.getNotify() : false, req.getNote(), req.getUnitPrice(), finalSafeQuantity,
				status, remindMessage);

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

	private String calcStatus(Integer quantity, Integer saveQuantity, LocalDate expireDate) {
		LocalDate today = LocalDate.now();

		if (expireDate != null && expireDate.isBefore(today)) {
			return "已到期";
		}

		if (expireDate != null && !expireDate.isAfter(today.plusDays(7))) {
			return "即將到期";
		}

		if (quantity != null && saveQuantity != null && quantity <= saveQuantity) {
			return "庫存不足";
		}

		return "正常";
	}

	private String calcRemindMessage(Integer quantity, Integer safeQuantity, LocalDate expireDate) {
		LocalDate today = LocalDate.now();

		if (expireDate != null) {
			long daysLeft = ChronoUnit.DAYS.between(today, expireDate);

			if (daysLeft < 0) {
				return "已過期 " + Math.abs(daysLeft) + " 天";
			}

			if (daysLeft <= 7) {
				return "剩餘 " + daysLeft + " 天";
			}
		}

		if (quantity != null && safeQuantity != null && quantity <= safeQuantity) {
			return "目前庫存低於安全庫存";
		}

		return "";
	}

}
