package com.example.Family_life_backend.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/* Shopping List 抓 shopping_list_id 用於分辨清單，雙PK做 Serializable：id & shopping_list_id*/
@Entity
@Table(name = "shopping_list_items")
public class PurchaseItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "shopping_list_id")
	private int listId;

	@Column(name = "created_by_id")
	private int createrId;

	@Column(name = "created_at")
	private LocalDate createdDate;

	@Column(name = "user_id")
	private int userId;

	@Column(name = "category_id")
	private int categoryId;
	
	@NotBlank
	@Column(name = "item_name")
	private String item;

	@NotNull
	@Column(name = "quantity")
	private int quantity;
	
	@Column(name = "is_checked")
	private boolean check;
	 
	@Column(name = "check_at")
	private LocalDate checkDate;
	 
	@Column(name = "checked_by_id")
	private int checkMan;

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

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public LocalDate getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(LocalDate checkDate) {
		this.checkDate = checkDate;
	}

	public int getCheckMan() {
		return checkMan;
	}

	public void setCheckMan(int checkMan) {
		this.checkMan = checkMan;
	}

	
	 
}
