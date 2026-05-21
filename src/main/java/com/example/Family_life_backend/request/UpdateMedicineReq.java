package com.example.Family_life_backend.request;

import java.time.LocalDate;

public class UpdateMedicineReq {

	 private Integer id;
	    private Integer groupId;
	    private Integer userId;
	    private String name;
	    private String medicineType;
	    private Integer quantity;
	    private String unit;
	    private Integer safeQuantity;
	    private LocalDate purchaseDate;
	    private LocalDate openDate;
	    private LocalDate expireDate;
	    private String dosage;
	    private String usageMethod;
	    private String frequency;
	    private String location;
	    private String source;
	    private Boolean notify;
	    private String note;

	    public Integer getId() {
	        return id;
	    }

	    public Integer getGroupId() {
	        return groupId;
	    }

	    public Integer getUserId() {
	        return userId;
	    }

	    public String getName() {
	        return name;
	    }

	    public String getMedicineType() {
	        return medicineType;
	    }

	    public Integer getQuantity() {
	        return quantity;
	    }

	    public String getUnit() {
	        return unit;
	    }

	    public Integer getSafeQuantity() {
	        return safeQuantity;
	    }

	    public LocalDate getPurchaseDate() {
	        return purchaseDate;
	    }

	    public LocalDate getOpenDate() {
	        return openDate;
	    }

	    public LocalDate getExpireDate() {
	        return expireDate;
	    }

	    public String getDosage() {
	        return dosage;
	    }

	    public String getUsageMethod() {
	        return usageMethod;
	    }

	    public String getFrequency() {
	        return frequency;
	    }

	    public String getLocation() {
	        return location;
	    }

	    public String getSource() {
	        return source;
	    }

	    public Boolean getNotify() {
	        return notify;
	    }

	    public String getNote() {
	        return note;
	    }
}
