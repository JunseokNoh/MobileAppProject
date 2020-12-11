package com.example.myapplication;

import java.util.ArrayList;

public class course_item {
    private String Place_name;
    private String Latitude;
    private String Longitude;

    public course_item(String place_name, String latitude, String longitude){
        Place_name = place_name;
        Latitude = latitude;
        Longitude = longitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public void setPlace_name(String place_name) {
        Place_name = place_name;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public String getPlace_name() {
        return Place_name;
    }
}