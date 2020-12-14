package com.example.myapplication;

import java.util.ArrayList;

public class Recommanded_course_item {
    private String Course_name;
    private String Course_id;
    private String Preference;
    private ArrayList<course_item> Course_list = new ArrayList<>();

    public Recommanded_course_item(String course_name, String course_id, String preference, ArrayList<course_item> course_list){
        Course_name = course_name;
        Course_id = course_id;
        Preference = preference;
        Course_list = course_list;
    }

    public void setPreference(String preference) {
        Preference = preference;
    }

    public String getPreference() {
        return Preference;
    }

    public void add_item(course_item item){
        Course_list.add(item);
    }

    public void setCourse_name(String course_name) {
        Course_name = course_name;
    }

    public String getCourse_name() {
        return Course_name;
    }

    public void setCourse_id(String course_id) {
        Course_id = course_id;
    }

    public void setCourse_list(ArrayList<course_item> course_list) {
        Course_list = course_list;
    }

    public ArrayList<course_item> getCourse_list() {
        return Course_list;
    }

    public String getCourse_id() {
        return Course_id;
    }


}