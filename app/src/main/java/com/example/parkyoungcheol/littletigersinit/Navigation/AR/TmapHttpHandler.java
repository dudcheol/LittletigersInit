package com.example.parkyoungcheol.littletigersinit.Navigation.AR;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.stream.Stream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class TmapHttpHandler extends AsyncTask<String, Void, String> {
    private static final String TAG = "TmapHttpHandler";
    private String str=null, receiveMsg=null;
    //private String urlStr="https://api2.sktelecom.com/tmap/routes/pedestrian?version=1&format=json";
    //private String startX="126.733638", startY="37.340267", endX="126.742849", endY="37.351813";
    private static final String T_MAP_APP_KEY = "6afd2dfb-333b-4618-bc36-f894407adc82";

    private String startName=null;
    {
        try {
            startName = URLEncoder.encode("출발지","UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private String endName=null;
    {
        try {
            endName = URLEncoder.encode("목적지","UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        URL url = null;
        try {
            /*urlStr += "&startX="+startX;
            urlStr += "&startY="+startY;
            urlStr += "&endX="+endX;
            urlStr += "&endY="+endY;
            urlStr += "&startName="+startName;
            urlStr += "&endName="+endName;
            urlStr += "&appKey=6afd2dfb-333b-4618-bc36-f894407adc82";*/
            String urlStr = params[0];
            Log.i("패럼 url",urlStr);

            urlStr += "&startName="+ startName + "&endName="+ endName + "&appKey=" + T_MAP_APP_KEY;

            url=new URL(urlStr);

            Log.i("패럼url에 파라미터 추가",urlStr);

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();
                Log.i("receiveMsg : ", receiveMsg);

                reader.close();
            } else {
                Log.i("통신 결과", conn.getResponseCode() + "에러");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return receiveMsg;

    }
}
