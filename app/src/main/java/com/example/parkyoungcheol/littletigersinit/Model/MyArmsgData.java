
package com.example.parkyoungcheol.littletigersinit.Model;

import android.view.View;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class MyArmsgData
{
    private String label;
    private Double latitude;
    private Double longitude;
    private String address;
    private Double distance;
    private String UID;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public View.OnClickListener onClickListener;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public MyArmsgData(){

    }
    public MyArmsgData(String label, Double latitude, Double longitude){
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;

    }


}