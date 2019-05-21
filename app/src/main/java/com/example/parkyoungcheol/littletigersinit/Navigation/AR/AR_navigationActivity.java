package com.example.parkyoungcheol.littletigersinit.Navigation.AR;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.parkyoungcheol.littletigersinit.MainActivity;
import com.example.parkyoungcheol.littletigersinit.Model.DataSource;
import com.example.parkyoungcheol.littletigersinit.Model.GeoPoint;
import com.example.parkyoungcheol.littletigersinit.R;
import com.google.gson.JsonArray;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    // lon 경도 lat 위도 _ 카텍좌표계임
    private double start_lon, start_lat, end_lon, end_lat;

    // Nav_searchActivity에서 받은 시작지, 도착지 경도X,위도Y
    private String start_lon_X, start_lat_Y, dest_lon_X, dest_lat_Y;
    private String startTitle, destTitle;
    private String coordiStyle;
    private GeoPoint resultGeoPoint = new GeoPoint();
    private ProgressDialog pDialog;


    public String start = "현재위치 확인 실패";


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1001:
                    coordiStyle = data.getStringExtra("coordiStyle");
                    if(coordiStyle.equals("KATECH")){
                        pDialog = new ProgressDialog(AR_navigationActivity.this);
                        // Showing progress dialog before making http request
                        pDialog.setMessage("Loading...");
                        pDialog.show();
                        transKTMtoWGS(data.getStringExtra("getX"),data.getStringExtra("getY"));

                        // 핸들러가 있는 이유?
                        // transKTMtoWGS에서 인터넷을 사용하기때문에 값을 반환받아 적용하는데 시간이 걸림
                        final Handler handler = new Handler(){
                            @Override
                            public void handleMessage(Message msg) {
                                hidePDialog();
                                start_lon_X=resultGeoPoint.getX_s();
                                start_lat_Y=resultGeoPoint.getY_s();
                                startTitle = data.getStringExtra("getTitle");
                                sourceResultText.setText(startTitle);
                                Log.i("카텍변환확인",start_lon_X+start_lat_Y);
                            }
                        };
                        handler.sendEmptyMessageDelayed(0,500);
                        hidePDialog();
                        start_lon_X=resultGeoPoint.getX_s();
                        start_lat_Y=resultGeoPoint.getY_s();
                        startTitle = data.getStringExtra("getTitle");
                        sourceResultText.setText(startTitle);
                        Log.i("카텍변환확인",start_lon_X+start_lat_Y);
                    }else{
                        start_lon_X = data.getStringExtra("getX");
                        start_lat_Y = data.getStringExtra("getY");
                        startTitle = data.getStringExtra("getTitle");
                        sourceResultText.setText(startTitle);
                        Log.i("확인 wgs",start_lon_X+start_lat_Y);
                    }
                    break;
                case 2002:
                    coordiStyle = data.getStringExtra("coordiStyle");
                    if(coordiStyle.equals("KATECH")){
                        pDialog = new ProgressDialog(AR_navigationActivity.this);
                        // Showing progress dialog before making http request
                        pDialog.setMessage("Loading...");
                        pDialog.show();
                        transKTMtoWGS(data.getStringExtra("getX"),data.getStringExtra("getY"));

                        // 핸들러가 있는 이유?
                        // transKTMtoWGS에서 인터넷을 사용하기때문에 값을 반환받아 적용하는데 시간이 걸림
                        final Handler handler = new Handler(){
                            @Override
                            public void handleMessage(Message msg) {
                                hidePDialog();
                                dest_lon_X=resultGeoPoint.getX_s();
                                dest_lat_Y=resultGeoPoint.getY_s();
                                destTitle = data.getStringExtra("getTitle");
                                destResultText.setText(destTitle);
                                Log.i("확인 카텍바꼇나",dest_lon_X+dest_lat_Y);
                            }
                        };
                        handler.sendEmptyMessageDelayed(0,500);
                    }else{
                        dest_lon_X = data.getStringExtra("getX");
                        dest_lat_Y = data.getStringExtra("getY");
                        destTitle = data.getStringExtra("getTitle");
                        destResultText.setText(destTitle);
                        Log.i("확인 wgs",dest_lon_X+dest_lat_Y);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_navigation);
        ButterKnife.bind(this);

        // 채팅에서 받아오는 경,위도 값
        Intent intent2  = getIntent();
        String dest_lon_X_from_chat = intent2.getStringExtra("dest_lon_X_from_chat");
        String dest_lat_Y_from_chat = intent2.getStringExtra("dest_lat_Y_from_chat");
        if(dest_lon_X_from_chat != null && dest_lat_Y_from_chat != null){
            SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
            SharedPreferences.Editor editor = sf.edit();
            editor.putString("destLonX",dest_lon_X_from_chat);
            editor.putString("destLatY",dest_lat_Y_from_chat);
            editor.putString("destResult","채팅 상대방이 보낸 위치");
            editor.commit();
        }

        SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
        String sourceResult = sf.getString("sourceResult", null);
        startTitle = sourceResult;
        String destResult = sf.getString("destResult", null);
        destTitle = destResult;
        start_lon_X = sf.getString("startLonX", null);
        start_lat_Y = sf.getString("startLatY", null);
        dest_lon_X = sf.getString("destLonX", null);
        dest_lat_Y = sf.getString("destLatY", null);
        coordiStyle = sf.getString("coordiStyle",null);

        /*final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // 전에 설정해두었던 시작지,도착지 기억
                SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
                String sourceResult = sf.getString("sourceResult", null);
                startTitle = sourceResult;
                String destResult = sf.getString("destResult", null);
                destTitle = destResult;
                start_lon_X = sf.getString("startLonX", null);
                start_lat_Y = sf.getString("startLatY", null);
                dest_lon_X = sf.getString("destLonX", null);
                dest_lat_Y = sf.getString("destLatY", null);
                coordiStyle = sf.getString("coordiStyle",null);
            }
        };
        handler.sendEmptyMessageDelayed(0,500);*/

        // 출발지 선택 버튼
        // requestCode 1001이면 출발지, 2002이면 도착지
        sourcePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AR_navigationActivity.this, Nav_searchActivity.class);
                startActivityForResult(intent, 1001);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // 도착지 선택 버튼
        destPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AR_navigationActivity.this, Nav_searchActivity.class);
                startActivityForResult(intent, 2002);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // AR 길안내 버튼
        navStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start_lon_X == null || start_lat_Y == null || dest_lon_X == null || dest_lat_Y == null) {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.nav_coord_layout),
                            "출발지와 목적지를 모두 선택해주세요.", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                } else {
                    pDialog = new ProgressDialog(AR_navigationActivity.this);
                    // Showing progress dialog before making http request
                    pDialog.setMessage("Loading...");
                    pDialog.show();
                    String result = TmapNaviJsonReceiver(start_lon_X, start_lat_Y, dest_lon_X, dest_lat_Y);
                    final Handler handler = new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            hidePDialog();
                            if(result.contains("Error")){
                                AlertDialog.Builder add = new AlertDialog.Builder(AR_navigationActivity.this);
                                AlertDialog alert = add.create();
                                alert.setTitle("<!>");
                                alert.setMessage("길안내 서비스가 지원되지 않는 구간이 설정되어있습니다.\n출발지나 목적지를 변경한 후 다시 시도해주세요.");
                                alert.show();
                            }else {
                                //Toast.makeText(AR_navigationActivity.this, result, Toast.LENGTH_SHORT).show();
                                Log.i("TmapNaviJsonReceiver", result);
                                Intent intent = new Intent(AR_navigationActivity.this,UnityPlayerActivity.class);
                                intent.putExtra("TmapJSON",result);
                                intent.putExtra("SELECT",1);
                                startActivity(intent);
                                overridePendingTransition(R.anim.non_anim, R.anim.push_down_out);
                            }
                        }
                    };
                    handler.sendEmptyMessageDelayed(0,1000);
                }
            }
        });

        mapNavStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double start_lon=Double.parseDouble(start_lon_X);
                double start_lat=Double.parseDouble(start_lat_Y);
                double end_lon=Double.parseDouble(dest_lon_X);
                double end_lat=Double.parseDouble(dest_lat_Y);
                String encTxt2 = "";
                String strTxt2 = "";
                try {
                    String encTxt = URLEncoder.encode(destTitle, "UTF-8");
                    String strTxt = URLEncoder.encode(startTitle, "UTF-8");
                    encTxt2 = encTxt;
                    strTxt2 = strTxt;
                } catch (UnsupportedEncodingException ec) {

                }
                String url = "nmap://route/walk?slat="+start_lat+"&slng="+start_lon+"&sname="+strTxt2+"&dlat="+end_lat+"&dlng="+end_lon+"&dname="+encTxt2+"&appname={Littletigers}";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);

                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list == null || list.isEmpty()) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nhn.android.nmap")));
                } else {
                    startActivity(intent);
                }
            }
        });



    }


    // startActivityForResult() 메서드가 먼저 실행되고 onResume() 메서드가 나중 실행된다!
    @Override
    protected void onResume() {
        super.onResume();

        // 핸들러가 있는 이유?
        // startActivityForResult에서 인터넷을 통해서 값을 받아오는 것을 진행한 바람에
        // 전역변수의 값이 나중에 바뀜
        // 따라서 적어도 startActivityForResult보다는 늦게 실행되어야함
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (startTitle == null) {
                    sourceResultText.setText("출발지를 선택해주세요.");
                } else {
                    if (startTitle.contains("현재위치")) {
                        findMyCurrentLocation(1);
                    } else {
                        sourceResultText.setText(startTitle);
                    }
                }

                if (destTitle == null) {
                    destResultText.setText("목적지를 선택해주세요.");
                } else {
                    if (destTitle.contains("현재위치")) {
                        findMyCurrentLocation(2);
                    } else {
                        destResultText.setText(destTitle);
                    }
                }
            }
        };
        handler.sendEmptyMessageDelayed(0,600);
    }

    // 현재 위치를 정확하게 받아오자
    public void findMyCurrentLocation(int flag){
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        double longitude=0,latitude=0;
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( AR_navigationActivity.this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }
        else{
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //String provider = location.getProvider();
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            //double altitude = location.getAltitude();

            // 1이면 출발지 텍스트 변경
            if (flag == 1) {
                sourceResultText.setText("현재위치:"+geoCodingCoordiToAddress(longitude, latitude));
                start_lon_X = String.valueOf(longitude);
                start_lat_Y = String.valueOf(latitude);
                //Toast.makeText(this, geoCodingCoordiToAddress(longitude, latitude), Toast.LENGTH_SHORT).show();
                //Log.i("지오코딩",geoCodingCoordiToAddress(longitude, latitude));
            }
            // 2이면 목적지 텍스트 번경
            if (flag == 2) {
                destResultText.setText("현재위치:"+geoCodingCoordiToAddress(longitude, latitude));
                dest_lon_X = String.valueOf(longitude);
                dest_lat_Y = String.valueOf(latitude);
                //Toast.makeText(this, geoCodingCoordiToAddress(longitude, latitude), Toast.LENGTH_SHORT).show();
            }

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000,
                    1,
                    gpsLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    3000,
                    1,
                    gpsLocationListener);
        }
    }

    // 현재위치가 바뀔때마다 좌표를 바꾸는 리스너
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            //double altitude = location.getAltitude();

            if(startTitle!=null&&startTitle.contains("현재위치")){
                sourceResultText.setText("현재위치:"+geoCodingCoordiToAddress(longitude, latitude));
                start_lon_X = String.valueOf(longitude);
                start_lat_Y = String.valueOf(latitude);
                //Toast.makeText(AR_navigationActivity.this, geoCodingCoordiToAddress(longitude, latitude), Toast.LENGTH_SHORT).show();
            }
            if(destTitle!=null&&destTitle.contains("현재위치")){
                destResultText.setText("현재위치:"+geoCodingCoordiToAddress(longitude, latitude));
                dest_lon_X = String.valueOf(longitude);
                dest_lat_Y = String.valueOf(latitude);
                //Toast.makeText(AR_navigationActivity.this, geoCodingCoordiToAddress(longitude, latitude), Toast.LENGTH_SHORT).show();
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    //구글 지오코딩. 좌표 -> 주소
    public String geoCodingCoordiToAddress(double lon, double lat){
        final Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        String result=null;

        try {
            list = geocoder.getFromLocation(
                    lat, // 위도
                    lon, // 경도
                    1); // 얻어올 값의 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
            return "주소로 표시할 수 없음";
        }

        if (list != null) {
            if (list.size() == 0) {
                return "주소로 표시할 수 없음";
            } else {
                String address = list.get(0).getAddressLine(0);
                result = address.substring(4);
            }
        }

        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.non_anim,R.anim.push_down_out);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);

        SharedPreferences.Editor editor  = sf.edit();
        editor.putString("sourceResult",startTitle);
        editor.putString("destResult",destTitle);
        editor.putString("startLonX",start_lon_X);
        editor.putString("startLatY",start_lat_Y);
        editor.putString("destLonX",dest_lon_X);
        editor.putString("destLatY",dest_lat_Y);

        editor.commit();
    }

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

    // Tmap에 출발지, 도착지 보내서 길안내 경로마다의 경위도를 받아서 파싱함
    // 파싱하여 유니티로 넘겨줌
    // 형태 : 경도,위도|경도,위도| ...
    private String TmapNaviJsonReceiver(String startLonX, String startLatY, String destLonX, String destLatY){
        StringBuffer sb=new StringBuffer();
        try {
            start_lon=Double.parseDouble(startLonX);
            start_lat=Double.parseDouble(startLatY);
            end_lon=Double.parseDouble(destLonX);
            end_lat=Double.parseDouble(destLatY);

            // Tmap에서 반환해줄때 내가 입력한 출발지는 제외해서 반환하므로 따로 추가해줌
            // sb.append(startLonX+","+startLatY+"|");

            String url = DataSource.createTMapRequestURL_WGS84GEO(start_lon, start_lat, end_lon, end_lat);
            //String url = DataSource.createTMapRequestURL_WGS84GEO(start_lon, start_lat, end_lon, end_lat);
            Log.i("티맵 url 주소 생성", url);

            // TmapHttpHandler의 doInBackground에 파라미터로 url 전달(params)
            String result = new TmapHttpHandler().execute(url).get();
            Log.i("TAG",result);

            if(result.equals("Error")){
                sb.append("Error");
            }else {
                Double coordinatesLon; //경도 126~
                Double coordinatesLat; //위도 37~
                JSONObject responseJSON = new JSONObject(result);
                JSONArray featuresAry = new JSONArray(responseJSON.getString("features"));
                for (int i = 0; i < featuresAry.length(); i++) {
                    JSONObject featuresObj = new JSONObject(featuresAry.get(i).toString());
                    JSONObject geometryObj = new JSONObject(featuresObj.getString("geometry"));
                    if (geometryObj.getString("type").equals("Point")) {
                        // geometry의 type이 Point인 경우
                        JSONArray coordinatesAry = new JSONArray(geometryObj.getString("coordinates"));

                        for (int j = 0; j < coordinatesAry.length(); j++) {
                            if (j % 2 == 0) {
                                coordinatesLon = (Double) coordinatesAry.get(j);
                                sb.append(coordinatesAry.get(j) + ",");
                                Log.i("경도", coordinatesLon.toString());
                            } else {
                                coordinatesLat = (Double) coordinatesAry.get(j);
                                sb.append(coordinatesAry.get(j) + "|");
                                Log.i("위도", coordinatesLat.toString());
                            }
                        }
                    } else {
                        // geometry의 type이 LineString인 경우
                    }
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("티맵api로 받은 길안내 결과",String.valueOf(sb));
        return String.valueOf(sb);
    }

    public void transKTMtoWGS(String lonX, String latY) {
        final RequestQueue queue1 = Volley.newRequestQueue(this);
        // 키워드받아서 검색, 검색결과갯수 display, 검색결과양식 sort=random이면 유사한 결과 출력
        String kakaoURL = "https://dapi.kakao.com/v2/local/geo/transcoord.json?x="+lonX+"&y="+latY+"&input_coord=KTM&output_coord=WGS84";
        JsonObjectRequest localRequest = new JsonObjectRequest(Request.Method.GET, kakaoURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("리스폰스", response.toString());
                        JSONArray documents = null;

                        try {
                            documents = response.getJSONArray("documents");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            for (int i = 0; i < documents.length(); i++) {
                                JSONObject obj = documents.getJSONObject(i);
                                String lonX=obj.getString("x");
                                String latY=obj.getString("y");
                                resultGeoPoint.setX_s(lonX);
                                resultGeoPoint.setY_s(latY);
                                Log.i("테스트3", obj.getString("x") + "," + obj.getString("y"));
                                Log.i("테스트4", resultGeoPoint.getX_s() + "," + resultGeoPoint.getY_s());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR_RESPONSE =>", error.toString());
                        Toast.makeText(AR_navigationActivity.this, "통신에러", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap();
                params.put("Authorization", "KakaoAK fcf32b28928096d6cf44be171f2a09b5");
                //params.put("X-Naver-Client-Secret", clientSecret);
                Log.d("getHedaer =>", "" + params);
                return params;
            }
        };
        queue1.add(localRequest);
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}