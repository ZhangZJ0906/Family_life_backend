package com.example.Family_life_backend.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserInfo {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "user_id")
	private int userId;

	@Column(name = "name")
	private String userName;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String pwd;

	@Column(name = "avatar")
	private String avatar;

	@Column(name = "is_notify_by_enddate")
	private boolean isNotifyByEndDate;
	
	@Column(name = "is_notify_by_email")
	private boolean isNotifyByEmail;

	@Column(name = "created_at")
	private LocalDate createdDate;

	@Column(name = "updated_at")
	private LocalDate updateDate;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public boolean isNotifyByEndDate() {
		return isNotifyByEndDate;
	}

	public void setNotifyByEndDate(boolean isNotifyByEndDate) {
		this.isNotifyByEndDate = isNotifyByEndDate;
	}

	public boolean isNotifyByEmail() {
		return isNotifyByEmail;
	}

	public void setNotifyByEmail(boolean isNotifyByEmail) {
		this.isNotifyByEmail = isNotifyByEmail;
	}

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDate getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(LocalDate updateDate) {
		this.updateDate = updateDate;
	}

}
