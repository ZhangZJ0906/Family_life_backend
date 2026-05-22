package com.example.Family_life_backend.request;

import jakarta.validation.constraints.NotBlank;

public class CreateGroupReq {

	@NotBlank(message = "名字不能空")
	private String groupName;

	private long createBy;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(long createBy) {
		this.createBy = createBy;
	}

}
