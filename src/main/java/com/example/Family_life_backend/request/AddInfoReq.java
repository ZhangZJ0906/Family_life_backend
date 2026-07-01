package com.example.Family_life_backend.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddInfoReq {

	private int userId;

	@NotBlank(message = "EMAIL_ERROR")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "EMAIL_ERROR")
	private String email;

	@NotBlank(message = "PASSWORD_ERROR")
	@Size(min = 6, message = "PASSWORD_ERROR")
	private String pwd;

	private String userName;
	private String avatar;
	private boolean notify;

//	private LocalDate createdDate;
//
//	private LocalDateTime loginItemListPageTime = LocalDateTime.now(ZoneId.of("Asia/Taipei"));
//
//	private LocalDateTime loginCalendarPageTime = LocalDateTime.now(ZoneId.of("Asia/Taipei"));
//
//	private LocalDateTime loginExpensePageTime = LocalDateTime.now(ZoneId.of("Asia/Taipei"));
//
//	@PrePersist
//	public void prePersist() {
//		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Taipei"));
//
//		if (createdDate == null)
//			createdDate = LocalDate.now(ZoneId.of("Asia/Taipei"));
//		
//		if (loginItemListPageTime == null)
//			loginItemListPageTime = now;
//
//		if (loginCalendarPageTime == null)
//			loginCalendarPageTime = now;
//
//		if (loginExpensePageTime == null)
//			loginExpensePageTime = now;
//	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public boolean isNotify() {
		return notify;
	}

	public void setNotify(boolean notify) {
		this.notify = notify;
	}

}
