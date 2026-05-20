package com.example.Family_life_backend.vo;

import java.time.LocalDate;

import com.example.Family_life_backend.constants.ValidMessage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class PurchaseItemVo {

	@Min(value = 1, message = ValidMessage.PURCHASE_ID_ERROR)
	private int id;

	private int listId;

	private int createrId;

	private LocalDate createdDate;

	private int userId;

	private int categoryId;

	@NotBlank(message = ValidMessage.ITEM_IS_BLANK)
	private String item;

	@Min(value = 1, message = ValidMessage.QUANTITY_IS_NULL)
	private int quantity;
	
	private boolean check;
	 
	private LocalDate checkDate;
	 
	private int checkMan;
	
	public PurchaseItemVo() {
	}

	public PurchaseItemVo(int id, int listId, int createrId, LocalDate createdDate,
			int userId, int categoryId, String item, int quantity) {
		super();
		this.id = id;
		this.listId = listId;
		this.createrId = createrId;
		this.createdDate = createdDate;
		this.userId = userId;
		this.categoryId = categoryId;
		this.item = item;
		this.quantity = quantity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
