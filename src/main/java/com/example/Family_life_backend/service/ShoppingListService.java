package com.example.Family_life_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.Family_life_backend.constants.ReplyMessage;
import com.example.Family_life_backend.dao.PurchaseItemDao;
import com.example.Family_life_backend.dao.ShoppingListDao;
import com.example.Family_life_backend.dao.UserInfoDao;
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
	private UserInfoDao userInfoDao;
	
	@Autowired
	private ShoppingListDao shoppingListDao;

	@Autowired
	private PurchaseItemDao purchaseItemDao;

	/* 新增購物清單 */
	@Transactional(rollbackOn = Exception.class)
	public BasicRes create(CreateListReq req) {
		if(req == null || req.getShoppingList() == null) {
			return new BasicRes(ReplyMessage.TITLE_ERROR.getMessage(), ReplyMessage.TITLE_ERROR.getCode());
		}

		ShoppingList shoppingList = req.getShoppingList();
		BasicRes checkRes = checkShoppingList(shoppingList);
		if(checkRes != null) {
			return checkRes;
		}

		LocalDate now = LocalDate.now();
		shoppingList.setCreatedDate(now);
		ShoppingList savedShoppingList = shoppingListDao.save(shoppingList);

		List<PurchaseItemVo> purchaseItemVoList = req.getPurchaseItemVoList();
		if(CollectionUtils.isEmpty(purchaseItemVoList)) {
			return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
		}

		List<PurchaseItem> purchaseItemList = buildPurchaseItemList(
				savedShoppingList.getId(), shoppingList.getCreaterId(), purchaseItemVoList, now);
		if(purchaseItemList.size() != purchaseItemVoList.size()) {
			return new BasicRes(ReplyMessage.PURCHASE_ITEM_ERROR.getMessage(),
					ReplyMessage.PURCHASE_ITEM_ERROR.getCode());
		}
		purchaseItemDao.saveAll(purchaseItemList);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}
	
	/* 刪除購物清單 */
	@Transactional(rollbackOn = Exception.class)
	public BasicRes delete(int listId) {
		if(!shoppingListDao.existsById(listId)) {
			return new BasicRes(ReplyMessage.LIST_NOT_FOUND.getMessage(), ReplyMessage.LIST_NOT_FOUND.getCode());
		}

		purchaseItemDao.deleteByListId(listId);
		shoppingListDao.deleteById(listId);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}
	
	/* 變更購物清單 */
	public BasicRes updateList(CreateListReq req) {
		if(req == null || req.getShoppingList() == null) {
			return new BasicRes(ReplyMessage.LIST_NOT_FOUND.getMessage(), ReplyMessage.LIST_NOT_FOUND.getCode());
		}

		ShoppingList reqList = req.getShoppingList();
		ShoppingList oldList = shoppingListDao.findById(reqList.getId()).orElse(null);

		if(oldList == null) {
			return new BasicRes(ReplyMessage.LIST_NOT_FOUND.getMessage(), ReplyMessage.LIST_NOT_FOUND.getCode());
		}
		if(!StringUtils.hasText(reqList.getTitle())) {
			return new BasicRes(ReplyMessage.TITLE_ERROR.getMessage(), ReplyMessage.TITLE_ERROR.getCode());
		}

		oldList.setTitle(reqList.getTitle());
		oldList.setGroup_id(reqList.getGroup_id());
		shoppingListDao.save(oldList);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}
	
	/* 查看購物清單 */
	public List<ShoppingList> getLists(int createrId) {
		return shoppingListDao.findByCreaterId(createrId);
	}

	/* 查看購物項目 */
	public List<PurchaseItem> getItems(int listId) {
		return purchaseItemDao.getByListId(listId);
	}

	/* 在已有的購物清單裡增加購物項目 */
	public BasicRes addItems(AddPurchaseItemReq req) {
		if(req == null || !shoppingListDao.existsById(req.getListId())) {
			return new BasicRes(ReplyMessage.LIST_NOT_FOUND.getMessage(), ReplyMessage.LIST_NOT_FOUND.getCode());
		}
		if(req.getCreaterId() <= 0 || !userInfoDao.existsById(req.getCreaterId())) {
			return new BasicRes(ReplyMessage.CREATOR_ID_ERROR.getMessage(), ReplyMessage.CREATOR_ID_ERROR.getCode());
		}
		if(CollectionUtils.isEmpty(req.getPurchaseItemVoList())) {
			return new BasicRes(ReplyMessage.PURCHASE_ITEM_ERROR.getMessage(),
					ReplyMessage.PURCHASE_ITEM_ERROR.getCode());
		}

		List<PurchaseItem> purchaseItemList = buildPurchaseItemList(
				req.getListId(), req.getCreaterId(), req.getPurchaseItemVoList(), LocalDate.now());
		if(purchaseItemList.size() != req.getPurchaseItemVoList().size()) {
			return new BasicRes(ReplyMessage.PURCHASE_ITEM_ERROR.getMessage(),
					ReplyMessage.PURCHASE_ITEM_ERROR.getCode());
		}
		purchaseItemDao.saveAll(purchaseItemList);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}

	/* 刪除購物清單裡的購物項目 */
	public BasicRes deleteItem(int listId, int itemId) {
		PurchaseItemId purchaseItemId = new PurchaseItemId(itemId, listId);
		if(!purchaseItemDao.existsById(purchaseItemId)) {
			return new BasicRes(ReplyMessage.PURCHASE_ITEM_ERROR.getMessage(),
					ReplyMessage.PURCHASE_ITEM_ERROR.getCode());
		}

		purchaseItemDao.deleteById(purchaseItemId);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}
	
	/* 勾選 / 取消勾選購物項目 */
	public BasicRes updateCheck(int listId, int itemId, boolean check, int checkMan) {
		if(!purchaseItemDao.existsById(new PurchaseItemId(itemId, listId))) {
			return new BasicRes(ReplyMessage.PURCHASE_ITEM_ERROR.getMessage(),
					ReplyMessage.PURCHASE_ITEM_ERROR.getCode());
		}

		LocalDate checkDate = check ? LocalDate.now() : null;
		int checkedById = check ? checkMan : 0;
		purchaseItemDao.updateCheck(listId, itemId, check, checkDate, checkedById);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}

	private BasicRes checkShoppingList(ShoppingList shoppingList) {
		if(!StringUtils.hasText(shoppingList.getTitle())) {
			return new BasicRes(ReplyMessage.TITLE_ERROR.getMessage(), ReplyMessage.TITLE_ERROR.getCode());
		}
		if(shoppingList.getCreaterId() <= 0 || !userInfoDao.existsById(shoppingList.getCreaterId())) {
			return new BasicRes(ReplyMessage.CREATOR_ID_ERROR.getMessage(), ReplyMessage.CREATOR_ID_ERROR.getCode());
		}
		return null;
	}

	private List<PurchaseItem> buildPurchaseItemList(
			int listId, int createrId, List<PurchaseItemVo> purchaseItemVoList, LocalDate createdDate) {
		List<PurchaseItem> purchaseItemList = new ArrayList<>();
		int nextItemId = purchaseItemDao.getMaxIdByListId(listId) + 1;

		for(PurchaseItemVo vo : purchaseItemVoList) {
			if(vo == null || !StringUtils.hasText(vo.getItem()) || vo.getQuantity() <= 0) {
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
		}

		return purchaseItemList;
	}
	
}
