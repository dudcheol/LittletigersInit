package com.example.parkyoungcheol.littletigersinit.Navigation.AR;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.parkyoungcheol.littletigersinit.Model.DataSource;
import com.example.parkyoungcheol.littletigersinit.R;
import com.google.gson.JsonArray;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AR_navigationActivity extends AppCompatActivity {

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
    // lon 경도 lat 위도
    private double start_lon, start_lat, end_lon, end_lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_navigation);
        ButterKnife.bind(this);

        sourcePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AR_navigationActivity.this, Nav_searchActivity.class);
                startActivity(intent);
            }
        });



        try {
            // 현재 위치 받아옴
            //NGeoPoint point = nMapLocationManager.getMyLocation();
            // 현재 위치와 도착지 좌표를 url에 넣음
            //String url = DataSource.createNaverMapRequestURL(126.733638, 37.340267,126.742849, 37.351813);
            start_lon=126.733638;
            start_lat=37.340267;
            end_lon=126.742849;
            end_lat=37.351813;
            String url = DataSource.createTMapRequestURL(start_lon, start_lat, end_lon, end_lat);
            Log.i("티맵 url 주소 생성", url);

            // TmapHttpHandler의 doInBackground에 파라미터로 url 전달(params)
            String result = new TmapHttpHandler().execute(url).get();
            Log.i("TAG",result);

            // Todo : Json 받아온 것 어떻게 처리할것인지
            Double coordinatesLon; //경도 126~
            Double coordinatesLat; //위도 37~
            JSONObject responseJSON = new JSONObject(result);
            JSONArray featuresAry =  new JSONArray(responseJSON.getString("features"));
            for(int i = 0 ; i < featuresAry.length();i++){
                JSONObject featuresObj = new JSONObject(featuresAry.get(i).toString());
                JSONObject geometryObj = new JSONObject(featuresObj.getString("geometry"));
                if(geometryObj.getString("type").equals("Point")){
                    // geometry의 type이 Point인 경우
                    JSONArray coordinatesAry = new JSONArray(geometryObj.getString("coordinates"));
                    for(int j=0;j<coordinatesAry.length();j++){
                        if(j%2==0){
                            coordinatesLon=(Double)coordinatesAry.get(j);
                            Log.i("경도", coordinatesLon.toString());
                        }else {
                            coordinatesLat=(Double)coordinatesAry.get(j);
                            Log.i("위도", coordinatesLat.toString());
                        }
                    }
                }else{
                    // geometry의 type이 LineString인 경우
                }
            }
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

    private String parsingTmapJson(String naviStirng) throws JSONException {
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