package com.example.Family_life_backend.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

	private LocalDateTime createdAt;

	private String avatar;




	public SubscriptionVo(Integer id, Integer groupId, Integer userId, String name, Integer price, String billingCycle,
			LocalDate purchaseDate, LocalDate trialEndDate, LocalDate nextBillingDate, String status,
			String remindMessage, Boolean notify, String note, String avatar, LocalDateTime createdAt) {
		super();
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
		this.avatar = avatar;
		this.createdAt = createdAt;
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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}
