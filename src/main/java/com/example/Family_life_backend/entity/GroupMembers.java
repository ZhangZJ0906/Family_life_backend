package com.example.Family_life_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "group_members")
@IdClass(value = GroupMembersId.class)
public class GroupMembers {

	@Id
	@Column(name = "group_id")
	private Long group_id;

	@Id
	@Column(name = "user_id")
	private Long user_id;

	@Column(name = "public_inventory", nullable = false)
	private Integer publicInventory = 0;
	
	@Transient
	private String user_name;

	@Transient
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

	public Integer getPublicInventory() {
		return publicInventory;
	}

	public void setPublicInventory(Integer publicInventory) {
		this.publicInventory = publicInventory;
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

	public GroupMembers(Long group_id, Long user_id, Integer publicInventory, String userName, String avatar) {
		super();
		this.group_id = group_id;
		this.user_id = user_id;
		this.publicInventory = publicInventory;
		this.user_name = userName;
		this.avatar = avatar;
	}

	public GroupMembers() {
		super();
		// TODO Auto-generated constructor stub
	}

}
