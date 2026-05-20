package com.example.Family_life_backend.vo;

public class GroupMembersVo {
	private Long groupId;

    private Long userId;

    private Integer publicInventory;

    public GroupMembersVo() {
    	super();
    }

    public GroupMembersVo(Long groupId, Long userId, Integer publicInventory) {
    	super();
        this.groupId = groupId;
        this.userId = userId;
        this.publicInventory = publicInventory;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPublicInventory() {
        return publicInventory;
    }

    public void setPublicInventory(Integer publicInventory) {
        this.publicInventory = publicInventory;
    }
}
