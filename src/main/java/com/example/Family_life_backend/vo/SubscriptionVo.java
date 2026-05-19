package com.example.Family_life_backend.vo;

import java.time.LocalDate;

public class SubscriptionVo {

	private Integer id;
    private Integer groupId;
    private Integer userId;
    private String name;
    private Integer price;
    private String billingCycle;
    private LocalDate purchaseDate;
    private LocalDate trialEndDate;
    private LocalDate nextBillingDate;

    private String status;
    private String remindMessage;
    private Boolean notify;
    private String note;

    public SubscriptionVo(
            Integer id,
            Integer groupId,
            Integer userId,
            String name,
            Integer price,
            String billingCycle,
            LocalDate purchaseDate,
            LocalDate trialEndDate,
            LocalDate nextBillingDate,
            String status,
            String remindMessage,
            Boolean notify,
            String note) {

        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
        this.name = name;
        this.price = price;
        this.billingCycle = billingCycle;
        this.purchaseDate = purchaseDate;
        this.trialEndDate = trialEndDate;
        this.nextBillingDate = nextBillingDate;
        this.status = status;
        this.remindMessage = remindMessage;
        this.notify = notify;
        this.note = note;
        
    }

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

    public Integer getPrice() {
        return price;
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public LocalDate getTrialEndDate() {
        return trialEndDate;
    }

    public LocalDate getNextBillingDate() {
        return nextBillingDate;
    }

    public String getStatus() {
        return status;
    }

    public String getRemindMessage() {
        return remindMessage;
    }
    
    public Boolean getNotify() {
        return notify;
    }
    
    public String getNote() {
        return note;
    }
}
