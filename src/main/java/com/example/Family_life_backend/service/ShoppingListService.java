package com.example.Family_life_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.constants.ReplyMessage;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.PurchaseItemDao;
import com.example.Family_life_backend.dao.ShoppingListDao;
import com.example.Family_life_backend.dao.UserInfoDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.entity.PurchaseItem;
import com.example.Family_life_backend.entity.PurchaseItemId;
import com.example.Family_life_backend.entity.ShoppingList;
import com.example.Family_life_backend.request.AddPurchaseItemReq;
import com.example.Family_life_backend.request.CreateListReq;
import com.example.Family_life_backend.response.BasicRes;
import com.example.Family_life_backend.vo.PurchaseItemVo;

import jakarta.transaction.Transactional;

@Service
public class ShoppingListService {

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserInfoDao userInfoDao;

	@Autowired
	private ShoppingListDao shoppingListDao;

	@Autowired
	private PurchaseItemDao purchaseItemDao;

	@Autowired
	private NotifyDao notifyDao;

	@Autowired
	private groupDao groupDao;

	@Autowired
	private groupMemberDao groupMemebrDao;

	@Autowired
	private NotifySocketService notifySocketService;

	@Transactional(rollbackOn = Exception.class)
	public BasicRes create(CreateListReq req) {
		if (req == null || req.getShoppingList() == null) {
			return new BasicRes(ReplyMessage.TITLE_ERROR.getMessage(), ReplyMessage.TITLE_ERROR.getCode());
		}

		ShoppingList shoppingList = req.getShoppingList();
		BasicRes checkRes = checkShoppingList(shoppingList);
		if (checkRes != null) {
			return checkRes;
		}

		LocalDate now = LocalDate.now();
		shoppingList.setCreatedDate(now);
		ShoppingList savedShoppingList = shoppingListDao.save(shoppingList);

		List<PurchaseItemVo> purchaseItemVoList = req.getPurchaseItemVoList();
		if (CollectionUtils.isEmpty(purchaseItemVoList)) {
			return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
		}

		List<PurchaseItem> purchaseItemList = buildPurchaseItemList(savedShoppingList.getId(),
				shoppingList.getCreaterId(), purchaseItemVoList, now);
		if (purchaseItemList.size() != purchaseItemVoList.size()) {
			return new BasicRes(ReplyMessage.PURCHASE_ITEM_ERROR.getMessage(),
					ReplyMessage.PURCHASE_ITEM_ERROR.getCode());
		}
		purchaseItemDao.saveAll(purchaseItemList);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}

	@Transactional(rollbackOn = Exception.class)
	public BasicRes delete(int listId) {
		if (!shoppingListDao.existsById(listId)) {
			return new BasicRes(ReplyMessage.LIST_NOT_FOUND.getMessage(), ReplyMessage.LIST_NOT_FOUND.getCode());
		}

		purchaseItemDao.deleteByListId(listId);
		shoppingListDao.deleteById(listId);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}

	public BasicRes updateList(CreateListReq req) {
		if (req == null || req.getShoppingList() == null) {
			return new BasicRes(ReplyMessage.LIST_NOT_FOUND.getMessage(), ReplyMessage.LIST_NOT_FOUND.getCode());
		}

		ShoppingList reqList = req.getShoppingList();
		ShoppingList oldList = shoppingListDao.findById(reqList.getId()).orElse(null);

		if (oldList == null) {
			return new BasicRes(ReplyMessage.LIST_NOT_FOUND.getMessage(), ReplyMessage.LIST_NOT_FOUND.getCode());
		}
		if (!StringUtils.hasText(reqList.getTitle())) {
			return new BasicRes(ReplyMessage.TITLE_ERROR.getMessage(), ReplyMessage.TITLE_ERROR.getCode());
		}

		oldList.setTitle(reqList.getTitle());
		oldList.setGroup_id(reqList.getGroup_id());
		shoppingListDao.save(oldList);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}

	public List<ShoppingList> getLists(int createrId) {
		return shoppingListDao.findByCreaterId(createrId);
	}

	public List<PurchaseItem> getItems(int listId) {
		return purchaseItemDao.getByListId(listId);
	}

