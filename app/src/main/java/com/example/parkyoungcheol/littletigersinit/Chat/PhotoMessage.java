package com.example.parkyoungcheol.littletigersinit.Chat;

import lombok.Data;

@Data
public class PhotoMessage extends Message {
    private String photoUrl;

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
