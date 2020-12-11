package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

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

public class Recommanded_Course extends AppCompatActivity  implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{


    private MaterialToolbar toolbar;
    private ActionBar actionBar;
    private MaterialTextView toolbartext;
    private MaterialTextView PhoneCall;

    private GoogleMap GMap;

    private ArrayList<Recommanded_course_item> RecommandedCourseDataList = new ArrayList<>();
    private RecycleAdaptors_recommended_Curse_rank recycleAdaptors;
    private RecyclerView Recommanded_recycler_view;
    private TextView Phone_call;

    private LinearLayoutManager layoutManager;

    private Double start_latitude;
    private Double start_longitude;
    private String target_phone;
    private String data_array;
    private JSONArray Course_total_array;
    private JSONArray Course_array;
    private String Starting_latitude;
    private String Starting_longitude;
    String Email;
    private SharedPreferences login_information_pref;

    private String ip;
    private String input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommanded__course);
        ip = getString(R.string.server_ip);

        Utils.setStatusBarColor(this, Utils.StatusBarcolorType.BLACK_STATUS_BAR);

        toolbar = (MaterialToolbar)findViewById(R.id.MainActiviy_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        toolbartext = (MaterialTextView)findViewById(R.id.toolbar_textview);

        login_information_pref = getSharedPreferences("login_information", Context.MODE_PRIVATE);
        Email = login_information_pref.getString("email", Email);
        String Address = login_information_pref.getString("address", "주소를 선택해주세요.");
        toolbartext.setText(Address);
        Recommanded_recycler_view = findViewById(R.id.recommended_courses_Recycler_view);
        layoutManager =  new LinearLayoutManager(Recommanded_Course.this);
        if(layoutManager != null){
            Recommanded_recycler_view.setLayoutManager(layoutManager);
        }
        else{
            Log.e("SensorFragment", "Error");
        }

        ThreadTask<Object> result = getThreadTask(Email, "/id_duplication_check");
        result.execute(ip);

//        String input = "{" +
//                "Course_Name: '추천코스1'," +
//                "Course_id: '128.59296105844146'," +
//                "data_array: '[{\"hospital_name\":\"광개토병원\",\"hospital_location\":\"대구광역시 중구 성내1동 중앙대로 366\",\"hospital_phone_num\":\"053-242-0119\",\"category\":\"종합병원\",\"time\":\"24시간 영업\",\"latitude\":\"35.866152697316956\",\"longitude\":\" 128.59380216598595\"}" +
//                ",{\"hospital_name\":\"다정 연합병원\",\"hospital_location\":\"대구광역시 중구 봉산동 52-31\",\"hospital_phone_num\":\"053-425-4604\",\"category\":\"종합병원\",\"time\":\"24시간 영업\",\"latitude\":\"35.86487021645115\",\"longitude\":\"128.5957011691545\"}" +
//                ",{\"hospital_name\":\"라파엘 병원\",\"hospital_location\":\"대구광역시 중구 남산동 700-11\",\"hospital_phone_num\":\"053-762-8228\",\"category\":\"종합병원\",\"time\":\"24시간 영업\",\"latitude\":\"35.86085586470934\",\"longitude\":\"128.59192351545732\"}]'"+
//                "}";

            String input ="{"+
                    "data_array:" + "[{\"Course_Name\" : \"추천코스1\", \"Course_id\" : \"0\", \"Course\" : [{\"Place_name\" : \"place1\", \"latitude\" : \"35.88565632201131\", \"longitude\" : \"128.6119264468393\"},{\"Place_name\" : \"place2\", \"latitude\" : \"35.88492137332584\", \"longitude\" : \"128.60971990159663\"},{\"Place_name\" : \"place3\", \"latitude\" : \"35.88253371936135\", \"longitude\" : \"128.60998957938452\"}]},{\"Course_Name\" : \"추천코스2\", \"Course_id\" : \"1\", \"Course\" : [{\"Place_name\" : \"place4\", \"latitude\" : \"35.884365190327685\", \"longitude\" : \"128.60770459025358\"},{\"Place_name\" : \"place5\", \"latitude\" : \"35.88213627620797\", \"longitude\" : \"128.60587875777216\"},{\"Place_name\" : \"place6\", \"latitude\" : \"35.884528069549695\", \"longitude\" : \"128.6060717434991\"} ]}]"
                    +"}";
        JSONObject input_object = null;
        try {
            input_object = new JSONObject(input);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Intent intent = getIntent();
        try {
//            start_latitude = Double.parseDouble(input_object.getString("latitude"));
//            start_longitude = Double.parseDouble(input_object.getString("longitude"));
//            Log.e("NearHospital", Double.toString(start_latitude));
//            Log.e("NearHospital", Double.toString(start_longitude));
            data_array = input_object.getString("data_array");
            Course_total_array = null;
            Course_total_array = new JSONArray(data_array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i = 0 ; i <= Course_total_array.length() ; i++) {
            try {
                ArrayList<course_item> course_list = new ArrayList<course_item>();

                JSONObject Recommanded_course_object = Course_total_array.getJSONObject(i);
                String Course_name = Recommanded_course_object.getString("Course_Name");
                String Course_id = Recommanded_course_object.getString("Course_id");
                Course_array = Recommanded_course_object.getJSONArray("Course");
                Log.e("Recommanded_Course_Test", String.format("%s %s", Course_name, Course_id));
                for(int j = 0 ; j < Course_array.length() ; j ++){

                    JSONObject temp_object = Course_array.getJSONObject(j);
                    String Place_name = temp_object.getString("Place_name");
                    String Latitude = temp_object.getString("latitude");
                    String Longitude = temp_object.getString("longitude");
                    if(i ==0 && j== 0){
                        Starting_latitude = Latitude;
                        Starting_longitude = Longitude;
                    }
                    course_list.add(new course_item(Place_name, Latitude, Longitude));
                    Log.e("Recommanded_Course_Test", String.format("%s %s %s", Place_name, Latitude, Longitude));
                }
                RecommandedCourseDataList.add(new Recommanded_course_item(Course_name, Course_id, course_list));
            } catch (JSONException e) {
                e.printStackTrace();
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
        getGoogleMap().moveCamera(CameraUpdateFactory.newLatLngZoom(Starting_Point, 14));

        //GMap.animateCamera(CameraUpdateFactory.zoomTo(14));// 키우면 더 확대

        // 시작시 마커 생성하기 누르면 title과 snippet이 뜬다.
        MarkerOptions markerOptions = new MarkerOptions();
        double Latitude;
        double Longtitude;

        Marker marker = null;
//        for(int i = 0 ; i < HospitalDataList.size() ; i++){
////            Latitude = Double.parseDouble(HospitalDataList.get(i).getLatitude());
////            Longtitude = Double.parseDouble(HospitalDataList.get(i).getLongtitude());
////
//////            markerOptions.position(new LatLng(Latitude,Longtitude));
//////            markerOptions.title(HospitalDataList.get(i).getHospital_name());
////            //markerOptions.snippet(Hos);
////            // 생성된 마커 옵션을 지도에 표시
////
////            //marker = GMap.addMarker(markerOptions);
////            //marker.showInfoWindow();
////            marker = GMap.addMarker(new MarkerOptions()
////                    .position(new LatLng(Latitude,Longtitude))
////                    .title(HospitalDataList.get(i).getHospital_name() ));
////        }
////        marker.showInfoWindow();
        // 서울광장마커

        // 회사 DB에 데이터를 가지고 있어야 된다.
//        LatLng plaza = new LatLng(37.565785, 126.978056);
//        markerOptions.position(plaza);
//        markerOptions.title("광장");
//        markerOptions.snippet("서울 광장");
//        GMap.addMarker(markerOptions);

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