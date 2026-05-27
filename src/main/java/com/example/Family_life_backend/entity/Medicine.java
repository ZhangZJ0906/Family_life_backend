package com.example.Family_life_backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "medicines")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "name")
    private String name;

    @Column(name = "medicine_type")
    private String medicineType;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "unit")
    private String unit;
    
    @Column(name = "unit_price")
    private Integer unitPrice;

    @Column(name = "price")
    private Integer price;

    @Column(name = "safe_quantity")
    private Integer safeQuantity;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "expire_date")
    private LocalDate expireDate;

    @Column(name = "dosage")
    private String dosage;

    @Column(name = "usage_method")
    private String usageMethod;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "source")
    private String source;
    
    @Column(name = "notify")
    private Boolean notify;
    
    @Column(name = "note")
    private String note;
    
    @Column(name = "status")
    private String status;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;
    
    @Column(name = "remind_message")
    private String remindMessage;
    
    public Medicine() {
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMedicineType() {
		return medicineType;
	}

	public void setMedicineType(String medicineType) {
		this.medicineType = medicineType;
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

	public Integer getSafeQuantity() {
		return safeQuantity;
	}

	public void setSafeQuantity(Integer safeQuantity) {
		this.safeQuantity = safeQuantity;
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

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public String getUsageMethod() {
		return usageMethod;
	}

	public void setUsageMethod(String usageMethod) {
		this.usageMethod = usageMethod;
	}


	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
	public Integer getUnitPrice() {
	    return unitPrice;
	}

	public void setUnitPrice(Integer unitPrice) {
	    this.unitPrice = unitPrice;
	}

	public Integer getPrice() {
	    return price;
	}

	public void setPrice(Integer price) {
	    this.price = price;
	}
	
	public String getRemindMessage() {
	    return remindMessage;
	}

	public void setRemindMessage(String remindMessage) {
	    this.remindMessage = remindMessage;
	}

}
