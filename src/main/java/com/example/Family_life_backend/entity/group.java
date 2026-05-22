package com.example.Family_life_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "`groups`")
public class group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    @Column(name = "invite_code", nullable = false, length = 20, unique = true)
    private String inviteCode;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "avatar", nullable = false, length = 100)
    private String Avatar;
    
    @Column(name = "creater", nullable = false)
    private String creater;
    
    // ===== Getter / Setter =====

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

	public String getAvatar() {
		return Avatar;
	}

	public void setAvatar(String avatar) {
		Avatar = avatar;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public group() {
		super();
		// TODO Auto-generated constructor stub
	}

	public group(Long groupId, String groupName, String inviteCode, Long createdBy, LocalDateTime createdAt, String avatar, String creater) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.inviteCode = inviteCode;
		this.createdBy = createdBy;
		this.createdAt = createdAt;
		this.Avatar = avatar;
		this.creater = creater;
	}
    
}