package com.example.parkyoungcheol.littletigersinit.Model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.example.parkyoungcheol.littletigersinit.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

// 데이터 소스를 실질적으로 다루는 클래스
public class DataSource {


    // 데이터 소스와 데이터 포맷의 열거형 변수
    public enum DATASOURCE {
        CAFE,BUSSTOP,Convenience,Restaurant,BANK,HOSPITAL,ACCOMMODATION,DOCUMENT, IMAGE, VIDEO,SEARCH,NAVI
    };

    public enum DATAFORMAT {
        NAVER_CATEGORY,NAVER_SEARCH,NAVI,FIREBASE
    };

    public static Bitmap cafeIcon; //카페
    public static Bitmap busIcon; // 버스
    public static Bitmap restraurantIcon; // 레스토랑
    public static Bitmap convenienceIcon; // 편의점
    public static Bitmap bankIcon; // 은행
    public static Bitmap hospitalIcon; //병원
    public static Bitmap accommodationIcon; // 숙박

    public static Bitmap documentIcon;
    public static Bitmap imageIcon;
    public static Bitmap videoIcon;

    private static final String NAVER_MAP_URL =	"http://map.naver.com/findroute2/findWalkRoute.nhn?call=route2&output=json&coord_type=naver&search=0";
    private static final String T_MAP_URL_KATHECH = "https://api2.sktelecom.com/tmap/routes/pedestrian?version=1&format=json&reqCoordType=KATECH&resCoordType=WGS84GEO";
    private static final String T_MAP_URL_WGS84GEO = "https://api2.sktelecom.com/tmap/routes/pedestrian?version=1&format=json";

    public DataSource() {

    }

    // 리소스로부터 각 아이콘 생성
    public static void createIcons(Resources res) {

    }

    // 아이콘 비트맵의 게터
    public static Bitmap getBitmap(String ds) {
        Bitmap bitmap = null;
        switch (ds) {
            case "CAFE":
                bitmap = cafeIcon;
                break;

            case "BUSSTOP":
                bitmap = busIcon;
                break;

            case "Convenience":
                bitmap = convenienceIcon;
                break;

            case "Restaurant":
                bitmap = restraurantIcon;
                break;

            case "BANK":
                bitmap = bankIcon;
                break;

            case "ACCOMMODATION":
                bitmap = accommodationIcon;
                break;

            case "HOSPITAL":
                bitmap = hospitalIcon;
                break;

        }
        return bitmap;
    }

    // 데이터 소스로부터 데이터 포맷을 추출
    public static DATAFORMAT dataFormatFromDataSource(DATASOURCE ds) {
        DATAFORMAT ret;
        switch (ds) {

            // 주위 편의시설
            case CAFE:
            case BUSSTOP: // 버스 정류장
            case Convenience:
            case Restaurant:
            case BANK:
            case HOSPITAL:
            case ACCOMMODATION:
                ret = DATAFORMAT.NAVER_CATEGORY;
                break;
            case SEARCH:
                ret = DATAFORMAT.NAVER_SEARCH;
                break;
            case NAVI:
                ret = DATAFORMAT.NAVI;
                break;
            // 파이어베이스 부분
            case DOCUMENT:
            case IMAGE:
            case VIDEO:
                ret = DATAFORMAT.FIREBASE;
                break;

            default:
                ret = DATAFORMAT.NAVER_CATEGORY;
                break;
        }
        return ret;    // 포맷 리턴
    }

    // 각 정보들로 완성된 URL 리퀘스트를 생성


