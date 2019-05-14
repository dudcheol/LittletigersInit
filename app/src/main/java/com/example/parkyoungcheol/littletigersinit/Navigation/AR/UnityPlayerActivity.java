package com.example.parkyoungcheol.littletigersinit.Navigation.AR;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;

import static com.unity3d.player.UnityPlayer.UnitySendMessage;

public class UnityPlayerActivity extends Activity
{
    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    private ProgressDialog pDialog;

    // Setup activity layout
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        mUnityPlayer = new UnityPlayer(this);
        setContentView(mUnityPlayer);
        mUnityPlayer.requestFocus();

        // 이전 액티비티에서 받아온 인텐트에서 putExtra해서 받아온 것을 따로 저장함
        Intent intent = getIntent();
        String TmapJSON = intent.getStringExtra("TmapJSON");

        Toast.makeText(this, TmapJSON, Toast.LENGTH_SHORT).show();

        /* 안드로이드에서 유니티로 넘어갈 때 로고가 뜨는 시간을 고려하여
        안드로이드에서 유니티로 값 넘기는 메소드 실행에 딜레이 시간을 줌 (1초) */
        //Todo : 단, 테스트한 기기가 갤S10이라 속도가 빨라서 1초만에 된 걸 수도 있어서 윤복이폰으로도 확인 필요함

        pDialog = new ProgressDialog(UnityPlayerActivity.this);
        // Showing progress dialog before making http request
        pDialog.setMessage("AR Loading...");
        pDialog.show();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // s:게임오브젝트이름, s1:함수이름, s2:전달할string
                // ARnavigation을 받으면 AR 네비게이션 신이 실행됨
                //UnitySendMessage("SceneType", "divScene", "ARnavigation");
                // 길안내 경로 경위도 유니티로 전송
                UnitySendMessage("reciveDataFromAndroid", "selectScene", "NAV");
                UnitySendMessage("reciveDataFromAndroid", "RecvLocation", TmapJSON);
                if (pDialog != null) {
                    pDialog.dismiss();
                    pDialog = null;
                }
            }
        };
        handler.sendEmptyMessageDelayed(0,2000);
    }

    @Override protected void onNewIntent(Intent intent)
    {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
    }

    // Quit Unity
    @Override protected void onDestroy ()
    {
        mUnityPlayer.destroy();
        super.onDestroy();
    }

    // Pause Unity
    @Override protected void onPause()
    {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override protected void onResume()
    {
        super.onResume();
        mUnityPlayer.resume();
    }

    @Override protected void onStart()
    {
        super.onStart();
        mUnityPlayer.start();
    }

    @Override protected void onStop()
    {
        super.onStop();
        mUnityPlayer.stop();
    }

    // Low Memory Unity
    @Override public void onLowMemory()
    {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL)
        {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
    /*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }
}