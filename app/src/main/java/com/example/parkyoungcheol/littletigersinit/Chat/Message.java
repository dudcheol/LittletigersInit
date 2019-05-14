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

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setMessageUser(User messageUser) {
        this.messageUser = messageUser;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public void setReadUserList(List<String> readUserList) {
        this.readUserList = readUserList;
    }

    public void setLongitudedb(double longitudedb) {
        this.longitudedb = longitudedb;
    }

    public void setLatitudedb(double latitudedb) {
        this.latitudedb = latitudedb;
    }

    public String getMessageId() {
        return messageId;
    }

    public User getMessageUser() {
        return messageUser;
    }

    public String getChatId() {
        return chatId;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public List<String> getReadUserList() {
        return readUserList;
    }

    public double getLongitudedb() {
        return longitudedb;
    }

    public double getLatitudedb() {
        return latitudedb;
    }

    public enum MessageType{
        TEXT, PHOTO, LOCATION,EXIT
    }
}
