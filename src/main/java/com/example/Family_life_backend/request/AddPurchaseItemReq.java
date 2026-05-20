package com.example.Family_life_backend.request;

import java.util.List;

import com.example.Family_life_backend.constants.ValidMessage;
import com.example.Family_life_backend.vo.PurchaseItemVo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public class AddPurchaseItemReq {

	@Min(value = 1, message = ValidMessage.PURCHASE_ID_ERROR)
	private int listId;

	@Min(value = 1, message = ValidMessage.CREATOR_ID_ERROR)
	private int createrId;

	@Valid
	@NotEmpty(message = ValidMessage.PURCHASEITEMVO_IS_EMPTY)
	private List<PurchaseItemVo> purchaseItemVoList;

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

	public int getCreaterId() {
		return createrId;
	}

	public void setCreaterId(int createrId) {
		this.createrId = createrId;
	}

	public List<PurchaseItemVo> getPurchaseItemVoList() {
		return purchaseItemVoList;
	}

	public void setPurchaseItemVoList(List<PurchaseItemVo> purchaseItemVoList) {
		this.purchaseItemVoList = purchaseItemVoList;
	}

}