	public BasicRes addItems(AddPurchaseItemReq req) {
		if (req == null || !shoppingListDao.existsById(req.getListId())) {
			return new BasicRes(ReplyMessage.LIST_NOT_FOUND.getMessage(), ReplyMessage.LIST_NOT_FOUND.getCode());
		}
		if (req.getCreaterId() <= 0 || !userInfoDao.existsById(req.getCreaterId())) {
			return new BasicRes(ReplyMessage.CREATOR_ID_ERROR.getMessage(), ReplyMessage.CREATOR_ID_ERROR.getCode());
		}
		if (CollectionUtils.isEmpty(req.getPurchaseItemVoList())) {
			return new BasicRes(ReplyMessage.PURCHASE_ITEM_ERROR.getMessage(),
					ReplyMessage.PURCHASE_ITEM_ERROR.getCode());
		}

		List<PurchaseItem> purchaseItemList = buildPurchaseItemList(req.getListId(), req.getCreaterId(),
				req.getPurchaseItemVoList(), LocalDate.now());

		if (purchaseItemList.size() != req.getPurchaseItemVoList().size()) {
			return new BasicRes(ReplyMessage.PURCHASE_ITEM_ERROR.getMessage(),
					ReplyMessage.PURCHASE_ITEM_ERROR.getCode());
		}
		purchaseItemDao.saveAll(purchaseItemList);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}

	public BasicRes deleteItem(int listId, int itemId, int userId, int groupId) {
		PurchaseItemId purchaseItemId = new PurchaseItemId(itemId, listId);
		if (!purchaseItemDao.existsById(purchaseItemId)) {
			return new BasicRes(ReplyMessage.PURCHASE_ITEM_ERROR.getMessage(),
					ReplyMessage.PURCHASE_ITEM_ERROR.getCode());
		}

		// 發送通知
		List<groupMembersDTO> getGroupMembers = groupMemebrDao.getMembersByGroupId((long)groupId);
		String DeletedName = purchaseItemDao.getItemNameById((long) listId, (long) itemId);
		String sendName = groupDao.getSelfName((long) userId);
		String content = sendName + "已將" + DeletedName + "購買請求刪除";
		// 不要傳給自己或私人通知

		for (groupMembersDTO member : getGroupMembers) {
			if ((long) userId != member.getUser_id() && groupId != 0) {
				purchaseItemDao.sendPurchaseReqToAnotherNotify((long)groupId, member.getUser_id(), content, "group", false);

				if (userInfoDao.getEmailNotifyById(member.getUser_id()) == true) {
					emailService.sendMail(userInfoDao.getEmailById(member.getUser_id()), "邀請通知", content);
				}

				// 🔥 正確：要重新查 unread count
				int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());

				notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
			}
		}
		

