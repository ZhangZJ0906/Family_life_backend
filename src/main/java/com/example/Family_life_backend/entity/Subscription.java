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
	@Column(name = "avatar")
	private String avatar;

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
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

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getBillingCycle() {
		return billingCycle;
	}

	public void setBillingCycle(String billingCycle) {
		this.billingCycle = billingCycle;
	}

	public LocalDate getNextBillingDate() {
		return nextBillingDate;
	}

	public void setNextBillingDate(LocalDate nextBillingDate) {
		this.nextBillingDate = nextBillingDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(LocalDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public LocalDate getTrialEndDate() {
		return trialEndDate;
	}

	public void setTrialEndDate(LocalDate trialEndDate) {
		this.trialEndDate = trialEndDate;
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

	public String getRemindMessage() {
		return remindMessage;
	}

	public void setRemindMessage(String remindMessage) {
		this.remindMessage = remindMessage;
	}

	public Subscription(Integer id, Integer groupId, Integer userId, String name, Integer price, String billingCycle,
			LocalDate nextBillingDate, LocalDateTime createdAt, LocalDate purchaseDate, LocalDate trialEndDate,
			Boolean notify, String note, String status, String remindMessage, String avatar) {
		super();
		this.id = id;
		this.groupId = groupId;
		this.userId = userId;
		this.name = name;
		this.price = price;
		this.billingCycle = billingCycle;
		this.nextBillingDate = nextBillingDate;
		this.createdAt = createdAt;
		this.purchaseDate = purchaseDate;
		this.trialEndDate = trialEndDate;
		this.notify = notify;
		this.note = note;
		this.status = status;
		this.remindMessage = remindMessage;
		this.avatar = avatar;
	}

	public Subscription() {
		super();
		// TODO Auto-generated constructor stub
	}

}