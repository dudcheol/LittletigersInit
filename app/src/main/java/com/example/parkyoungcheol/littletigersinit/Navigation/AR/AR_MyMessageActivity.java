package com.example.parkyoungcheol.littletigersinit.Navigation.AR;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.parkyoungcheol.littletigersinit.R;

public class AR_MyMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar__my_message);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.non_anim,R.anim.push_down_out);
    }
}