    public static String createRequestCategoryURL(String source, double lon, double lat) {
        String ret = "";    // 결과 스트링

        // 각 소스에 따른 URL 리퀘스트를 완성한다
        switch (source) {

            // Test URL(정왕역 좌표) : "http://map.naver.com/search2/interestSpot.nhn?type=CAFE&boundary=126.73278739132492%3B37.34197819069592%3B126.75278739132492%3B37.36197819069592&pageSize=5"
            // 네이버 웹페이지에서 가져오는 정보임
            case "CAFE":
                ret = "http://map.naver.com/search2/interestSpot.nhn?type=CAFE&boundary=" + Double.toString(lon - 0.01) + "%3B" +
                        Double.toString(lat - 0.01) + "%3B" + Double.toString(lon + 0.01) +
                        "%3B" + Double.toString(lat + 0.01) + "&pageSize=5";
                break;

            case "BUSSTOP": // 버스 정류장
                ret = "http://map.naver.com/search2/searchBusStopWithinRectangle.nhn?bounds="+ Double.toString(lon - 0.01) + "%3B" +
                        Double.toString(lat - 0.01) +"%3B" +  Double.toString(lon + 0.01) + "%3B"
                        + Double.toString(lat + 0.01) +"&count=100&level10";
                break;

            case "CONVENIENCE": // 편의점
                ret = "http://map.naver.com/search2/interestSpot.nhn?type=STORE&boundary=" + Double.toString(lon - 0.01) + "%3B" +
                        Double.toString(lat - 0.01) + "%3B" + Double.toString(lon + 0.01) +
                        "%3B" + Double.toString(lat + 0.01) + "&pageSize=5";
                break;

            case "RESTAURANT": //식당
                ret =  "http://map.naver.com/search2/interestSpot.nhn?type=DINING_KOREAN&boundary=" + Double.toString(lon - 0.02) + "%3B" +
                        Double.toString(lat - 0.01) + "%3B" + Double.toString(lon + 0.01) +
                        "%3B" + Double.toString(lat + 0.01) + "&pageSize=5";
                break;


            case "BANK": //은행
                ret =  "http://map.naver.com/search2/interestSpot.nhn?type=BANK&boundary=" + Double.toString(lon - 0.01) + "%3B" +
                        Double.toString(lat - 0.01) + "%3B" + Double.toString(lon + 0.01) +
                        "%3B" + Double.toString(lat + 0.01) + "&pageSize=5";
                break;

            case "ACCOMMODATION":
                ret =  "http://map.naver.com/search2/interestSpot.nhn?type=ACCOMMODATION&boundary=" + Double.toString(lon - 0.02) + "%3B" +
                        Double.toString(lat - 0.01) + "%3B" + Double.toString(lon + 0.01) +
                        "%3B" + Double.toString(lat + 0.01) + "&pageSize=5";
                break;

            case "HOSPITAL":
                ret =  "http://map.naver.com/search2/interestSpot.nhn?type=HOSPITAL&boundary=" + Double.toString(lon - 0.02) + "%3B" +
                        Double.toString(lat - 0.01) + "%3B" + Double.toString(lon + 0.01) +
                        "%3B" + Double.toString(lat + 0.01) + "&pageSize=5";
                break;
        }

        return ret;
    }

    public static String createNaverGeoAPIRequestURL(String locationName) {
        String ret;
        ret =  "https://openapi.naver.com/v1/map/geocode?query=" + locationName;
        Log.i("NaverGeoURL", ret);

        return ret;
    }

    public static String createNaverReverseGeoAPIRequcetURL(double lat, double lon) {
        String ret;
        String queryLocation = String.valueOf(lon)+","+String.valueOf(lat);
        ret = "https://openapi.naver.com/v1/map/reversegeocode?query=" + queryLocation;
        Log.i("NaverReverseGeoURL", ret);
        return ret;
    }

    public static String createNaverSearchCallBackURL(String _query) {
        String ret;
        ret = "http://ac.map.naver.com/ac?q=" + _query +
                "&st=10&r_lt=10&r_format=json";
        return ret;
    }

    // 일정 장소 하나 검색
    public static String createNaverSearchRequestURL(String _query) {
        String ret;
        ret = "https://openapi.naver.com/v1/search/local.json?" +
                "query=" + _query +
                "&display=10&start=1&sort=random";

        Log.i("검색 url test",ret);

        return ret;
    }

    // 네이버 지도 경로 검색
    //public static String createNaverMapRequestURL(double start_lon, double start_lat, double end_lon, double end_lat) {
    public static String createNaverMapRequestURL(double start_lon, double start_lat, double end_lon, double end_lat) {
        String ret;
        ret = NAVER_MAP_URL;

        /*/*ret += "&start=" + Double.toString(start_lon) + "%2C" + Double.toString(start_lat)
                + "&destination=" + Double.toString(end_lon) + "%2C" + Double.toString(end_lat);//*/
        ret += "&start=" + Double.toString(start_lon) + "%2C" + Double.toString(start_lat)
                + "&destination=" + Double.toString(end_lon) + "%2C" + Double.toString(end_lat);
        //ret은 http://map.naver.com/findroute2/findWalkRoute.nhn?call=route2&output=json&coord_type=naver&search=0&start= *출발지lon* %2C *출발지lat* &destination= *도착지lon* %2C *도착지lat*

        return ret;
    }

    // 티 맵 지도 보행자 경로 검색 - KATECH 좌표계일 경우
    public static String createTMapRequestURL_KATECH(int start_lon, int start_lat, int end_lon,int end_lat){
        String ret;
        ret=T_MAP_URL_KATHECH;

        ret += "&startX=" + Integer.toString(start_lon) + "&startY=" + Integer.toString(start_lat)
                + "&endX=" + Integer.toString(end_lon) + "&endY=" + Integer.toString(end_lat);

        return ret;
    }

    // 티 맵 지도 보행자 경로 검색 - 위도,경도 좌표계일 경우
    public static String createTMapRequestURL_WGS84GEO(double start_lon, double start_lat, double end_lon, double end_lat){
        String ret;
        ret=T_MAP_URL_WGS84GEO;

        ret += "&startX=" + Double.toString(start_lon) + "&startY=" + Double.toString(start_lat)
                + "&endX=" + Double.toString(end_lon) + "&endY=" + Double.toString(end_lat);

        return ret;
    }


    // 각 소스에 따른 색을 리턴
    public static int getColor(DATASOURCE datasource) {
        int ret;
        switch (datasource) {

            default:
                ret = Color.GREEN;
                break;
        }
        return ret;
    }

}
