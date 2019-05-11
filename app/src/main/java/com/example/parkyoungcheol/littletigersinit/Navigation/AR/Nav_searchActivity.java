package com.example.parkyoungcheol.littletigersinit.Navigation.AR;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.parkyoungcheol.littletigersinit.MainActivity;
import com.example.parkyoungcheol.littletigersinit.Model.DataSet;
import com.example.parkyoungcheol.littletigersinit.Model.GeoPoint;
import com.example.parkyoungcheol.littletigersinit.R;
import com.example.parkyoungcheol.littletigersinit.util.CustomListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nav_searchActivity extends AppCompatActivity {
    String clientId = "28dMbtnQ2ce6ytQckJ6h";//애플리케이션 클라이언트 아이디값";
    String clientSecret = "ZNsxiYjwC2";//애플리케이션 클라이언트 시크릿값";
    Button searchBtn, current_location_btn;
    EditText searchEdit;
    private String keyword = "키워드값";
    public static StringBuilder sb;//
    int display = 10; // 가져올 데이터의 수
    int count = 10; // 가져올 page 수
    String sort = "random"; // similar한 데이터를 가져옴
    private ListView listView;
    private CustomListAdapter adapter;
    private List<DataSet> list = new ArrayList<DataSet>(); //데이터셋 클래스 만들어야함
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_search);

        current_location_btn = (Button) findViewById(R.id.current_location_btn);
        searchBtn = (Button) findViewById(R.id.search_btn);
        searchEdit = (EditText) findViewById(R.id.search_editText);
        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, list);
        listView.setAdapter(adapter);

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchEdit, 0);

        current_location_btn.setOnClickListener(v -> {
            GeoPoint myLocation = findMyLocation();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("getX", String.valueOf(myLocation.getX()));
            resultIntent.putExtra("getY", String.valueOf(myLocation.getY()));
            resultIntent.putExtra("getTitle", "현재 위치");
            resultIntent.putExtra("coordiStyle","WGS");
            setResult(RESULT_OK, resultIntent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = searchEdit.getText().toString();

                //키보드 내려가게 하기
                imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                pDialog = new ProgressDialog(Nav_searchActivity.this);
                // Showing progress dialog before making http request
                pDialog.setMessage("Loading...");
                pDialog.show();
                requestWithSomeHttpHeaders();
            }
        });

        searchEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    keyword = searchEdit.getText().toString();

                    //키보드 내려가게 하기
                    imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                    pDialog = new ProgressDialog(Nav_searchActivity.this);
                    // Showing progress dialog before making http request
                    pDialog.setMessage("Loading...");
                    pDialog.show();
                    requestWithSomeHttpHeaders();
                    return true;
                }
                return false;
            }
        });

    }

    public void requestWithSomeHttpHeaders() {
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
    }

    private GeoPoint findMyLocation() {
        GeoPoint myGeo = null;
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 밑줄 권한때문에 그런거임
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( Nav_searchActivity.this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
