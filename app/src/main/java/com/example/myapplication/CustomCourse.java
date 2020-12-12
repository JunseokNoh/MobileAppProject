package com.example.myapplication;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomCourse#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomCourse extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private MaterialButton Create_destination_button;
    private MaterialButton Custom_courses_confirm_button;

    private RecycleAdaptors_custom_course recycleAdaptors;
    private RecyclerView Custom_Course_recycler_view;
    private RecyclerView.LayoutManager layoutManager;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GoogleMap googleMap;
    private MapView mapView;
    private List<Address> list = null;

    private String Custom_course_name;
    private String Custom_course_detail;
    private String Latitude;
    private String Longitude;

    private double Starting_latitude;
    private double Starting_longitude;

    private TextView tv_marker;

    private ArrayList<custom_course_item> custom_course_items = new ArrayList<>();

    public CustomCourse() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomCourse.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomCourse newInstance(String param1, String param2) {
        CustomCourse fragment = new CustomCourse();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity)getActivity();
        Bundle mbundle = activity.mbundle;
        if(mbundle != null){
            Custom_course_name = mbundle.getString("destination");
            Custom_course_detail = mbundle.getString("address");
            Latitude = mbundle.getString("latitude");
            Longitude = mbundle.getString("longitude");
            Log.e("CustomCurse Test", String.format("%s %s %s %s", Custom_course_name, Custom_course_detail, Latitude, Longitude));
            int index = Integer.parseInt(mbundle.getString("index"));
            custom_course_items.set(index, new custom_course_item(Custom_course_name,Custom_course_detail, Latitude, Longitude ));
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_custom_course, container, false);

        ((MainActivity)getActivity()).resetToolbar();

        final Geocoder geocoder = new Geocoder(getContext());
        try {
            list = geocoder.getFromLocationName(((MainActivity)getActivity()).getAddress(), 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address addr = list.get(0);
        Starting_latitude = addr.getLatitude();
        Starting_longitude = addr.getLongitude();

        Create_destination_button = v.findViewById(R.id.Create_new_destination);
        Custom_courses_confirm_button = v.findViewById(R.id.Custom_courses_confirm);

        Custom_Course_recycler_view = v.findViewById(R.id.custom_course_Recycler_view);
        layoutManager =  new LinearLayoutManager(getActivity());
        if(layoutManager != null){
            Custom_Course_recycler_view.setLayoutManager(layoutManager);
        }

        Create_destination_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Custom_course_name = "목적지를 선택해주세요.";
                String Custom_course_detail = " ";
                String Latitude = "33";
                String Longitude = "158";
                custom_course_items.add(new custom_course_item(Custom_course_name, Custom_course_detail, Latitude, Longitude));
                recycleAdaptors.setItems(custom_course_items);
                Custom_Course_recycler_view.setAdapter(recycleAdaptors);
            }
        });
        mapView = (MapView)v.findViewById(R.id.map);
        // 맵이 실행되면 onMapReady 실행
        mapView.getMapAsync(this);


        Custom_courses_confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 저장버튼
                 * */

            }
        });
        return v;
    }

    public com.google.android.gms.maps.GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(com.google.android.gms.maps.GoogleMap googleMap) {
        this.googleMap = googleMap;
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
        //액티비티가 처음 생성될 때 실행되는 함수
        if(mapView != null)
        {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setGoogleMap(googleMap);
        googleMap.setOnMarkerClickListener(this);

        ArrayList<LatLng> latngs = new ArrayList<>();
        MarkerOptions markerOptions = new MarkerOptions();
        PolylineOptions polylineOptions = new PolylineOptions().clickable(true);
        Marker marker = null;

        LatLng starting_point = new LatLng(Starting_latitude, Starting_longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(starting_point, 14);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(starting_point));
        googleMap.animateCamera(cameraUpdate);

        if(custom_course_items.size() > 0){
            for(int i = 0 ; i < custom_course_items.size(); i ++){
                //googleMap.clear();
                Log.e("CustomCurse Test22", String.format("%s %s %s %s", custom_course_items.get(i).getCustom_course_name(),
                        custom_course_items.get(i).getCustom_course_detail(),
                        custom_course_items.get(i).getLatitude()
                        , custom_course_items.get(i).getLongitude()));

                double temp_latitude = Double.parseDouble(custom_course_items.get(i).getLatitude());
                double temp_longitude = Double.parseDouble(custom_course_items.get(i).getLongitude());

                polylineOptions.add(new LatLng(temp_latitude,temp_longitude));
                LatLng Starting_Point = new LatLng(temp_latitude, temp_longitude); // 스타팅 지점
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Starting_Point, 14));

                markerOptions.position(new LatLng(temp_latitude,temp_longitude));
                markerOptions.title(custom_course_items.get(i).getCustom_course_name());

                marker = googleMap.addMarker(markerOptions);
                marker.showInfoWindow();
            }
            Polyline polyline= googleMap.addPolyline(polylineOptions);
            polyline.setColor(Color.RED);
            polyline.setTag("A");
        }


        recycleAdaptors = new RecycleAdaptors_custom_course((MainActivity)getActivity(), getGoogleMap());
        recycleAdaptors.setItems(custom_course_items);
        Custom_Course_recycler_view.setAdapter(recycleAdaptors);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}