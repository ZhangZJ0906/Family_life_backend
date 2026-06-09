package com.example.Family_life_backend.DTO;

import java.time.LocalDateTime;

public interface groupDTO {
	Long getGroupId();
	String getGroupName();
	String getInviteCode();
	Long getCreatedBy();
	LocalDateTime getCreatedAt();
	String getAvatar();
	String getCreater();
}
