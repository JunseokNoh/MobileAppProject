package com.example.myapplication;

public class Course_rank {
    private String Course_name;
    private String Latitude;
    private String Longitude;
    private String Address;

    public Course_rank(String place_name, String address, String latitude, String longitude){
        Course_name = place_name;
        Address = address;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getAddress() {
        return Address;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
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

    public void setCourse_name(String course_name) {
        Course_name = course_name;
    }

    public String getCourse_name() {
        return Course_name;
    }
}