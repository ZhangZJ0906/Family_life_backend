package com.example.Family_life_backend.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public class AddExpensesInfoReq {

	private Long groupId;
	@NotNull(message = "userId 不可為空") // 修正：Long 必須用 @NotNull
	private Long userId;

	@NotNull(message = "價格不可為空") // 改用 Integer 才能判斷是否未傳
	@Min(value = 0, message = "價格不能小於 0") // 依業務邏輯調整，通常價格不能為負數
	private Integer price;

	@NotNull(message = "分類 ID 不可為空") // 改用 Integer 才能判斷是否未傳
	private Integer categoryId;

	// 非必填欄位，允許為 null
	private Long relatedItemId;

	private String relatedItemName;

	@NotNull(message = "記帳日期不可為空")
	@PastOrPresent(message = "記帳日期不能是未來的時間") // 限制不能選未來日期
	private LocalDate expenseDate;

	@Size(max = 500, message = "備註不可超過 500 個字") // 限制字數，防止資料庫報錯
	private String note;

	private LocalDateTime createdAt;

	public AddExpensesInfoReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getRelatedItemName() {
		return relatedItemName;
	}

	public void setRelatedItemName(String relatedItemName) {
		this.relatedItemName = relatedItemName;
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

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
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

	public AddExpensesInfoReq(Long groupId, Long userId, Integer price, Integer categoryId, Long relatedItemId,
			String relatedItemName, LocalDate expenseDate, String note, LocalDateTime createdAt) {
		super();
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

}
