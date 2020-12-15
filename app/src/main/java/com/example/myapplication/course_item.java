package com.example.myapplication;

import java.util.ArrayList;

public class course_item {
    private String Place_name;
    private String Latitude;
    private String Longitude;
    private String Address;
    private String Place_number;
    private String Preference;

    public course_item(String place_name, String address, String latitude, String longitude, String place_number, String preference){
        Place_name = place_name;
        Address = address;
        Latitude = latitude;
        Longitude = longitude;
        Place_number = place_number;
        Preference = preference;
    }

    public void setPreference(String preference) {
        Preference = preference;
    }

    public String getPreference() {
        return Preference;
    }

    public void setPlace_number(String place_number) {
        Place_number = place_number;
    }

    public String getPlace_number() {
        return Place_number;
    }

    public void setAddress(String address) {
        Address = address;
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

    public String getAddress() {
        return Address;
    }
}