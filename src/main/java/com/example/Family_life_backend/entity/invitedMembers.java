package com.example.Family_life_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "invited_members")
@IdClass(value = invitedMembersId.class)
public class invitedMembers {
	@Id
	@Column(name = "group_id")
	private Long group_id;

	@Id
	@Column(name = "user_id")
	private Long user_id;

	@Column(name = "name", nullable = false)
	private String user_name;
	
	@Column(name = "avatar", nullable = false)
	private String avatar;

	public Long getGroup_id() {
		return group_id;
	}

	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public invitedMembers() {
		super();
		// TODO Auto-generated constructor stub
	}

	public invitedMembers(Long group_id, Long user_id, String user_name, String avatar) {
		super();
		this.group_id = group_id;
		this.user_id = user_id;
		this.user_name = user_name;
		this.avatar = avatar;
	}
	
}
