package com.example.parkyoungcheol.littletigersinit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.parkyoungcheol.littletigersinit.Navigation.CHAT.PeopleFragment;

public class Chat_MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //test//
        setContentView(R.layout.activity_chat_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.ChatMainActivity_framelayout, new PeopleFragment()).commit();
    }
}
