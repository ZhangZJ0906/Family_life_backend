package com.example.Family_life_backend.DTO;

public class GroupChatReadCountDTO {

    private Long messageId;
    private Long count;

    public GroupChatReadCountDTO() {
    }

    public GroupChatReadCountDTO(Long messageId, Long count) {
        this.messageId = messageId;
        this.count = count;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}