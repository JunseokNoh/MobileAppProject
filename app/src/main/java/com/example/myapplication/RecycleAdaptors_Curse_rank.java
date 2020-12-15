package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Thread.ThreadTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import temp.Sensor_inform;

class RecycleAdaptors_Curse_rank extends RecyclerView.Adapter<RecycleAdaptors_Curse_rank.CustomViewHolder> implements ItemTouchHelperListener{

    private int position;
    private ViewGroup parent;
    private ArrayList<Recommanded_course_item> RecommandedCourseDataList;
    private String ip;

    private SharedPreferences sensor_status_pref;
    private SharedPreferences.Editor sensor_status_editor;

    public RecycleAdaptors_Curse_rank(String sensor_ip) {
        ip = sensor_ip;
    }

    public void addItem(Recommanded_course_item item){
        RecommandedCourseDataList.add(item);
    }

    public void setItems(ArrayList<Recommanded_course_item> items){
        RecommandedCourseDataList = items;
    }

    public void removeItems(int position){
        RecommandedCourseDataList.remove(position);
    }

    public Recommanded_course_item getItem(int position){
        return RecommandedCourseDataList.get(position);
    }
    public ArrayList<Recommanded_course_item> getItems(){
        return RecommandedCourseDataList;
    }


    public void setItem(int position, Recommanded_course_item item){
        RecommandedCourseDataList.set(position, item);
    }

    @Override
    public RecycleAdaptors_Curse_rank.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_rank_list, parent,false );
        return new CustomViewHolder(view, ip, this.position, parent);
    }

    @Override // 실제 추가될 때
    public void onBindViewHolder(@NonNull final RecycleAdaptors_Curse_rank.CustomViewHolder holder, final int position) {
        Recommanded_course_item item = RecommandedCourseDataList.get(position);
        this.position = position;
        holder.setItem(item, position);
    }

    @Override
    public int getItemCount() {
        return RecommandedCourseDataList.size();
    }

    @Override
    public boolean onItemMove(int from_position, int to_position) {
        return true;
    }
    @Override
    public void onItemSwipe(int position) {
    }

    @Override
    public void onLeftClick(int position, RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public void onRightClick(int position, RecyclerView.ViewHolder viewHolder) {
    }


    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        protected String ip;

        protected LinearLayout view;
        protected TextView Course_name;
        protected TextView Course_like;
        protected TextView Course_Rank;

        int position;
        Recommanded_course_item item;

        public CustomViewHolder(@NonNull View itemView, String sensor_ip, int position, ViewGroup parent) {
            super(itemView);
            ip = sensor_ip;
            this.position = position;
            view = itemView.findViewById(R.id.Course_Rank_list_item);
            Course_name  = (TextView)itemView.findViewById(R.id.Course_Rank_name);
            Course_like = (TextView)itemView.findViewById(R.id.Course_Rank_like);
            Course_Rank = (TextView)itemView.findViewById(R.id.Course_Rank);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(parent.getContext(), Recommanded_Course.class);
                    intent.putExtra("Course_number", item.getCourse_id());
                    //Toast.makeText(parent.getContext(), String.format("%s번째 입니다.",item.getCourse_id() ),Toast.LENGTH_SHORT).show();
                    view.getContext().startActivity(intent);
                }
            });
        }

        @SuppressLint("DefaultLocale")
        public void setItem(Recommanded_course_item item, int position){
            this.item = item;
            Course_name.setText(item.getCourse_name());
            Course_like.setText(item.getPreference());
            Course_Rank.setText(String.format("%d",position+1));
        }

    }

}
