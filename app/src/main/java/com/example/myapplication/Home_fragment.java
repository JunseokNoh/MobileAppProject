package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.myapplication.Thread.ThreadTask;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Home_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private RecycleAdaptors_Curse_rank recycleAdaptors;
    private  RecyclerView.LayoutManager layoutManager;

    private MaterialButton Recommended_button;

    private String Starting_latitude;
    private String Starting_longitude;
    private SharedPreferences login_information_pref;
    private SharedPreferences location_information_pref;

    private JSONArray Course_total_array;
    private ArrayList<Recommanded_course_item> RecommandedCourseDataList;
    private ArrayList<course_item> course_list = new ArrayList<course_item>();

    public Home_fragment() {
        // Required empty public constructor
    }

    /***
     * 추천코스 받아오기
     */

    public static Home_fragment newInstance(String param1, String param2) {
        Home_fragment fragment = new Home_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String ip = getString(R.string.server_ip);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_fragment, container, false);
        RecommandedCourseDataList = new ArrayList<>();
        ((MainActivity) getActivity()).setToolbar();

//        View header = inflater.inflate(R.layout.activity_main, null, false);
//        MaterialTextView toolbartext = (MaterialTextView) header.findViewById(R.id.toolbar_textview);
//
//        SharedPreferences login_information_pref = getActivity().getSharedPreferences("login_information", Context.MODE_PRIVATE);
//        String Address = login_information_pref.getString("address", "주소를 선택해주세요.");

//        toolbartext.setText(Address);

        Recommended_button = v.findViewById(R.id.recommended_courses);
        Recommended_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Recommanded_Course.class);
                startActivity(intent);
            }
        });
        recyclerView = (RecyclerView) v.findViewById(R.id.Recycler_view);

        layoutManager = new LinearLayoutManager(getContext());
        if (layoutManager != null) {
            recyclerView.setLayoutManager(layoutManager);
        } else {
            Log.e("SensorFragment", "Error");
        }

        location_information_pref = getActivity().getSharedPreferences("location_information", Activity.MODE_PRIVATE);

        Starting_latitude = location_information_pref.getString("current_latitude", "");
        Starting_longitude = location_information_pref.getString("current_longitude", "");

        ThreadTask<Object> result = getThreadTask_getMAPInform(Starting_latitude, Starting_longitude, "/get_Recommend_information_top5");
        result.execute(ip);

        if(Course_total_array.length() > 0){
            int next_course_num = -1;
            int current_course_num;

            try {
                for (int i = 0; i < Course_total_array.length(); i++) {
                    Log.e("Recommaned_course", "들어왓다.");
                    JSONObject temp_object = Course_total_array.getJSONObject(i);
                    System.out.println(temp_object);
                    String Course_name = temp_object.getString("Course_name");
                    String Name = temp_object.getString("Name");
                    String address = temp_object.getString("Address");
                    String Latitude = Double.toString(temp_object.getDouble("latitude"));
                    String Longitude = Double.toString(temp_object.getDouble("longitude"));
                    String Course_num = Integer.toString(temp_object.getInt("Course_num"));
                    String Preference = Integer.toString(temp_object.getInt("Preference"));
                    if(Preference == null){
                        Preference ="0";
                    }
                    Log.e("Recommanded_course test", String.format("%s %s %s %s %s %s", Course_name, Course_num, Name, address, Latitude, Longitude));
                    current_course_num = Integer.parseInt(Course_num);

                    course_list.add(new course_item(Name, address, Latitude, Longitude,"0",Preference ));

                    if (i + 1 < Course_total_array.length()) {
                        JSONObject temp_object2 = Course_total_array.getJSONObject(i + 1);
                        next_course_num = temp_object2.getInt("Course_num");
                        if (current_course_num != next_course_num) {
                            for (int j = 0; j < course_list.size(); j++) {
                                Log.e("Recommanded_course test222", String.format("%s %s %s %s", course_list.get(j).getPlace_name(), course_list.get(j).getAddress(), course_list.get(j).getLatitude(), course_list.get(j).getLongitude()));
                            }
                            RecommandedCourseDataList.add(new Recommanded_course_item(Course_name, Course_num, Preference, course_list));
                            course_list = new ArrayList<>();
                        }
                    } else if (i == Course_total_array.length() - 1) {
                        for (int j = 0; j < course_list.size(); j++) {
                            Log.e("Recommanded_course test222", String.format("%s %s %s %s", course_list.get(j).getPlace_name(), course_list.get(j).getAddress(), course_list.get(j).getLatitude(), course_list.get(j).getLongitude()));
                        }
                        RecommandedCourseDataList.add(new Recommanded_course_item(Course_name, Course_num, Preference, course_list));
                        course_list = new ArrayList<>();
                    }
                }

            } catch (Exception e) {

            }

            recycleAdaptors = new RecycleAdaptors_Curse_rank(ip);
            recycleAdaptors.setItems(RecommandedCourseDataList);
            recyclerView.setAdapter(recycleAdaptors);
        }
        else{
            Toast.makeText(getContext(), "해당 지역에 검색된 결과가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    private ThreadTask<Object> getThreadTask_getMAPInform(String starting_latitude, String starting_longitude, String Router_name){

        return new ThreadTask<Object>() {
            private int response_result;
            private String error_code;
            @Override
            protected void onPreExecute() {// excute 전에

            }

            @Override
            protected void doInBackground(String... urls) throws IOException, JSONException {//background로 돌아갈것
                HttpURLConnection con = null;
                JSONObject sendObject = new JSONObject();
                BufferedReader reader = null;
                URL url = new URL(urls[0] + Router_name);

                con = (HttpURLConnection) url.openConnection();

//                sendObject.put("kind", kind);
                sendObject.put("latitude", starting_latitude);
                sendObject.put("longitude", starting_longitude);
                Log.e("위경도 전송완료","위경도 전송완료");

                con.setRequestMethod("POST");//POST방식으로 보냄
                con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                con.setRequestProperty("Accept", "application/json");//서버에 response 데이터를 html로 받음
                con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미

                OutputStream outStream = con.getOutputStream();
                outStream.write(sendObject.toString().getBytes());
                outStream.flush();

                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    InputStream stream = con.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData = null;
                    int nLength = 0;
                    while ((nLength = stream.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                        baos.write(byteBuffer, 0, nLength);
                    }
                    byteData = baos.toByteArray();
                    String response = new String(byteData);
                    JSONObject responseJSON = new JSONObject(response);
                    Course_total_array = (JSONArray) responseJSON.get("data");

                    Log.e("twtwtwsdfw", String.format("%s", Course_total_array));
                }
            }

            @Override
            protected void onPostExecute() {

            }

            @Override
            public int getResult() {
                return response_result;
            }

            @Override
            public String getErrorCode() {
                return error_code;
            }
        };
    }


}