package com.example.parkyoungcheol.littletigersinit.Navigation.AR;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkyoungcheol.littletigersinit.Chat.RecyclerViewItemClickListener;
import com.example.parkyoungcheol.littletigersinit.Model.ArmsgData;
import com.example.parkyoungcheol.littletigersinit.Model.GeoPoint;
import com.example.parkyoungcheol.littletigersinit.R;
import com.example.parkyoungcheol.littletigersinit.ar_mainActivity;
import com.example.parkyoungcheol.littletigersinit.util.ArmsgListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import org.w3c.dom.Text;

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

    private ProgressDialog pDialog;

    private AlertDialog.Builder alertDialogBuilder;

    Double sLat, sLng;
    public TextView txtView, current_mylocation_text;
    String msg;//현재위치 지오코딩결과
    Button change_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armessage);

        change_list = (Button) findViewById(R.id.change_list);
        txtView = (TextView) findViewById(R.id.result);
        current_mylocation_text = (TextView) findViewById(R.id.current_location_text);

        change_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog();
            }
        });

        findMyLocation();
        if(geoCodingCoordiToAddress(sLng,sLat).length()>=15){
            msg = geoCodingCoordiToAddress(sLng,sLat).substring(0,15)+"...";
        }else{
            msg = geoCodingCoordiToAddress(sLng,sLat);
        }
        current_mylocation_text.setText(msg);

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
                m_oListView.addOnItemTouchListener(new RecyclerViewItemClickListener(ARmessageActivity.this, new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ArmsgData armsgData = mBoardList.get(position);

                        alertDialogBuilder = new AlertDialog.Builder(ARmessageActivity.this);

                        alertDialogBuilder.setTitle("AR 메시지 내용");
                        alertDialogBuilder
                                .setMessage(armsgData.getLabel())
                                .setCancelable(true)
                                .setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        })
                                .setNegativeButton("여기로 길안내 받기",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String longitude = Double.toString(armsgData.getLongitude());
                                                String latitude = Double.toString(armsgData.getLatitude());


                                                Intent intent = new Intent(ARmessageActivity.this, AR_navigationActivity.class);
                                                intent.putExtra("dest_lon_X_from_armessage", String.valueOf(longitude));
                                                intent.putExtra("dest_lat_Y_from_armessage", String.valueOf(latitude));
                                                intent.putExtra("dest_label_from_armessage", String.valueOf(armsgData.getAddress()));

                                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                                overridePendingTransition(R.anim.push_up_in,R.anim.non_anim);
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

    //구글 지오코딩. 좌표 -> 주소
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

        ArrayList<ArmsgData> oData = new ArrayList<ArmsgData>();
        ArmsgData oItem = new ArmsgData();
        mARMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String msg;

                oData.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Marker marker = new Marker();
                    ArmsgData abc = snapshot.getValue(ArmsgData.class); // 컨버팅되서 Bbs로........
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

            sLat = lat_Y;
            sLng = lon_X;
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

    public void ShowDialog()
    {

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final SeekBar seek = new SeekBar(this);
        seek.setMax(200);
        //popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle("AR 메시지 검색 범위 설정(km)\n");
        popDialog.setView(seek);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                txtView.setText(String.valueOf(progress));
                if(txtView.getText().equals("200"))
                {
                    txtView.setText("전체");
                }
            }
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        popDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (txtView.getText().equals("전체"))
                        {
                            mARMessageRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    mBoardList.clear();
                                    int i=0;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ArmsgData bbs = snapshot.getValue(ArmsgData.class); // 컨버팅되서 Bbs로........
                                        mBoardList.add(bbs);
                                        mBoardList.get(i).setAddress(geoCodingCoordiToAddress(mBoardList.get(i).getLongitude(), mBoardList.get(i).getLatitude()));
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

                            dialog.dismiss();
                        }
                        else {
                            mARMessageRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    findMyLocation();
                                    if(geoCodingCoordiToAddress(sLng,sLat).length()>=15){
                                        msg = geoCodingCoordiToAddress(sLng,sLat).substring(0,15)+"...";
                                    }else{
                                        msg = geoCodingCoordiToAddress(sLng,sLat);
                                    }
                                    current_mylocation_text.setText(msg);
                                    mBoardList.clear();
                                    int i=0;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ArmsgData bbs = snapshot.getValue(ArmsgData.class); // 컨버팅되서 Bbs로........
                                        if(calcDistance(sLat, sLng, bbs.getLatitude(), bbs.getLongitude()) <= Integer.parseInt(txtView.getText().toString())) {
                                            mBoardList.add(bbs);
                                            mBoardList.get(i).setAddress(geoCodingCoordiToAddress(mBoardList.get(i).getLongitude(), mBoardList.get(i).getLatitude()));
                                            i++;
                                        }
                                        else {
                                        }
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

                            dialog.dismiss();
                        }
                    }
                });
        popDialog.create();
        popDialog.show();
    }
}
