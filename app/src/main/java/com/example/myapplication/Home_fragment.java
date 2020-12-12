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

import com.dinuscxj.progressbar.CircleProgressBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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

    public Home_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home_fragment.
     */
    // TODO: Rename and change types and number of parameters
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

        ((MainActivity)getActivity()).setToolbar();
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
        recyclerView = (RecyclerView)v.findViewById(R.id.Recycler_view);

        layoutManager =  new LinearLayoutManager(getActivity());
        if(layoutManager != null){
            recyclerView.setLayoutManager(layoutManager);
        }
        else{
            Log.e("SensorFragment", "Error");
        }

        ArrayList<Course_rank> courseRanksList = new ArrayList<Course_rank>();
        courseRanksList.add(new Course_rank("수성구 데이트 코스"));
        courseRanksList.add(new Course_rank("동성로 데이트 코스"));
        courseRanksList.add(new Course_rank("앞산 데이트 코스"));

        recycleAdaptors = new RecycleAdaptors_Curse_rank(ip);

        recycleAdaptors.setItems(courseRanksList);
        recyclerView.setAdapter(recycleAdaptors);

        return v;
    }


}