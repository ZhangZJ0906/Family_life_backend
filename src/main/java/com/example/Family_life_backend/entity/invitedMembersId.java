package com.example.Family_life_backend.entity;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("serial")
public class invitedMembersId implements Serializable{

	private Long user_id;

	private Long group_id;

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Long getGroup_id() {
		return group_id;
	}

	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}

	public invitedMembersId(Long user_id, Long group_id) {
		super();
		this.user_id = user_id;
		this.group_id = group_id;
	}

	public invitedMembersId() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean equals(Object o) {
		/* 檢查是否為同一個記憶體位址 */
		if (this == o)
			return true;
		/* 檢查物件是否為 null 或類別不一致 */
		if (o == null || getClass() != o.getClass())
			return false;
		/* 轉型後比較欄位內容 */
		invitedMembersId that = (invitedMembersId) o;
		return group_id == that.group_id && user_id == that.user_id;
	}

	@Override
	public int hashCode() {
		// 根據欄位內容產生 Hash 值
		return Objects.hash(group_id, user_id);
	}
}
