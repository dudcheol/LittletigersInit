package com.example.parkyoungcheol.littletigersinit.Navigation.AR;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.parkyoungcheol.littletigersinit.R;

import butterknife.BindView;

public class ar_navigationActivity extends AppCompatActivity {

    @BindView(R.id.source_pick_btn)
    Button sourcePickBtn;
    @BindView(R.id.dest_pick_btn)
    Button destPickBtn;
    @BindView(R.id.nav_start_btn)
    Button navStartBtn;
    @BindView(R.id.source_result_text)
    TextView sourceResultText;
    @BindView(R.id.dest_result_text)
    TextView destResultText;
    @BindView(R.id.non_ar_nav_start_btn)
    Button mapNavStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_navigation);
    }
}
