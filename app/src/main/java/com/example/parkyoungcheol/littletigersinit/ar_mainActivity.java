package com.example.parkyoungcheol.littletigersinit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.parkyoungcheol.littletigersinit.Navigation.AR.AR_navigationActivity;
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


import butterknife.BindView;
import butterknife.ButterKnife;


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
    @BindView(R.id.about_btn)
    com.github.clans.fab.FloatingActionButton about_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_main);
        ButterKnife.bind(this);

        /*// 카메라 권한 받기
        int permissionCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        if(permissionCamera == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ar_mainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {

        }*/

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


        about_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ar_nav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ar_mainActivity.this, AR_navigationActivity.class);
                startActivity(intent);
            }
        });

        /*poi_browser_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*Intent intent = new Intent(ar_mainActivity.this, MixView.class);
                startActivity(intent);*//*
                Intent intent = new Intent(ar_mainActivity.this, com.dragon4.owo.ar_trace.ARCore.MixView.class);
                startActivity(intent);
            }
        });*/


    }


    // 권한설정
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 위치 추적 권한
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }

        // 카메라 권한
        if(requestCode==REQUEST_CAMERA){
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (permission.equals(Manifest.permission.CAMERA)) {
                    if(grantResult == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        Toast.makeText(this, "ar기능을 이용하려면 카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
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

}