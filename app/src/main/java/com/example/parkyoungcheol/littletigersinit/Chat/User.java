package com.example.parkyoungcheol.littletigersinit.Chat;

import lombok.Data;


@Data
public class User {
    private String uid, email, name, profileUrl;
    private boolean selection;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setSelection(boolean selection) {
        this.selection = selection;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public boolean isSelection() {
        return selection;
    }

    public User() {
        // lombok을 사용하기 때문에 반드시 기본 생성자가 있어야함.
    }

    public User(String uid, String email, String name, String profileUrl) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.profileUrl = profileUrl;
    }


}
