package com.example.parkyoungcheol.littletigersinit.Chat;

import java.util.Date;

import lombok.Data;

@Data
public class Chat {
    private String chatId;
    private String title;
    private Date createDate;
    private TextMessage lastMessage;
    private boolean disabled;
    private int totalUnreadCount;

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setLastMessage(TextMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setTotalUnreadCount(int totalUnreadCount) {
        this.totalUnreadCount = totalUnreadCount;
    }

    public String getChatId() {
        return chatId;
    }

    public String getTitle() {
        return title;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public TextMessage getLastMessage() {
        return lastMessage;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public int getTotalUnreadCount() {
        return totalUnreadCount;
    }
}
