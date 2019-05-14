package com.example.parkyoungcheol.littletigersinit.Chat;

import lombok.Data;

@Data
public class LocationMessage extends Message {
    private String locationText;
    private String longitude;
    private String latitude;

    public String getLocationText() {
        return locationText;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLocationText(String locationText) {
        this.locationText = locationText;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

}
