package com.example.Family_life_backend.request;

import com.example.Family_life_backend.constants.ValidMessage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePwdReq {

	@Min(value = 1, message = ValidMessage.USER_ID_ERROR)
	private int userId;

	@NotBlank(message = ValidMessage.PASSWORD_ERROR)
	private String oldPwd;

	@NotBlank(message = ValidMessage.PASSWORD_ERROR)
	@Size(min = 6, message = ValidMessage.PASSWORD_ERROR)
	private String newPwd;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getOldPwd() {
		return oldPwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}

}
