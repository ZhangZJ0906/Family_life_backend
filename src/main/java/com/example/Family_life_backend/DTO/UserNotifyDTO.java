package com.example.Family_life_backend.DTO;

import java.time.LocalDateTime;

public interface UserNotifyDTO {
	Long getId();
    Long getSendUserId();
    Long getGetUserId();
    String getContent();
    String getType();
    Boolean getIsRead();
    LocalDateTime getSendDate();
    Long getTargetGroupId();
    String getStatus();
    String getName();
    String getAvatar();
}
