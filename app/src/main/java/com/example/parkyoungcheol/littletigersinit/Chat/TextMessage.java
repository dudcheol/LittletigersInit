package com.example.parkyoungcheol.littletigersinit.Chat;

import lombok.Data;

@Data
public class TextMessage extends Message {
    private String messageText;

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageText() {
        return messageText;
    }
}
