package com.example.parkyoungcheol.littletigersinit;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.parkyoungcheol.littletigersinit.Model.DataSet;
import com.example.parkyoungcheol.littletigersinit.Model.DataSource;
import com.example.parkyoungcheol.littletigersinit.Model.GeoPoint;
import com.example.parkyoungcheol.littletigersinit.Navigation.AR.AR_navigationActivity;
import com.example.parkyoungcheol.littletigersinit.Navigation.AR.Nav_searchActivity;
import com.example.parkyoungcheol.littletigersinit.Navigation.AR.UnityPlayerActivity;
import com.github.clans.fab.FloatingActionMenu;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.util.FusedLocationSource;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.val;


public class ar_mainActivity extends FragmentActivity implements OnMapReadyCallback {
    private MapView mapView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private Context context;
    private static final int REQUEST_CAMERA = 2000;

    //private Button menu,ar_nav,info,poi;
    @BindView(R.id.fab_menu_btn)
    FloatingActionMenu fab_menu;
    @BindView(R.id.ar_nav_btn)
    com.github.clans.fab.FloatingActionButton ar_nav_btn;
    @BindView(R.id.poi_browser_btn)
    com.github.clans.fab.FloatingActionButton poi_browser_btn;
    @BindView(R.id.decode_box)
    EditText decode_editText;
    @BindView(R.id.decode_btn)
    Button decode_button;
    @BindView(R.id.ar_message_btn)
    com.github.clans.fab.FloatingActionButton ar_message_btn;

