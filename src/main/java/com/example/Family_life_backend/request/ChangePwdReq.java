package com.example.Family_life_backend.request;

import com.example.Family_life_backend.constants.ValidMessage;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePwdReq {

	@NotBlank(message = ValidMessage.EMAIL_ERROR)
	@Email(message = ValidMessage.EMAIL_ERROR)
	private String email;

	@NotBlank(message = ValidMessage.PASSWORD_ERROR)
	private String oldPwd;

	@NotBlank(message = ValidMessage.PASSWORD_ERROR)
	@Size(min = 6, message = ValidMessage.PASSWORD_ERROR)
	private String newPwd;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
