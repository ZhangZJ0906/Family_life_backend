package com.example.Family_life_backend.enity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "items")
public class Items {
	@Id
	@Column(name = "id")
	private int id;

	@Column(name = "group_id")
	private int groupId;

	@Column(name = "category_id")
	private int categoryId;

	@Column(name = "created_by_id")
	private int createdById;

	@Column(name = "name")
	private String name;

	@Column(name = "quantity")
	private Integer quantity = 0;

	@Column(name = "unit")
	private String unit;

	@Column(name = "location_id")
	private Long locationId;

	@Column(name = "purchase_date")
	private LocalDate purchaseDate;

	@Column(name = "expire_date")
	private LocalDate expireDate;

	@Column(name = "safe_quantity")
	private Integer safeQuantity = 0;
	@Column(name = "unit_price")
	private int unitPrice;
	@Column(name = "price")
	private Integer price;

	@Column(name = "notify")
	private Boolean notify = false;

	@Column(name = "note")
	private String note;



	public Items(int id, int groupId, int categoryId, int createdById, String name, Integer quantity, String unit,
			Long locationId, LocalDate purchaseDate, LocalDate expireDate, Integer safeQuantity, int unitPrice,
			Integer price, Boolean notify, String note, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.groupId = groupId;
		this.categoryId = categoryId;
		this.createdById = createdById;
		this.name = name;
		this.quantity = quantity;
		this.unit = unit;
		this.locationId = locationId;
		this.purchaseDate = purchaseDate;
		this.expireDate = expireDate;
		this.safeQuantity = safeQuantity;
		this.unitPrice = unitPrice;
		this.price = price;
		this.notify = notify;
		this.note = note;
		this.createdAt = createdAt;
	}

	public int getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(int unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Items() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getCreatedById() {
		return createdById;
	}

	public void setCreatedById(int createdById) {
		this.createdById = createdById;
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

	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(LocalDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public LocalDate getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(LocalDate expireDate) {
		this.expireDate = expireDate;
	}

	public Integer getSafeQuantity() {
		return safeQuantity;
	}

	public void setSafeQuantity(Integer safeQuantity) {
		this.safeQuantity = safeQuantity;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
