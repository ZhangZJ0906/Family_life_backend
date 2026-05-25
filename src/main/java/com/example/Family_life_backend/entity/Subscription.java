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
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Integer price;

    @Column(name = "billing_cycle")
    private String billingCycle;

    @Column(name = "next_billing_date")
    private LocalDate nextBillingDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "trial_end_date")
    private LocalDate trialEndDate;
    
    @Column(name = "notify")
    private Boolean notify;
    
    @Column(name = "note")
    private String note;
    
    
    @Column(name = "status")
    private String status;

    @Column(name = "remind_message")
    private String remindMessage;

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

    public LocalDate getNextBillingDate() {
        return nextBillingDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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