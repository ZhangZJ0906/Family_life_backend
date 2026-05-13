package com.example.Family_life_backend.request;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public class ItemAddInfoReq {
	@NotNull(message = "創建者 ID 為必填")
	private int userId;

	private Integer groupId;

	@NotNull(message = "分類 ID 為必填")
	private Integer categoryId;

	@NotBlank(message = "物品名稱不能為空")
	@Size(max = 50, message = "名稱長度不能超過 50 個字")
	private String name;

	@NotNull(message = "數量為必填")
	@Min(value = 0, message = "數量不能小於 0")
	private Integer quantity;

	@NotBlank(message = "單位不能為空")
	private String unit;

	@NotNull(message = "地點 ID 為必填")
	private Long locationId;

	@NotNull(message = "單價為必填")
	@Min(value = 0, message = "單價不能小於 0")
	private Integer price;

	@NotNull(message = "購買日期為必填")
	@PastOrPresent(message = "購買日期不能是未來")
	private LocalDate purchaseDate;
	@FutureOrPresent(message = "到期日期不能早於今天")
	private LocalDate expireDate;

	private Boolean notify = false;

	private String note;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(LocalDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public LocalDate getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(LocalDate expireDate) {
		this.expireDate = expireDate;
	}

	public Boolean getNotify() {
		return notify;
	}

	public void setNotify(Boolean notify) {
		this.notify = notify;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public ItemAddInfoReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ItemAddInfoReq(Integer groupId,
			Integer categoryId,
			String name,
			Integer quantity,
			String unit, Long locationId,
			Integer price,
			LocalDate expireDate, Boolean notify, String note) {
		super();
		this.groupId = groupId;
		this.categoryId = categoryId;
		this.name = name;
		this.quantity = quantity;
		this.unit = unit;
		this.locationId = locationId;
		this.price = price;
		this.expireDate = expireDate;
		this.notify = notify;
		this.note = note;
	}

}