		purchaseItemDao.deleteById(purchaseItemId);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}

	public BasicRes updateCheck(int listId, int itemId, boolean check, int checkMan) {
		if (!purchaseItemDao.existsById(new PurchaseItemId(itemId, listId))) {
			return new BasicRes(ReplyMessage.PURCHASE_ITEM_ERROR.getMessage(),
					ReplyMessage.PURCHASE_ITEM_ERROR.getCode());
		}

		LocalDate checkDate = check ? LocalDate.now() : null;
		int checkedById = check ? checkMan : 0;
		purchaseItemDao.updateCheck(listId, itemId, check, checkDate, checkedById);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}

	private BasicRes checkShoppingList(ShoppingList shoppingList) {
		if (!StringUtils.hasText(shoppingList.getTitle())) {
			return new BasicRes(ReplyMessage.TITLE_ERROR.getMessage(), ReplyMessage.TITLE_ERROR.getCode());
		}
		if (shoppingList.getCreaterId() <= 0 || !userInfoDao.existsById(shoppingList.getCreaterId())) {
			return new BasicRes(ReplyMessage.CREATOR_ID_ERROR.getMessage(), ReplyMessage.CREATOR_ID_ERROR.getCode());
		}
		return null;
	}

	private List<PurchaseItem> buildPurchaseItemList(int listId, int createrId, List<PurchaseItemVo> purchaseItemVoList,
			LocalDate createdDate) {
		List<PurchaseItem> purchaseItemList = new ArrayList<>();
		// 拿群組ID
		Long groupID = shoppingListDao.getgroupIdByListId((long) listId);
		int nextItemId = purchaseItemDao.getMaxIdByListId(listId) + 1;

		for (PurchaseItemVo vo : purchaseItemVoList) {
			if (vo == null || !StringUtils.hasText(vo.getItem()) || vo.getQuantity() <= 0) {
				return new ArrayList<>();
			}

			PurchaseItem purchaseItem = new PurchaseItem();
			purchaseItem.setId(nextItemId++);
			purchaseItem.setListId(listId);
			purchaseItem.setCreaterId(createrId);
			purchaseItem.setCreatedDate(createdDate);
			purchaseItem.setUserId(vo.getUserId());
			purchaseItem.setCategoryId(vo.getCategoryId());
			purchaseItem.setItem(vo.getItem());
			purchaseItem.setQuantity(vo.getQuantity());
			purchaseItem.setCheck(false);
			purchaseItemList.add(purchaseItem);

			// 發送通知
			String sendName = groupDao.getSelfName((long) createrId);
			String content = sendName + "已傳送" + vo.getItem() + "購買請求給你";
			// 不要傳給自己或私人通知
			if ((long) createrId != (long) vo.getUserId() && groupID != 0L) {
				purchaseItemDao.sendPurchaseReqToAnotherNotify(groupID, (long) vo.getUserId(), content, "group", false);

				if (userInfoDao.getEmailNotifyById((long) vo.getUserId()) == true) {
					emailService.sendMail(userInfoDao.getEmailById((long) vo.getUserId()), "群組通知", content);
				}

				// 🔥 正確：要重新查 unread count
				int unreadCount = notifyDao.countUnreadByUserId((long) vo.getUserId());

				notifySocketService.pushUnreadCount((long) vo.getUserId(), unreadCount);
			}

		}

		return purchaseItemList;
	}

	public BasicRes updateItem(AddPurchaseItemReq req) {
		PurchaseItemVo vo = req.getPurchaseItemVoList().get(0);
		PurchaseItemId id = new PurchaseItemId(vo.getId(), req.getListId());

		// 拿群組ID
		Long groupID = shoppingListDao.getgroupIdByListId((long) req.getListId());

		PurchaseItem item = purchaseItemDao.findById(id).orElse(null);
		if (item == null) {
			return new BasicRes(ReplyMessage.PURCHASE_ITEM_ERROR.getMessage(),
					ReplyMessage.PURCHASE_ITEM_ERROR.getCode());
		}

		Long OldGetterId = (long) item.getUserId();// 拿原先轉給的成員ID
		item.setUserId(vo.getUserId());
		item.setCategoryId(vo.getCategoryId());
		item.setItem(vo.getItem());
		item.setQuantity(vo.getQuantity());

		// 發送通知
		String sendName = groupDao.getSelfName((long) req.getCreaterId());
		String getName = groupDao.getSelfName((long) vo.getUserId());
		String contentToOldGetter = sendName + "已將" + vo.getItem() + "購買請求轉給" + getName;
		String contentToNewGetter = sendName + "已將" + vo.getItem() + "購買請求轉給你";

		purchaseItemDao.sendPurchaseReqToAnotherNotify(groupID, OldGetterId, contentToOldGetter, "group", false);

		if ((long) req.getCreaterId() != (long) vo.getUserId() && groupID != 0L) {

			purchaseItemDao.sendPurchaseReqToAnotherNotify(groupID, (long) vo.getUserId(), contentToNewGetter, "group",
					false);

			if (userInfoDao.getEmailNotifyById(OldGetterId) == true) {
				emailService.sendMail(userInfoDao.getEmailById(OldGetterId), "群組通知", contentToOldGetter);
			}

			if (userInfoDao.getEmailNotifyById((long) vo.getUserId()) == true) {
				emailService.sendMail(userInfoDao.getEmailById((long) vo.getUserId()), "群組通知", contentToNewGetter);
			}

			// 🔥 正確：要重新查 unread count
			int unreadCount = notifyDao.countUnreadByUserId((long) vo.getUserId());

			notifySocketService.pushUnreadCount((long) vo.getUserId(), unreadCount);
		}

		purchaseItemDao.save(item);
		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}

}
