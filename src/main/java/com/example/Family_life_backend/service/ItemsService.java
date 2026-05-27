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
import com.example.Family_life_backend.dao.NotifyDao;
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

	@Autowired
	private NotifyDao notifyDao;

	@Autowired
	private NotifySocketService notifySocketService;

	public GetItemsRes getItems(Integer groupId, Integer userId) {

		List<Items> list = new ArrayList<Items>();
		// 群組模式驗成員
		if (groupId != 0) {
			int isMember = groupMemberDao.checkUserIdExistInGroup(Long.valueOf(groupId), Long.valueOf(userId));
			if (isMember <= 0) {
				return new GetItemsRes("你不是該群組成員", 400);
			}

			// 後續還要加上 使用者查詢
			list = itemDao.getItemByGroupId(groupId, userId);

			if (list == null) {
				return new GetItemsRes("失敗", 400);
			}

		} else if (groupId == 0) { // 私人物品
			list = itemDao.getSelfItem(userId);
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

		System.out.println("locationMap: " + locationMap);
		System.out.println("categoriesMap: " + categoriesMap);

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

		if (finalGroupId != 0) {
			List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId((long) finalGroupId);
			String content = groupDao.getSelfName((long) req.getUserId()) + "已新增" + req.getName() + "清單";

			for (groupMembersDTO member : getGroupMembers) {
				if (member.getUser_id() != (long) req.getUserId()) {
					itemDao.addGroupItemNotify((long) finalGroupId, member.getUser_id(), content, "itemlist", false);
					// 🔥 正確：要重新查 unread count
					int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());

					notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
				}
			}
		}

		return new AddItemsInfoRes("成功", 200);
	}
	@Transactional
	public BasicRes updateItem(ItemUpdateReq req) {

		Integer finalGroupId = (req.getGroupId() != null) ? req.getGroupId() : 0;
		String oldItemName = itemDao.getItemNameById((long) req.getId());


		// 安全庫存量：沒填就給 0
		Integer finalSafeQuantity = req.getSafeQuantity() != null ? req.getSafeQuantity() : 0;

		String status = calcStatus(req.getQuantity(), finalSafeQuantity, req.getExpireDate());

		String remindMessage = calcRemindMessage(req.getQuantity(), finalSafeQuantity, req.getExpireDate());

		itemDao.updateItem(req.getId(), finalGroupId, (long) req.getUserId(), req.getCategoryId(), req.getName(),
				req.getQuantity(), req.getUnit(), req.getLocationId(), req.getPrice(), req.getPurchaseDate(),
				req.getExpireDate(), req.getNotify() != null ? req.getNotify() : false, req.getNote(),
				req.getUnitPrice(), finalSafeQuantity, status, remindMessage);

		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId((long) finalGroupId);
		String content = groupDao.getSelfName((long) req.getUserId()) + "已將" + oldItemName + "清單改成" + req.getName();

		if (finalGroupId != 0) {
			for (groupMembersDTO member : getGroupMembers) {
				if (member.getUser_id() != (long) req.getUserId()) {
					itemDao.addGroupItemNotify((long) finalGroupId, member.getUser_id(), content, "update", false);
					// 🔥 正確：要重新查 unread count
					int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());

					notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
				}
			}
		}

		return new BasicRes("成功", 200);
	}

	@Transactional
	public BasicRes deleteItem(List<Integer> id, Long userId) {
		if (id == null || id.isEmpty()) {
			return new BasicRes("失敗：請提供要刪除的 ID 清單", 400);
		}
		for (int i : id) {
			if (i <= 0) {
				return new BasicRes("失敗 id 參數錯誤", 400);
			}
		}

		for (Integer i : id) {
			Long finalGroupId = itemDao.getGroupIdById((long) i);
			List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId(finalGroupId);
			String content = groupDao.getSelfName(userId) + "已將" + itemDao.getItemNameById((long) i) + "刪除";
			if (finalGroupId != 0) {
				for (groupMembersDTO member : getGroupMembers) {
					if (member.getUser_id() != userId) {
						itemDao.addGroupItemNotify((long) finalGroupId, member.getUser_id(), content, "update", false);
						// 🔥 正確：要重新查 unread count
						int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());

						notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
					}
				}
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
