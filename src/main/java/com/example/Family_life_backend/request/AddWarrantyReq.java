package com.example.Family_life_backend.request;

import java.time.LocalDate;

public class AddWarrantyReq {

	private Integer groupId;
    private Integer userId;
    private String productName;
    private String brand;
    private String model;
    private String serialNumber;
    private LocalDate purchaseDate;
    private LocalDate warrantyEndDate;
    private String storeName;
    private Integer price;
    private Boolean notify;
    private String note;

    public Integer getGroupId() {
        return groupId;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getProductName() {
        return productName;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public LocalDate getWarrantyEndDate() {
        return warrantyEndDate;
    }

    public String getStoreName() {
        return storeName;
    }

    public Integer getPrice() {
        return price;
    }

    public Boolean getNotify() {
        return notify;
    }

    public String getNote() {
        return note;
    }
}
