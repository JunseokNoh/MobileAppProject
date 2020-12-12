package com.example.myapplication;

public class custom_course_item {
    private String Custom_course_name;
    private String Custom_course_detail;
    private String Latitude;
    private String Longitude;

    public custom_course_item(String custom_course_name, String custom_course_detail, String latitude, String longitude){
        Custom_course_name = custom_course_name;
        Custom_course_detail = custom_course_detail;
        Latitude = latitude;
        Longitude = longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public void setCustom_course_detail(String custom_course_detail) {
        Custom_course_detail = custom_course_detail;
    }

    public void setCustom_course_name(String custom_course_name) {
        Custom_course_name = custom_course_name;
    }

    public String getCustom_course_detail() {
        return Custom_course_detail;
    }

    public String getCustom_course_name() {
        return Custom_course_name;
    }

    public String getLongitude() {
        return Longitude;
    }

    public String getLatitude() {
        return Latitude;
    }
}