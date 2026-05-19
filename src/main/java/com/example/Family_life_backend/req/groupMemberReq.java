package com.example.Family_life_backend.req;

import com.example.Family_life_backend.entity.GroupMembers;

import jakarta.validation.Valid;

public class groupMemberReq extends GroupMembers{
	private Long sendUserId;

    public Long getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(Long sendUserId) {
        this.sendUserId = sendUserId;
    }
}
