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
import com.example.Family_life_backend.entity.ShoppingList;
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

	@Transactional (rollbackOn = Exception.class)
	public BasicRes create(CreateListReq req) {
		if(req == null || req.getShoppingList() == null) {
			return new BasicRes(ReplyMessage.TITLE_ERROR.getMessage(), ReplyMessage.TITLE_ERROR.getCode());
		}

		ShoppingList shoppingList = req.getShoppingList();
		List<PurchaseItemVo> purchaseItemVoList = req.getPurchaseItemVoList();

		if(!StringUtils.hasText(shoppingList.getTitle())) {
			return new BasicRes(ReplyMessage.TITLE_ERROR.getMessage(), ReplyMessage.TITLE_ERROR.getCode());
		}
		
		if(CollectionUtils.isEmpty(purchaseItemVoList)) {
			return new BasicRes(ReplyMessage.PURCHASE_ITEM_ERROR.getMessage(),
					ReplyMessage.PURCHASE_ITEM_ERROR.getCode());
		}

		LocalDate now = LocalDate.now();
		shoppingList.setCreatedDate(now);
		ShoppingList savedShoppingList = shoppingListDao.save(shoppingList);

		List<PurchaseItem> purchaseItemList = new ArrayList<>();
		for (PurchaseItemVo vo : purchaseItemVoList) {
			if(vo == null || !StringUtils.hasText(vo.getItem()) || vo.getQuantity() <= 0) {
				return new BasicRes(ReplyMessage.PURCHASE_ITEM_ERROR.getMessage(), ReplyMessage.PURCHASE_ITEM_ERROR.getCode());
			}

			PurchaseItem purchaseItem = new PurchaseItem();
			purchaseItem.setListId(savedShoppingList.getId());
			purchaseItem.setCreaterId(shoppingList.getCreaterId());
			purchaseItem.setCreatedDate(now);
			purchaseItem.setUserId(vo.getUserId());
			purchaseItem.setCategoryId(vo.getCategoryId());
			purchaseItem.setItem(vo.getItem());
			purchaseItem.setQuantity(vo.getQuantity());
			purchaseItem.setCheck(false);
			purchaseItemList.add(purchaseItem);
		}
		purchaseItemDao.saveAll(purchaseItemList);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}
	
}
