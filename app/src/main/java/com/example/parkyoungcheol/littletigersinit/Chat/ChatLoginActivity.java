package com.example.parkyoungcheol.littletigersinit.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import android.widget.Toast;

import com.example.parkyoungcheol.littletigersinit.MainActivity;
import com.example.parkyoungcheol.littletigersinit.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SignInButton mGoogleSignInbtn; // 로그인 버튼
    private ProgressBar mProgressBar;
    private Button mEmailLoginbtn;
    private EditText mEmail_edittext;
    private EditText mPassword_edittext;

    private static final int RC_SIGN_IN  = 1000;

    private FirebaseAuth mAuth;

    private GoogleApiClient mGoogleApiClient;

    private FirebaseAnalytics mFirebaseAnalytics;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserRef;

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_login);

        mGoogleSignInbtn = (SignInButton) findViewById(R.id.google_sign_in_button);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mEmailLoginbtn = (Button) findViewById(R.id.sign_in_button);

        mEmail_edittext = (EditText) findViewById(R.id.email_edittext);
        mPassword_edittext = (EditText) findViewById(R.id.password_edittext);


        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,mGoogleSignInOptions)
                .build(); // 구글 계정 인증을 하기 위해 mGoogleApiClient 인스턴스 변수를 추가하고 초기화

        mAuth = FirebaseAuth.getInstance(); // 인스턴스 변수로 선언하여 Firebase 인증을 사용할 수 있게 초기화
        if(mAuth.getCurrentUser() != null){ // 자동로그인
            // 액티비티 띄우기
            startActivity(new Intent(ChatLoginActivity.this, MainActivity.class)); // 로그인 성공 시, 메인으로
            finish(); // 로그인 인증은 꺼줌
            return;
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = mFirebaseDatabase.getReference("users");

        mGoogleSignInbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient); // 지원하는 intent 창
                startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });

        mEmailLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((mEmail_edittext.getText().toString().replace(" ", "").equals("")) ||
                        (mPassword_edittext.getText().toString().replace(" ", "").equals(""))) {
                    Snackbar.make(mProgressBar, "이메일과 비밀번호를 입력해주세요.", Snackbar.LENGTH_SHORT).show();
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    createAndLoginEmail();
                }
            }
        });
    }

    private void createAndLoginEmail(){
        mAuth.createUserWithEmailAndPassword(mEmail_edittext.getText().toString(), mPassword_edittext.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            final User user = new User(); // 데이터 변경 방지 final

                            //-- data set
                            user.setEmail(firebaseUser.getEmail());
                            if (firebaseUser.getDisplayName() != null)
                                user.setName(firebaseUser.getDisplayName());
                            else
                                user.setName("홍길동");
                            user.setUid(firebaseUser.getUid());

                            if (firebaseUser.getPhotoUrl() != null)
                                user.setProfileUrl(firebaseUser.getPhotoUrl().toString());
                            else
                                user.setProfileUrl("https://i.imgur.com/jCxAEpA.jpg");

                            mUserRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // 데이터가 존재하지 않을 때만 데이터를 새로 생성
                                    if(!dataSnapshot.exists()){
                                        mUserRef.child(user.getUid()).setValue(user, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if (databaseError == null) {
                                                    startActivity(new Intent(ChatLoginActivity.this, MainActivity.class)); // 로그인 성공 시, 메인으로
                                                    finish(); // 로그인 인증은 꺼줌
                                                }
                                            }
                                        });
                                    }else{
                                        startActivity(new Intent(ChatLoginActivity.this,MainActivity.class));
                                        finish();
                                    }

                                    Bundle eventBundle = new Bundle();
                                    eventBundle.putString("email", user.getEmail());
                                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, eventBundle);
                                    Snackbar.make(mProgressBar, "로그인에 성공하였습니다.", Snackbar.LENGTH_SHORT).show();

                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            signinEmail();
                        }
                    }
                });

    }


    private void signinEmail(){
        mAuth.signInWithEmailAndPassword(mEmail_edittext.getText().toString(), mPassword_edittext.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            final User user = new User(); // 데이터 변경 방지 final

                            //-- data set
                            user.setEmail(firebaseUser.getEmail());
                            if (firebaseUser.getDisplayName() != null)
                                user.setName(firebaseUser.getDisplayName());
                            else
                                user.setName("홍길동");
                            user.setUid(firebaseUser.getUid());

                            if (firebaseUser.getPhotoUrl() != null)
                                user.setProfileUrl(firebaseUser.getPhotoUrl().toString());
                            else
                                user.setProfileUrl("https://i.imgur.com/jCxAEpA.jpg");

                            mUserRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // 데이터가 존재하지 않을 때만 데이터를 새로 생성
                                    if(!dataSnapshot.exists()){
                                        mUserRef.child(user.getUid()).setValue(user, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if (databaseError == null) {
                                                    startActivity(new Intent(ChatLoginActivity.this, MainActivity.class)); // 로그인 성공 시, 메인으로
                                                    finish(); // 로그인 인증은 꺼줌
                                                }
                                            }
                                        });
                                    }else{
                                        startActivity(new Intent(ChatLoginActivity.this,MainActivity.class));
                                        finish();
                                    }

                                    Bundle eventBundle = new Bundle();
                                    eventBundle.putString("email", user.getEmail());
                                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, eventBundle);
                                    Snackbar.make(mProgressBar, "로그인에 성공하였습니다.", Snackbar.LENGTH_SHORT).show();

                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        else {
                            Snackbar.make(mProgressBar, "로그인에 실패sign.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                //구글 로그인 성공해서 firebase 인증
                GoogleSignInAccount account = result.getSignInAccount(); // 정보 저장
                firebaseAuthWithGoogle(account); // 수신한 정보를 어플에 맞게 저장
            }
            else{

                //구글 로그인 실패
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null); // 자격 증명
        mAuth.signInWithCredential(credential) // 자격 증명을 이용하여 로그인
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isComplete()) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = task.getResult().getUser();
                                final User user = new User(); // 데이터 변경 방지 final

                                //-- data set
                                user.setEmail(firebaseUser.getEmail());
                                user.setName(firebaseUser.getDisplayName());
                                user.setUid(firebaseUser.getUid());

                                if (firebaseUser.getPhotoUrl() != null)
                                    user.setProfileUrl(firebaseUser.getPhotoUrl().toString());

                                mUserRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // 데이터가 존재하지 않을 때만 데이터를 새로 생성
                                        if(!dataSnapshot.exists()){
                                            mUserRef.child(user.getUid()).setValue(user, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    if (databaseError == null) {
                                                        startActivity(new Intent(ChatLoginActivity.this, MainActivity.class)); // 로그인 성공 시, 메인으로
                                                        finish(); // 로그인 인증은 꺼줌
                                                    }
                                                }
                                            });
                                        }else{
                                            startActivity(new Intent(ChatLoginActivity.this,MainActivity.class));
                                            finish();
                                        }

                                        Bundle eventBundle = new Bundle();
                                        eventBundle.putString("email", user.getEmail());
                                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, eventBundle);
                                        Snackbar.make(mProgressBar, "로그인에 성공하였습니다.", Snackbar.LENGTH_SHORT).show();

                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                Snackbar.make(mProgressBar, "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    @Override // 메소드는 서비스가 연결 실패했을 때 호출
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

