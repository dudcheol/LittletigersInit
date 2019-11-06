package com.example.parkyoungcheol.littletigersinit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.parkyoungcheol.littletigersinit.Chat.ChatLoginActivity;
import com.example.parkyoungcheol.littletigersinit.Chat.User;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class CreateActivity extends AppCompatActivity {
    private Button mCreatebtn;
    private Button mCancelbtn;
    EditText mEmail_edittext, mNickname_edittext, mPassword_edittext, mPassword_check;


    private FirebaseAuth mAuth;

    private FirebaseAnalytics mFirebaseAnalytics;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserRef;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mEmail_edittext = (EditText) findViewById(R.id.email_edittext);
        mNickname_edittext = (EditText) findViewById(R.id.nickname_edittext);
        mPassword_edittext = (EditText) findViewById(R.id.password_edittext);
        mPassword_check = (EditText) findViewById(R.id.password_check);
        mCreatebtn = (Button) findViewById(R.id.create_button);
        mCancelbtn = (Button) findViewById(R.id.cancel_button);

        mAuth = FirebaseAuth.getInstance(); // 인스턴스 변수로 선언하여 Firebase 인증을 사용할 수 있게 초기화
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = mFirebaseDatabase.getReference("users");

        mCreatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((mEmail_edittext.getText().toString().replace(" ", "").equals("")) ||
                        (mPassword_edittext.getText().toString().replace(" ", "").equals(""))) {
                    Toast.makeText(CreateActivity.this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    createEmail();
                }
            }
        });
        mCancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createEmail() {
        if (mPassword_edittext.getText().toString().equals(mPassword_check.getText().toString())) {
            mAuth.createUserWithEmailAndPassword(mEmail_edittext.getText().toString(), mPassword_edittext.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = task.getResult().getUser();
                                final User user = new User(); // 데이터 변경 방지 final

                                //-- data set
                                user.setEmail(firebaseUser.getEmail());
                                if (firebaseUser.getDisplayName() != null)
                                    user.setName(firebaseUser.getDisplayName());
                                else
                                    user.setName(mNickname_edittext.getText().toString());
                                user.setUid(firebaseUser.getUid());

                                if (firebaseUser.getPhotoUrl() != null)
                                    user.setProfileUrl(firebaseUser.getPhotoUrl().toString());
                                else
                                    user.setProfileUrl("https://i.imgur.com/jCxAEpA.jpg");

                                mUserRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // 데이터가 존재하지 않을 때만 데이터를 새로 생성
                                        if (!dataSnapshot.exists()) {
                                            mUserRef.child(user.getUid()).setValue(user, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    if (databaseError == null) {
                                                        Toast.makeText(CreateActivity.this, "계정이 생성되었습니다.", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(CreateActivity.this, ChatLoginActivity.class)); // 로그인 성공 시, 메인으로
                                                        finish(); // 로그인 인증은 꺼줌
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(CreateActivity.this, "계정생성이 불가한 아이디입니다.", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }

                                        Bundle eventBundle = new Bundle();
                                        eventBundle.putString("email", user.getEmail());
                                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, eventBundle);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                //여기에 추가해야함
                                if (!mEmail_edittext.getText().toString().contains("@")) {
                                    Toast.makeText(CreateActivity.this, "아이디는 이메일 형식이여야 합니다.", Toast.LENGTH_SHORT).show();
                                } else if (mPassword_edittext.getText().toString().length() < 6) {
                                    Toast.makeText(CreateActivity.this, "비밀번호는 6자리 이상이여야 합니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CreateActivity.this, "이미있는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "비밀번호가 일치하지않습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
