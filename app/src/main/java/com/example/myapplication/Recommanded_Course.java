package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Thread.ThreadTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
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
import java.util.Objects;

public class Recommanded_Course extends AppCompatActivity  implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{


    private MaterialToolbar toolbar;
    private ActionBar actionBar;
    private MaterialTextView toolbartext;
    private MaterialTextView PhoneCall;

    private GoogleMap GMap;

    private ArrayList<Recommanded_course_item> RecommandedCourseDataList = new ArrayList<>();
    private RecycleAdaptors_recommended_Curse_rank recycleAdaptors;
    private RecyclerView Recommanded_recycler_view;

    private LinearLayoutManager layoutManager;

    private JSONArray Course_total_array;
    private JSONArray Course_array;
    private String Starting_latitude;
    private String Starting_longitude;
    private String Address;

    private SharedPreferences login_information_pref;
    private SharedPreferences location_information_pref;

    private String ip;
    private String input;

    private ArrayList<course_item> course_list = new ArrayList<course_item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommanded__course);
        ip = getString(R.string.server_ip);

        Utils.setStatusBarColor(this, Utils.StatusBarcolorType.BLACK_STATUS_BAR);
        location_information_pref = getSharedPreferences("location_information", Activity.MODE_PRIVATE);

        Starting_latitude = location_information_pref.getString("current_latitude", "");
        Starting_longitude = location_information_pref.getString("current_longitude", "");
        Address = location_information_pref.getString("address", "주소를 선택해주세요.");

        toolbar = (MaterialToolbar)findViewById(R.id.MainActiviy_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //actionBar.setDisplayHomeAsUpEnabled(true);
        toolbartext = (MaterialTextView)findViewById(R.id.toolbar_textview);


        toolbartext.setText(Address);
        Recommanded_recycler_view = findViewById(R.id.recommended_courses_Recycler_view);
        layoutManager =  new LinearLayoutManager(Recommanded_Course.this);
        if(layoutManager != null){
            Recommanded_recycler_view.setLayoutManager(layoutManager);
        }
        else{
            Log.e("SensorFragment", "Error");
        }

        ThreadTask<Object> result = getThreadTask_getMAPInform(Starting_latitude, Starting_longitude, "/get_Recommend_information");
        result.execute(ip);

        int next_course_num = -1;
        int current_course_num;

        if(Course_total_array.length() == 0){
            Toast.makeText(Recommanded_Course.this, "해당 지역에는 등록된 추천코스가 없습니다.", Toast.LENGTH_SHORT).show();
            //자동으로 뒤로가기 만들기
        }
        else {
            try {
                for(int i = 0 ; i < Course_total_array.length() ; i++){
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

                    Log.e("Recommanded_course test", String.format("%s %s %s %s %s %s", Course_name, Course_num,Name, address, Latitude, Longitude));
                    current_course_num = Integer.parseInt(Course_num);

                    course_list.add(new course_item(Name, address, Latitude, Longitude));


                    if(i + 1 < Course_total_array.length()){
                        JSONObject temp_object2 = Course_total_array.getJSONObject(i+1);
                        next_course_num = temp_object2.getInt("Course_num");
                        if(current_course_num != next_course_num){
                            for(int j = 0 ; j < course_list.size() ; j++){
                                Log.e("Recommanded_course test222", String.format("%s %s %s %s", course_list.get(j).getPlace_name(), course_list.get(j).getAddress(), course_list.get(j).getLatitude(), course_list.get(j).getLongitude()));
                            }
                            RecommandedCourseDataList.add(new Recommanded_course_item(Course_name, Course_num,Preference, course_list));
                            course_list = new ArrayList<>();
                        }
                    }
                    else if(i == Course_total_array.length() - 1){
                        for(int j = 0 ; j < course_list.size() ; j++){
                            Log.e("Recommanded_course test222", String.format("%s %s %s %s", course_list.get(j).getPlace_name(), course_list.get(j).getAddress(), course_list.get(j).getLatitude(), course_list.get(j).getLongitude()));
                        }
                        RecommandedCourseDataList.add(new Recommanded_course_item(Course_name, Course_num,Preference, course_list));
                        course_list = new ArrayList<>();
                    }
                }
            }catch (Exception e){

            }
        }

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        // 맵이 실행되면 onMapReady 실행
        mapFragment.getMapAsync(this);


        /**
         * 근처 병원 위치 다 받아오기
         * */

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

    private ThreadTask<Object> getThreadTask(String email, String Router_name){

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

                URL url = new URL(urls[0] +Router_name);

                con = (HttpURLConnection) url.openConnection();

                sendObject.put("email_address", email);

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

                    this.response_result = (Integer) responseJSON.get("key");
                    this.error_code = (String) responseJSON.get("err_code");
                    input = (String) responseJSON.getString("input");
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

    public com.google.android.gms.maps.GoogleMap getGoogleMap() {
        return GMap;
    }

    public void setGoogleMap(com.google.android.gms.maps.GoogleMap googleMap) {
        GMap = googleMap;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        setGoogleMap(googleMap);
        GMap.setOnMarkerClickListener(this);
        // 구글에서 등록한 api와 엮어주기
        // 시작위치를 서울 시청으로 변경
        /**
         * 센서 위치 시작 지점 받아오기
         * */
        LatLng Starting_Point = new LatLng(Double.parseDouble(Starting_latitude), Double.parseDouble(Starting_longitude)); // 스타팅 지점
        getGoogleMap().moveCamera(CameraUpdateFactory.newLatLngZoom(Starting_Point, 13));

        //GMap.animateCamera(CameraUpdateFactory.zoomTo(14));// 키우면 더 확대

        // 시작시 마커 생성하기 누르면 title과 snippet이 뜬다.
        MarkerOptions markerOptions = new MarkerOptions();
        double Latitude;
        double Longtitude;

        Marker marker = null;

        //맵 로드 된 이후
        GMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //Toast.makeText(NearHospital.this, "Map로딩성공", Toast.LENGTH_SHORT).show();
            }
        });

        //카메라 이동 시작
        GMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                //Log.d("set>>","start");
            }
        });

        // 카메라 이동 중
        GMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                //Log.d("set>>","move");
            }
        });

        // 지도를 클릭하면 호출되는 이벤트
        GMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // 기존 마커 정리
                //googleMap.clear();
                // 클릭한 위치로 지도 이동하기
                GMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                // 신규 마커 추가
//                MarkerOptions newMarker=new MarkerOptions();
//                newMarker.position(latLng);
//                googleMap.addMarker(newMarker);
            }
        });

        recycleAdaptors = new RecycleAdaptors_recommended_Curse_rank(ip, getGoogleMap());
        recycleAdaptors.setItems(RecommandedCourseDataList);
        Recommanded_recycler_view.setAdapter(recycleAdaptors);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //System.out.println(layoutManager.canScrollVertically());
//                int position = 0;
//                for( Hospital hospital : HospitalDataList ){
//                    if(hospital.getHospital_name().equals(marker.getTitle())){
//                        System.out.println(hospital.getHospital_name());
//                        Hospital_recycler_view.smoothScrollToPosition(position);
//                    }
//                    position++;
//                }
            }
        }, 200);
        return false;
    }
}