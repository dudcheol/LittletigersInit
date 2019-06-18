package com.example.parkyoungcheol.littletigersinit.Navigation.AR;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.parkyoungcheol.littletigersinit.Model.ArmsgData;
import com.example.parkyoungcheol.littletigersinit.Model.GeoPoint;
import com.example.parkyoungcheol.littletigersinit.R;
import com.example.parkyoungcheol.littletigersinit.ar_mainActivity;
import com.example.parkyoungcheol.littletigersinit.util.ArmsgListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ARmessageActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private FirebaseDatabase mFirebaseDb;
    private DatabaseReference mARMessageRef;

    private static List<ArmsgData> mBoardList;
    private RecyclerView m_oListView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private com.github.clans.fab.FloatingActionButton ar_message_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armessage);

        // 위치권한 받아오기
        int permissionLocation = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionLocation == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ARmessageActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }else{

        }

        ar_message_btn = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.ar_message_list_btn);

        ar_message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ARmessageActivity.this, UnityPlayerActivity.class);
                intent.putExtra("SELECT", 3);
                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in,R.anim.non_anim);
            }
        });


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

        mFirebaseDb = FirebaseDatabase.getInstance();
        mARMessageRef = mFirebaseDb.getReference("ARMessages");

        mBoardList =  new ArrayList<>();
        mARMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mBoardList.clear();

                int i=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ArmsgData bbs = snapshot.getValue(ArmsgData.class); // 컨버팅되서 Bbs로........
                    mBoardList.add(bbs);
                    mBoardList.get(i).setAddress(geoCodingCoordiToAddress(mBoardList.get(i).getLongitude(),mBoardList.get(i).getLatitude()));
                    i++;
                }

                Collections.reverse(mBoardList);
                m_oListView = (RecyclerView)findViewById(R.id.listView);
                mAdapter = new ArmsgListAdapter(ARmessageActivity.this, mBoardList);
                mAdapter.notifyDataSetChanged();
                mLayoutManager = new LinearLayoutManager(ARmessageActivity.this);
                m_oListView.setLayoutManager(mLayoutManager);
                m_oListView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

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
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        InfoWindow infoWindow = new InfoWindow();
        //정보창 안띄어짐
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(ARmessageActivity.this) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return "정보 창 내용";
            }
        });

        mFirebaseDb = FirebaseDatabase.getInstance();
        mARMessageRef = mFirebaseDb.getReference("ARMessages");


        //지하철 역 정보 까지 띄어주는 레이어그룹
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true);
        //건물 내부정보까지 보여지게하는 옵션
        naverMap.setIndoorEnabled(false);
        naverMap.setOnMapClickListener((point, coord) ->
                Toast.makeText(this, coord.latitude + ", " + coord.longitude, Toast.LENGTH_SHORT).show());

        ArrayList<ArmsgData> oData = new ArrayList<ArmsgData>();
        ArmsgData oItem = new ArmsgData();
        mARMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                oData.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Marker marker = new Marker();
                    ArmsgData abc = snapshot.getValue(ArmsgData.class); // 컨버팅되서 Bbs로........
                    marker.setPosition(new LatLng(abc.getLatitude(), abc.getLongitude()));
                    marker.setCaptionText(abc.getLabel());
                    marker.setWidth(80);
                    marker.setHeight(80);
                    marker.setIcon(OverlayImage.fromResource(R.drawable.ar_marker));
                    marker.setCaptionColor(Color.WHITE);
                    marker.setCaptionHaloColor(Color.BLACK);
                    marker.setCaptionTextSize(12);
                    marker.setMap(naverMap);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

        // 카메라 이동 .. 단, 위치 퍼미션이 허가되어있을 때만
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            GeoPoint mGeo = findMyLocation();
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(mGeo.getY(), mGeo.getX()));
            naverMap.moveCamera(cameraUpdate);
        }
    }

    // 현재 내 위치 반환
    private GeoPoint findMyLocation() {
        GeoPoint myGeo = null;
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 밑줄 권한때문에 그런거임
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( ARmessageActivity.this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }
        else {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String provider = location.getProvider();
            Double lon_X = location.getLongitude();
            Double lat_Y = location.getLatitude();

            myGeo = new GeoPoint(lon_X, lat_Y);
        }
        return myGeo;
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

                    } else {
                        Toast.makeText(this, "ar기능을 이용하려면 위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    }
                }
            }
        }
    }
}
