package com.example.parkyoungcheol.littletigersinit.Chat;

import lombok.Data;

@Data
public class User {
    private String uid, email, name, profileUrl;
    private boolean selection;

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
