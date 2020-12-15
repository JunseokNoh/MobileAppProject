package com.example.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Thread.ThreadTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Categories_coures_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Categories_coures_fragment extends Fragment  implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    private MaterialToolbar toolbar;
    private ActionBar actionBar;
    private MaterialTextView toolbartext;
    private MaterialTextView PhoneCall;
    private Button Confirm_Button;

    private GoogleMap GMap;

    //private ArrayList<Recommanded_course_item> RecommandedCourseDataList = new ArrayList<>();
    private RecycleAdaptors_categories_Curse_rank recycleAdaptors;
    private RecyclerView Recommanded_recycler_view;
    private TextView Phone_call;

    private LinearLayoutManager layoutManager;

    private JSONArray Course_total_array;
    private JSONArray Course_array;
    private String Starting_latitude;
    private String Starting_longitude;

    private String ip;
    private String input;

    private ArrayList<course_item> course_list = new ArrayList<course_item>();

    private MapView mapView;
    private List<Address> list = null;

    private SharedPreferences location_information_pref;

    public Categories_coures_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Categories_coures_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Categories_coures_fragment newInstance(String param1, String param2) {
        Categories_coures_fragment fragment = new Categories_coures_fragment();
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
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_categories_coures_fragment, container, false);
        ip = getString(R.string.server_ip);

        location_information_pref = getContext().getSharedPreferences("location_information", Activity.MODE_PRIVATE);

        //location_infromation_editor = location_information_pref.edit();

        // Address addr = list.get(0);
        Starting_latitude = location_information_pref.getString("current_latitude", "");
        Starting_longitude = location_information_pref.getString("current_longitude", "");

        Recommanded_recycler_view = v.findViewById(R.id.recommended_courses_Recycler_view);
        layoutManager =  new LinearLayoutManager(getContext());
        if(layoutManager != null){
            Recommanded_recycler_view.setLayoutManager(layoutManager);
        }
        else{
            Log.e("SensorFragment", "Error");
        }
        String kind = "";
        if(mParam2.equals("식당")){
            kind = "음식";
        }
        else if(mParam2.equals("카페")){
            kind = "카페";
        }
        else if(mParam2.equals("놀거리")){
            kind = "놀거리";
        }
        else if(mParam2.equals("볼거리")){
            kind = "볼거리" ;
        }
        ThreadTask<Object> result = getThreadTask_getMAPInform(kind,Starting_latitude, Starting_longitude, "/map_category_information");
        result.execute(ip);

        String input ="{"+
                "Course:"  +"[{\"Place_name\" : \"맥시멈짐\",\"Place_detail\" : \"place detail1\", \"latitude\" : \"35.88214144011649\", \"longitude\" : \"128.60999144632964\"},{\"Place_name\" : \"place2\",\"Place_detail\" : \"place detail2\", \"latitude\" : \"35.88492137332584\", \"longitude\" : \"128.60971990159663\"},{\"Place_name\" : \"place3\",\"Place_detail\" : \"place detail3\", \"latitude\" : \"35.88253371936135\", \"longitude\" : \"128.60998957938452\"}]"
                +"}";


        JSONObject input_object = null;
        try {
            input_object = new JSONObject(input);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            for(int j = 0 ; j < Course_total_array.length() ; j ++){
                JSONObject temp_object = Course_total_array.getJSONObject(j);
                String Place_name = temp_object.getString("Name");
                String Place_detail = temp_object.getString("Address");
                String Latitude = temp_object.getString("Latitude");
                String Longitude = temp_object.getString("Longitude");

                if(j == 0){
                    Starting_latitude = Latitude;
                    Starting_longitude = Longitude;
                }

                course_list.add(new course_item(Place_name, Place_detail, Latitude, Longitude));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        mapView = (MapView)v.findViewById(R.id.map);
        // 맵이 실행되면 onMapReady 실행
        mapView.getMapAsync(this);


        Confirm_Button = v.findViewById(R.id.recommended_courses_confirm);
        Confirm_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = recycleAdaptors.getSelectedPosition();
                if(position != -1){
                    course_item items = recycleAdaptors.getItem(position);
                    Bundle args = new Bundle();
                    args.putString("destination", items.getPlace_name());
                    args.putString("address", items.getAddress());
                    args.putString("latitude", items.getLatitude());
                    args.putString("longitude", items.getLongitude());
                    args.putString("index", mParam1);

                    ((MainActivity)getActivity()).onMoveCustomcourse(args);
                    Toast.makeText(getContext(), "선택되었습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "목적지를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                /**
                 * 목적지 이름
                 * 주소
                 * 위도
                 * 경도 전달
                 * */
            }
        });
        return v;


    }


    public com.google.android.gms.maps.GoogleMap getGoogleMap() {
        return GMap;
    }

    public void setGoogleMap(com.google.android.gms.maps.GoogleMap googleMap) {
        GMap = googleMap;
    }
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(mapView != null)
        {
            mapView.onCreate(savedInstanceState);
        }
    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        setGoogleMap(googleMap);
        GMap.setOnMarkerClickListener(this);

        /**
         * 센서 위치 시작 지점 받아오기
         * */
        LatLng Starting_Point = new LatLng(Double.parseDouble(Starting_latitude), Double.parseDouble(Starting_longitude)); // 스타팅 지점
        getGoogleMap().moveCamera(CameraUpdateFactory.newLatLngZoom(Starting_Point, 14));

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

        recycleAdaptors = new RecycleAdaptors_categories_Curse_rank(ip, getGoogleMap());
        recycleAdaptors.setItems(course_list);
        Recommanded_recycler_view.setAdapter(recycleAdaptors);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().finish();
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

    private ThreadTask<Object> getThreadTask_getMAPInform(String kind, String starting_latitude, String starting_longitude, String Router_name){

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

                sendObject.put("kind", kind);
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

    //                this.response_result = (Integer) responseJSON.get("key");
                    //this.error_code = (String) responseJSON.get("err_code");

                   // JSONObject test = (JSONObject) responseJSON.get("data");
                    Course_total_array = (JSONArray) responseJSON.get("data");
//                    Name = (String) test.get("inst_name");
//                    Phonenumber = (String) test.get("phone_number");
//                    Address = (String) test.get("inst_address");

                   // Log.e("twtwtwsdfw", String.format("번호 : %s, 주소 :", test));
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