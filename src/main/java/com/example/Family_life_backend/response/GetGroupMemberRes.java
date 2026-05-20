package com.example.Family_life_backend.response;

import java.util.List;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.entity.GroupMembers;

public class GetGroupMemberRes extends BasicResponse{
	private List<groupMembersDTO> groupMembersList;

	public List<groupMembersDTO> getGroupMembersList() {
		return groupMembersList;
	}

	public void setGroupMembersList(List<groupMembersDTO> groupMembersList) {
		this.groupMembersList = groupMembersList;
	}

	public GetGroupMemberRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetGroupMemberRes(String message, int code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public GetGroupMemberRes(String message, int code, List<groupMembersDTO> groupMembersList) {
		super(message, code);
		this.groupMembersList = groupMembersList;
	}
	
	
}
