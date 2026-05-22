package com.example.Family_life_backend.response;

import java.util.List;

import com.example.Family_life_backend.entity.invitedMembers;

public class getInvitedMemberRes extends BasicResponse {
	private List<invitedMembers> invitedMemberList;

	public List<invitedMembers> getInvitedMemberList() {
		return invitedMemberList;
	}

	public void setInvitedMemberList(List<invitedMembers> invitedMemberList) {
		this.invitedMemberList = invitedMemberList;
	}

	public getInvitedMemberRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public getInvitedMemberRes(String message, int code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public getInvitedMemberRes(String message, int code, List<invitedMembers> invitedMemberList) {
		super(message, code);
		this.invitedMemberList = invitedMemberList;
	}

}
