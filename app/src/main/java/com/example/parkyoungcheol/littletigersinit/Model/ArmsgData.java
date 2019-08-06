
package com.example.parkyoungcheol.littletigersinit.Model;

import android.view.View;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmsgData
{
    public String label;
    public Double latitude;
    public Double longitude;
    public String address;
    public Double distance;
    public int likecnt;
    public ArrayList<String> likelist;
    public String key;
    public String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArmsgData(){

    }
    public ArmsgData(String label, Double latitude, Double longitude){
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;

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

    public int getLikecnt() {
        return likecnt;
    }

    public void setLikecnt(int likecnt) {
        this.likecnt = likecnt;
    }

    public ArrayList<String> getLikelist() {
        return likelist;
    }

    public void setLikelist(ArrayList<String> likelist) {
        this.likelist = likelist;
    }

    /*public List<String> getLikelist() {
        return likelist;
    }

    public void setLikelist(List<String> likelist) {
        this.likelist = likelist;
    }*/
}