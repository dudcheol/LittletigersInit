package com.example.parkyoungcheol.littletigersinit.Navigation.AR;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.parkyoungcheol.littletigersinit.Model.DataSource;
import com.example.parkyoungcheol.littletigersinit.R;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private NMapLocationManager nMapLocationManager;
    private NMapView nMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_navigation);
        ButterKnife.bind(this);

        sourcePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ar_navigationActivity.this, nav_searchActivity.class);
                startActivity(intent);
            }
        });



        try {
            // 현재 위치 받아옴
            //NGeoPoint point = nMapLocationManager.getMyLocation();
            // 현재 위치와 도착지 좌표를 url에 넣음
            String url = DataSource.createNaverMapRequestURL(126.733638, 37.340267,126.742849, 37.351813);
            Log.i("Navi URL",url);
            String result = new NaverHttpHandler().execute(url).get();

            Log.i("TAG",result);

            String nav = parsingNaverNaviJson(result);
            Log.i("nav",nav);


            //updateNaviStatus(result); // 네비게이션 가이드.

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*private void updateNaviStatus(String result) throws JSONException {

        //List<ARMarker> naviList = null;
        //final DataConvertor dataConvertor = new DataConvertor();
        //naviList =  dataConvertor.load(result, DataSource.DATASOURCE.NAVI, DataSource.DATAFORMAT.NAVI);

        final Intent naviBroadReceiver = new Intent();
        naviBroadReceiver.setAction("NAVI");

        String guide = parsingNaverNaviJson(result); // result에 있는 값은 NaverHttpHandler.에 url넣은거

        if(guide.equals("END")) {
            loopThread.interrupt();
        }

        naviBroadReceiver.putExtra("GUIDE", guide);
        mixContext.sendBroadcast(naviBroadReceiver);

    }*/

    private String parsingNaverNaviJson(String naviStirng) throws JSONException {
        String guide;
        JSONObject jObject = new JSONObject(naviStirng);
        int distance = jObject.getJSONObject("result").getJSONObject("summary").getInt("totalDistance");

        JSONArray jArray = jObject.getJSONObject("result").getJSONArray("route").getJSONObject(0).getJSONArray("point");
        JSONObject firstRoute = jArray.getJSONObject(1);

        if(distance < 40 || firstRoute == null) { //  거리가 얼마 안남았을때
            guide = "END";
        }

        else {
            guide = firstRoute.getJSONObject("guide").getString("name");
        }
        return guide;
    }
}
