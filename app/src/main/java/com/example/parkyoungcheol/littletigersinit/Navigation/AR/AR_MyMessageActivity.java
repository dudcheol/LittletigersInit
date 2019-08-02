package com.example.parkyoungcheol.littletigersinit.Navigation.AR;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkyoungcheol.littletigersinit.Chat.RecyclerViewItemClickListener;
import com.example.parkyoungcheol.littletigersinit.Model.ArmsgData;
import com.example.parkyoungcheol.littletigersinit.Model.GeoPoint;
import com.example.parkyoungcheol.littletigersinit.Model.MyArmsgData;
import com.example.parkyoungcheol.littletigersinit.R;
import com.example.parkyoungcheol.littletigersinit.util.ArmsgListAdapter;
import com.example.parkyoungcheol.littletigersinit.util.MyArmsgListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.xw.repo.BubbleSeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AR_MyMessageActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private int permissionLocation;
    private FusedLocationSource locationSource;
    private FirebaseDatabase mFirebaseDb;
    private DatabaseReference mARMessageRef;

    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;

    private static List<MyArmsgData> mBoardList;
    private RecyclerView m_oListView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressDialog pDialog;
    private AlertDialog.Builder alertDialogBuilder;

    Double sLat, sLng;
    public TextView txtView;
    String msg;//현재위치 지오코딩결과


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar__my_message);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        txtView = (TextView) findViewById(R.id.result);

        // 위치권한 받아오기
        checkLocationPermission();

        // 위치권한이 안받아져있을 경우
        if(permissionLocation == PackageManager.PERMISSION_DENIED){
        }else{
            findMyLocation();
            if(geoCodingCoordiToAddress(sLng,sLat).length()>=15){
                msg = geoCodingCoordiToAddress(sLng,sLat).substring(0,15)+"...";
            }else{
                msg = geoCodingCoordiToAddress(sLng,sLat);
            }
        }

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

        mBoardList = new ArrayList<>();

        // 권한체크
        if(permissionLocation == PackageManager.PERMISSION_DENIED){

        }else {
            mARMessageRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mBoardList.clear();
                    findMyLocation();
                    int i = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MyArmsgData bbs = snapshot.getValue(MyArmsgData.class); // 컨버팅되서 Bbs로........
                        //TODO 여기다가 uid 비교 후 같은 접속한 uid와 같은거만 리스트에 넣어주며 끝
                        String special = bbs.getUID();
                        String special2 = mFirebaseUser.getUid();
                        if(special != null && special.contains("0aqhKGhuyxeSVRomlALyxVnyYRx2")) {
                            mBoardList.add(bbs);
                            mBoardList.get(i).setAddress(geoCodingCoordiToAddress(mBoardList.get(i).getLongitude(), mBoardList.get(i).getLatitude()));
                            mBoardList.get(i).setDistance(calcDistance2(sLat, sLng, bbs.getLatitude(), bbs.getLongitude()) / 1000);
                            i++;
                        }
                        else{

                        }

                    }

                    //Collections.reverse(mBoardList);
                    Collections.sort(mBoardList, new Comparator<MyArmsgData>() {
                        @Override
                        public int compare(MyArmsgData o1, MyArmsgData o2) {
                            return o1.getDistance().compareTo(o2.getDistance());
                        }
                    });
                    m_oListView = (RecyclerView) findViewById(R.id.listView);
                    mAdapter = new MyArmsgListAdapter(AR_MyMessageActivity.this, mBoardList);
                    mAdapter.notifyDataSetChanged();
                    mLayoutManager = new LinearLayoutManager(AR_MyMessageActivity.this);
                    m_oListView.setLayoutManager(mLayoutManager);
                    m_oListView.setAdapter(mAdapter);
                    m_oListView.addOnItemTouchListener(new RecyclerViewItemClickListener(AR_MyMessageActivity.this, new RecyclerViewItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            MyArmsgData armsgData = mBoardList.get(position);

                            alertDialogBuilder = new AlertDialog.Builder(AR_MyMessageActivity.this);

                            alertDialogBuilder.setTitle("AR 메시지 내용");
                            alertDialogBuilder
                                    .setMessage(armsgData.getLabel())
                                    .setCancelable(true)
                                    .setPositiveButton("취소",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            })
                                    .setNegativeButton("삭제하기",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //TODO 이부분을 DB삭제 기능을 추가한다.
                                                }
                                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private String geoCodingCoordiToAddress(double lon, double lat){
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
            return "주소변환실패(위도:"+lat+"/경도:"+lon+")";
        }

        if (list != null) {
            if (list.size() == 0) {
                return "주소변환실패(위도:"+lat+"/경도:"+lon+")";
            } else {
                String address = list.get(0).getAddressLine(0);
                result = address.substring(4);
            }
        }

        return result;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        InfoWindow infoWindow = new InfoWindow();
        //정보창 안띄어짐
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(AR_MyMessageActivity.this) {
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

        ArrayList<MyArmsgData> oData = new ArrayList<MyArmsgData>();
        MyArmsgData oItem = new MyArmsgData();
        mARMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String msg;

                oData.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Marker marker = new Marker();
                    MyArmsgData abc = snapshot.getValue(MyArmsgData.class); // 컨버팅되서 Bbs로........
                    marker.setPosition(new LatLng(abc.getLatitude(), abc.getLongitude()));
                    if(abc.getLabel().length()>=10){
                        msg = abc.getLabel().substring(0,10)+"...";
                    }else{
                        msg = abc.getLabel();
                    }
                    marker.setCaptionText(msg);
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

    private GeoPoint findMyLocation(){
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GeoPoint myGeo = null;
        double longitude=0,latitude=0;
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( AR_MyMessageActivity.this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }
        else{
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location == null){

                pDialog = new ProgressDialog(AR_MyMessageActivity.this);
                pDialog.setMessage("위치정보를 받아오고 있습니다...");
                pDialog.show();
                while (true){
                    location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location==null){
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                1000,
                                1,
                                gpsLocationListener);
                        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                1000,
                                1,
                                gpsLocationListener);
                        continue;
                    }else{
                        break;
                    }
                }
                //로딩메시지제거
                if (pDialog != null) {
                    pDialog.dismiss();
                    pDialog = null;
                }

            } else {

                //String provider = location.getProvider();
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                //double altitude = location.getAltitude();
                sLat = latitude;
                sLng = longitude;
                myGeo = new GeoPoint(longitude, latitude);
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

        return myGeo;
    }

    // 현재위치가 바뀔때마다 좌표를 바꾸는 리스너
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            //double altitude = location.getAltitude();

            sLat = latitude;
            sLng = longitude;

            if(geoCodingCoordiToAddress(sLng,sLat).length()>=15){
                msg = geoCodingCoordiToAddress(sLng,sLat).substring(0,15)+"...";
            }else{
                msg = geoCodingCoordiToAddress(sLng,sLat);
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    public void checkLocationPermission(){
        permissionLocation = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionLocation == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(AR_MyMessageActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
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
                        checkLocationPermission();
                        recreate();
                    } else {
                        Toast.makeText(this, "ar기능을 이용하려면 위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    }
                }
            }
        }
    }

    public static double calcDistance(double lat1, double lon1, double lat2, double lon2){
        double EARTH_R, Rad, radLat1, radLat2, radDist;
        double distance, ret;

        EARTH_R = 6371000.0;
        Rad = Math.PI/180;
        radLat1 = Rad * lat1;
        radLat2 = Rad * lat2;
        radDist = Rad * (lon1 - lon2);

        distance = Math.sin(radLat1) * Math.sin(radLat2);
        distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist);
        ret = EARTH_R * Math.acos(distance);

        double rslt = Math.round(Math.round(ret) / 1000);
        //String result = rslt + " km";
        //if(rslt == 0) result = Math.round(ret) +" m";

        return rslt;
    }
    public static double calcDistance2(double lat1, double lon1, double lat2, double lon2){
        double EARTH_R, Rad, radLat1, radLat2, radDist;
        double distance, ret;

        EARTH_R = 6371000.0;
        Rad = Math.PI/180;
        radLat1 = Rad * lat1;
        radLat2 = Rad * lat2;
        radDist = Rad * (lon1 - lon2);

        distance = Math.sin(radLat1) * Math.sin(radLat2);
        distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist);
        ret = EARTH_R * Math.acos(distance);

        double rslt = Math.round(Math.round(ret) / 1);
        //String result = rslt + " km";
        //if(rslt == 0) result = Math.round(ret) +" m";

        return rslt;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences pref = getSharedPreferences("Distance", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.non_anim,R.anim.push_down_out);
    }
}
