package com.example.Family_life_backend.respond;

import java.util.List;

import com.example.Family_life_backend.DTO.invitedMembersDTO;

public class getInviteMembersRes extends BasicResponse {
	List<invitedMembersDTO> invitedMembersList;

	public List<invitedMembersDTO> getInvitedMembersList() {
		return invitedMembersList;
	}

	public void setInvitedMembersList(List<invitedMembersDTO> invitedMembersList) {
		this.invitedMembersList = invitedMembersList;
	}

	public getInviteMembersRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public getInviteMembersRes(String message, int code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public getInviteMembersRes(String message, int code, List<invitedMembersDTO> invitedMembersList) {
		super(message, code);
		this.invitedMembersList = invitedMembersList;
	}

}
