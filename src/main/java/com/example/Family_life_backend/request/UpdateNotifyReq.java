package com.example.Family_life_backend.request;

import jakarta.validation.constraints.NotNull;

public class UpdateNotifyReq {
	@NotNull
	private Integer id;
	@NotNull
	private Boolean notify;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getNotify() {
		return notify;
	}

	public void setNotify(Boolean notify) {
		this.notify = notify;
	}

	public UpdateNotifyReq(@NotNull Integer id, @NotNull Boolean notify) {
		super();
		this.id = id;
		this.notify = notify;
	}

	public UpdateNotifyReq() {
		super();
		// TODO Auto-generated constructor stub
	}

}
