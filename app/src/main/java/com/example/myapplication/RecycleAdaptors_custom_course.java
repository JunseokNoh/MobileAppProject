package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Thread.ThreadTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

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

import temp.Sensor_inform;

class RecycleAdaptors_custom_course extends RecyclerView.Adapter<RecycleAdaptors_custom_course.CustomViewHolder> implements ItemTouchHelperListener{

    private int position;
    private ViewGroup parent;
    ArrayList<custom_course_item> Custom_course_List = new ArrayList<>();
    private String ip;
    private String Km;
    private Spinner KmSpinner;

    custom_course_item course_item;

    private SharedPreferences sensor_status_pref;
    private SharedPreferences.Editor sensor_status_editor;
    private GoogleMap googleMap;
    private FragmentActivity activity;

    public RecycleAdaptors_custom_course(FragmentActivity activity, GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.activity = activity;
    }

    public void setKmSpinner(Spinner kmSpinner) {
        KmSpinner = kmSpinner;
    }

    public Spinner getKmSpinner() {
        return KmSpinner;
    }

    public void setKm(String km) {
        Km = km;
    }

    public String getKm() {
        return Km;
    }

    public void addItem(custom_course_item item){
        Custom_course_List.add(item);
    }

    public void setItems(ArrayList<custom_course_item> items){
        Custom_course_List = items;
    }

    public void removeItems(int position){
        Custom_course_List.remove(position);
    }
    public custom_course_item getItem(int position){
        return Custom_course_List.get(position);
    }
    public ArrayList<custom_course_item> getItems(){
        return Custom_course_List;
    }


    public void setItem(int position, custom_course_item item){
        Custom_course_List.set(position, item);
    }

    @Override
    public RecycleAdaptors_custom_course.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_course_item, parent,false );

        return new CustomViewHolder(view, ip);
    }

    @Override // 실제 추가될 때
    public void onBindViewHolder(@NonNull final RecycleAdaptors_custom_course.CustomViewHolder holder, final int position) {

        custom_course_item item = Custom_course_List.get(position);
//        sensor_status_pref = parent.getContext().getSharedPreferences("Sensor_status", Activity.MODE_PRIVATE);
//        sensor_status_editor = sensor_status_pref.edit();
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(parent.getContext(), choice_category.class);
//                holder.view.getContext().startActivity(intent);
                //choice_course_fragment.newInstance()
                String km = getKmSpinner().getSelectedItem().toString().replace("km", "");

               ((MainActivity)activity).replaceFragment(choice_course_fragment.newInstance(Integer.toString(position), " ", km));
            }
        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder msgBuilder = new AlertDialog.Builder(parent.getContext())
                        .setTitle("알림")
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialogInterface, int i)
                            {
                            Custom_course_List.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position,Custom_course_List.size());
                            } })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialogInterface, int i) {

                            } });
                AlertDialog msgDlg = msgBuilder.create(); msgDlg.show();
                return false;
            }
        });

        holder.setItem(item);

       // String sensor_status = sensor_status_pref.getString("sensor"+position, "false");

    }

    private ThreadTask<Object> getThreadTask_deleteSensor(String MAC, String Router_name){

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

                sendObject.put("wifi_mac_address", MAC);

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

    @Override
    public int getItemCount() {
        return Custom_course_List.size();
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
        protected TextView custom_courses_name;
        protected TextView custom_courses_detail;

        private SharedPreferences sensor_status_pref;
        private SharedPreferences.Editor sensor_status_editor;

        public CustomViewHolder(@NonNull View itemView, String ip) {
            super(itemView);
            this.ip = ip;
            view = itemView.findViewById(R.id.Custom_course_layout);
            custom_courses_name  = (TextView)itemView.findViewById(R.id.custom_courses_name);
            custom_courses_detail  = (TextView)itemView.findViewById(R.id.custom_courses_detail);
        }

        public void setItem(custom_course_item item){
            custom_courses_name.setText(item.getCustom_course_name());
            custom_courses_detail.setText(item.getCustom_course_detail());
        }

        private ThreadTask<Object> getThreadTask_macCheck(String MAC, String Router_name){

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

                    sendObject.put("wifi_mac_address", MAC);

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

}
