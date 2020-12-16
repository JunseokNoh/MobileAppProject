package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link choice_course_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class choice_course_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String Km;

    private ImageButton Restaurant;
    private ImageButton Coffee;
    private ImageButton Play;
    private ImageButton LookThings;

    public choice_course_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment choice_course_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static choice_course_fragment newInstance(String param1, String param2, String param3) {
        choice_course_fragment fragment = new choice_course_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            Km = getArguments().getString(ARG_PARAM3);
            //Toast.makeText(getContext(), String.format("%s번째 선택됨", mParam1), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_choice_course_fragment, container, false);

        Restaurant = v.findViewById(R.id.restaurant);
        Coffee = v.findViewById(R.id.coffee);
        Play = v.findViewById(R.id.play);
        LookThings = v.findViewById(R.id.lookThings);

        Restaurant.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), Categories_coures.class);
//                intent.getStringExtra("restaurant");
//                startActivity(intent);
                ((MainActivity)getActivity()).replaceFragment(Categories_coures_fragment.newInstance(mParam1, "식당", Km));
            }
        });

        Coffee.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), Categories_coures.class);
//                intent.getStringExtra("coffee");
//                startActivity(intent);
                ((MainActivity)getActivity()).replaceFragment(Categories_coures_fragment.newInstance(mParam1, "카페", Km));
            }
        });

        Play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), Categories_coures.class);
//                intent.getStringExtra("play");
//                startActivity(intent);
                ((MainActivity)getActivity()).replaceFragment(Categories_coures_fragment.newInstance(mParam1, "놀거리", Km));
            }
        });

        LookThings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), Categories_coures.class);
//                intent.getStringExtra("lookThings");
//                startActivity(intent);
                ((MainActivity)getActivity()).replaceFragment(Categories_coures_fragment.newInstance(mParam1, "볼거리", Km));
            }
        });
        return v;

    }
}