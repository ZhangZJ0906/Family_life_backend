package com.example.Family_life_backend.request;

import java.time.LocalDate;

import jakarta.persistence.Column;

public class AddSubscriptionReq {
	
	private Integer groupId;
    private Integer userId;
    private String name;
    private Integer price;
    private String billingCycle;
    private LocalDate nextBillingDate;
    private LocalDate purchaseDate;
    private LocalDate trialEndDate;
    private Boolean notify;
    private String note;
    private String status;
    private String remindMessage;

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

    public LocalDate getNextBillingDate() {
        return nextBillingDate;
    }
    
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public LocalDate getTrialEndDate() {
        return trialEndDate;
    }
    
    public Boolean getNotify() {
        return notify;
}
    public String getNote() {
        return note;
}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemindMessage() {
		return remindMessage;
	}

	public void setRemindMessage(String remindMessage) {
		this.remindMessage = remindMessage;
	}
    
}
