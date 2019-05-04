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
}
