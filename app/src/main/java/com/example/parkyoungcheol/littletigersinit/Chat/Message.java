package com.example.parkyoungcheol.littletigersinit.Chat;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Message {

    private String messageId;
    private User messageUser;
    private String chatId;
    private int unreadCount;
    private Date messageDate;
    private MessageType messageType;
    private List<String> readUserList;
    private double longitudedb;
    private double latitudedb;

    public enum MessageType{
        TEXT, PHOTO, LOCATION,EXIT
    }
}
