package com.example.Family_life_backend.DTO;

public class OnlineUserDTO {
	private Long id;
	private String name;
	private String avatar;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public OnlineUserDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OnlineUserDTO(Long id, String name, String avatar) {
		super();
		this.id = id;
		this.name = name;
		this.avatar = avatar;
	}

}
