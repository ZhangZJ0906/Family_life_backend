package com.example.Family_life_backend.request;

import java.util.List;

import com.example.Family_life_backend.constants.ValidMessage;
import com.example.Family_life_backend.entity.ShoppingList;
import com.example.Family_life_backend.vo.PurchaseItemVo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class CreateListReq {

	@Valid
	@NotNull(message = ValidMessage.SHOPPINGLIST_IS_NULL)
	private ShoppingList shoppingList;

	@Valid
	private List<PurchaseItemVo> purchaseItemVoList;

	public ShoppingList getShoppingList() {
		return shoppingList;
	}

	public void setShoppingList(ShoppingList shoppingList) {
		this.shoppingList = shoppingList;
	}

	public List<PurchaseItemVo> getPurchaseItemVoList() {
		return purchaseItemVoList;
	}

	public void setPurchaseItemVoList(List<PurchaseItemVo> purchaseItemVoList) {
		this.purchaseItemVoList = purchaseItemVoList;
	}

}