    private GeoPoint myCurrentLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_main);
        ButterKnife.bind(this);

        // 카메라, 위치정보활용 권환 받기
        checkCameraAndLocationPermission();

        // 네이버 맵 띄우기
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("0yfv84wqze"));

        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        // ar 메시지 남기기로 이동 버튼
        ar_message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // ar 네비게이션으로 이동버튼
       ar_nav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ar_mainActivity.this, AR_navigationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in,R.anim.non_anim);
            }
        });

        // ar 주변검색으로 이동 버튼
        poi_browser_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*myCurrentLocation = findMyLocation();
                allPOIreciever(myCurrentLocation.getX(),myCurrentLocation.getY());*/
            }
        });


    }

    // 현재 내 위치 반환
    private GeoPoint findMyLocation() {
        GeoPoint myGeo = null;
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 밑줄 권한때문에 그런거임
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( ar_mainActivity.this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }
        else {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String provider = location.getProvider();
            double lon_X = location.getLongitude();
            double lat_Y = location.getLatitude();

            myGeo = new GeoPoint(lon_X, lat_Y);
        }
        return myGeo;
    }

    // 주변검색 API 결과값 파싱
    // NaverHttpHandler에 Naver에서 요구하는 API URL형식을 맞춰 보내고 전달받은 JSON 값을 파싱한다
    // 파싱해서 저장하는 형태 : 위치이름|경도|위도|
    public String allPOIreciever(double myCurrentLonX,double myCurrentLatY){
        DataSource.createRequestCategoryURL("CAFE",myCurrentLonX,myCurrentLatY,0,0);
        DataSource.createRequestCategoryURL("BUSSTOP",myCurrentLonX,myCurrentLatY,0,0);
        DataSource.createRequestCategoryURL("CONVENIENCE",myCurrentLonX,myCurrentLatY,0,0);
        DataSource.createRequestCategoryURL("RESTAURANT",myCurrentLonX,myCurrentLatY,0,0);
        DataSource.createRequestCategoryURL("BANK",myCurrentLonX,myCurrentLatY,0,0);
        DataSource.createRequestCategoryURL("ACCOMMODATION",myCurrentLonX,myCurrentLatY,0,0);
        DataSource.createRequestCategoryURL("HOSPITAL",myCurrentLonX,myCurrentLatY,0,0);

        return "";
    }


    // Todo : 20190515 여기까지했음..계속진행하삼
    /*public void 이름정해야함() {
        final RequestQueue queue = Volley.newRequestQueue(this);
        // 키워드받아서 검색, 검색결과갯수 display, 검색결과양식 sort=random이면 유사한 결과 출력
        String NAVERURL = "https://openapi.naver.com/v1/search/local.json?query=" + keyword + "&display=" + display + "&start=1&sort=" + sort;
        JsonObjectRequest localRequest = new JsonObjectRequest(Request.Method.GET, NAVERURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("Response =>", response.toString());

                        hidePDialog();

                        JSONArray items = null;

                        try {
                            items = response.getJSONArray("items");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // 리스트 초기화 후에 값 넣기
                        list.clear();
                        try {
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject obj = items.getJSONObject(i);
                                DataSet dataSet = new DataSet();
                                dataSet.setTitle(Html.fromHtml(obj.getString("title")).toString());
                                dataSet.setRoadAddress(obj.getString("roadAddress"));
                                dataSet.setMapx(obj.getInt("mapx"));
                                dataSet.setMapy(obj.getInt("mapy"));
                                list.add(dataSet);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //GeoPoint getGeoPoint = convertKATECtoWGS(list.get(position).getMapx(),list.get(position).getMapy());

                                //Toast.makeText(Nav_searchActivity.this, String.valueOf(getGeoPoint.getX())+", "+String.valueOf(getGeoPoint.getY()), Toast.LENGTH_SHORT).show();
                                //Toast.makeText(Nav_searchActivity.this, list.get(position).getMapx() + "," + list.get(position).getMapy(), Toast.LENGTH_SHORT).show();
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("getX", String.valueOf(list.get(position).getMapx()));
                                resultIntent.putExtra("getY", String.valueOf(list.get(position).getMapy()));
                                resultIntent.putExtra("getTitle", list.get(position).getTitle());
                                resultIntent.putExtra("coordiStyle","KATECH");
                                setResult(RESULT_OK, resultIntent);
                                finish();
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR_RESPONSE =>", error.toString());
                        hidePDialog();
                        AlertDialog.Builder add = new AlertDialog.Builder(Nav_searchActivity.this);
                        add.setMessage(error.getMessage()).setCancelable(true);
                        AlertDialog alert = add.create();
                        alert.setTitle("<!>");
                        alert.setMessage("검색 내용을 다시 입력해주세요.");
                        alert.show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap();
                params.put("X-Naver-Client-Id", clientId);
                params.put("X-Naver-Client-Secret", clientSecret);
                Log.d("getHedaer =>", "" + params);
                return params;
            }
        };
        queue.add(localRequest);
    }*/


    // 카메라 권한 받아오기
    public void checkCameraAndLocationPermission(){
        int permissionCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int permissionLocation = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionLocation == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ar_mainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }else if(permissionCamera == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ar_mainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }else{

        }
    }

    // 권한설정
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 위치 추적 권한
        if(requestCode==LOCATION_PERMISSION_REQUEST_CODE){
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if(grantResult == PackageManager.PERMISSION_GRANTED) {
                        checkCameraAndLocationPermission();
                    } else {
                        Toast.makeText(this, "ar기능을 이용하려면 위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    }
                }
            }
        }

        // 카메라 권한
        if(requestCode==REQUEST_CAMERA){
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (permission.equals(Manifest.permission.CAMERA)) {
                    if(grantResult == PackageManager.PERMISSION_GRANTED) {
                        checkCameraAndLocationPermission();
                    } else {
                        Toast.makeText(this, "ar기능을 이용하려면 카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    }
                }
            }
        }
    }

    // 사전설정
    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {
        // 사용자 현위치 버튼 활성화
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        // 사용자 위치 오버레이 활성화
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        // 위치 오버레이 반경
        locationOverlay.setCircleRadius(100);

        naverMap.setLocationSource(locationSource);

        naverMap.setLocationTrackingMode(LocationTrackingMode.Face);

        /*
        Projection projection = naverMap.getProjection();
        // 화면의 100,100 지점을 지도 좌표로 변환
        LatLng coord = projection.fromScreenLocation(new PointF(100, 100));
        // 지도의 x,y 지점을 화면 좌표로 변환
        PointF point = projection.toScreenLocation(new LatLng(37.5666102, 126.9783881));
        */

        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                // 카메라 이동
                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(location.getLatitude(), location.getLongitude()));
                naverMap.moveCamera(cameraUpdate);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences prefs = getSharedPreferences("sFile",0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
        Log.i("destroy","SharedPreferences 데이터 삭제");
    }
}