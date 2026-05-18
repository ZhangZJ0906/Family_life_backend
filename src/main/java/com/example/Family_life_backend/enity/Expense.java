package com.example.Family_life_backend.enity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "expenses")
public class Expense {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "group_id")
	private Long groupId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "price")
	private int price;
	@Column(name = "category_id")
	private int categoryId;

	@Column(name = "related_item_id")
	private Long relatedItemId;
	@Column(name = "related_item_name")
	private String relatedItemName;

	@Column(name = "expense_date")
	private LocalDate expenseDate;

	@Column(name = "note")
	private String note;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public Expense() {
		super();
		// TODO Auto-generated constructor stub
	}



	public String getRelatedItemName() {
		return relatedItemName;
	}

	public void setRelatedItemName(String relatedItemName) {
		this.relatedItemName = relatedItemName;
	}

	public Expense(Long id, Long groupId, Long userId, int price, int categoryId, Long relatedItemId,
			String relatedItemName, LocalDate expenseDate, String note, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.groupId = groupId;
		this.userId = userId;
		this.price = price;
		this.categoryId = categoryId;
		this.relatedItemId = relatedItemId;
		this.relatedItemName = relatedItemName;
		this.expenseDate = expenseDate;
		this.note = note;
		this.createdAt = createdAt;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public Long getRelatedItemId() {
		return relatedItemId;
	}

	public void setRelatedItemId(Long relatedItemId) {
		this.relatedItemId = relatedItemId;
	}

	public LocalDate getExpenseDate() {
		return expenseDate;
	}

	public void setExpenseDate(LocalDate expenseDate) {
		this.expenseDate = expenseDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}