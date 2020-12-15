package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Thread.ThreadTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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

class RecycleAdaptors_categories_Curse_rank extends RecyclerView.Adapter<RecycleAdaptors_categories_Curse_rank.CustomViewHolder> implements ItemTouchHelperListener{
    /**
     * 추천 코스보기 리싸이클러뷰
     * */
    private int position;
    private ViewGroup parent;
    private ArrayList<course_item> CategoriesCourseDataList = new ArrayList<>();
    private String ip;
    private GoogleMap googleMap;
    private View marker_root_view;
    private TextView tv_marker;
    int selectedPosition = -1;
    int oldPosition = -1;

    private SharedPreferences sensor_status_pref;
    private SharedPreferences.Editor sensor_status_editor;

    public RecycleAdaptors_categories_Curse_rank(String ip, GoogleMap googleMap) {
        ip = ip;
        this.googleMap = googleMap;
    }

    public void addItem(course_item item){
        CategoriesCourseDataList.add(item);
    }

    public void setItems(ArrayList<course_item> items){
        CategoriesCourseDataList = items;
    }

    public void removeItems(int position){
        CategoriesCourseDataList.remove(position);
    }
    public course_item getItem(int position){
        return CategoriesCourseDataList.get(position);
    }
    public int getSelectedPosition(){
        return selectedPosition;
    }
    public ArrayList<course_item> getItems(){
        return CategoriesCourseDataList;
    }


    public void setItem(int position, course_item item){
        CategoriesCourseDataList.set(position, item);
    }

    @Override
    public RecycleAdaptors_categories_Curse_rank.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_items, parent,false );

        return new CustomViewHolder(view, ip);
    }

    @Override // 실제 추가될 때
    public void onBindViewHolder(@NonNull final RecycleAdaptors_categories_Curse_rank.CustomViewHolder holder, final int position) {

        course_item item = CategoriesCourseDataList.get(position);
        //ArrayList<course_item> course_list = item.getCourse_list();
//        sensor_status_pref = parent.getContext().getSharedPreferences("Sensor_status", Activity.MODE_PRIVATE);
//        sensor_status_editor = sensor_status_pref.edit();
        LinearLayout layout = holder.view.findViewById(R.id.categories_courses_item);
        //holder.view.setSelected(true);

        if(selectedPosition == position){ //선택되면
            layout.setBackground(parent.getContext().getDrawable(R.drawable.edge21));
        }
        else{//선택이 안되면 나머지는 다 흰색으로 바꿔줌
            layout.setBackground(parent.getContext().getDrawable(R.drawable.edge2));
            holder.view.setSelected(false);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedPosition = position;
                holder.view.setSelected(true);
                notifyItemChanged(selectedPosition);
                notifyDataSetChanged();
                double Latitude;
                double Longtitude;

                setCustomMarkerView();

                ArrayList<LatLng> latngs = new ArrayList<>();
                MarkerOptions markerOptions = new MarkerOptions();
                PolylineOptions polylineOptions = new PolylineOptions().clickable(true);
                Marker marker = null;
                googleMap.clear();
                Latitude = Double.parseDouble(item.getLatitude());
                Longtitude = Double.parseDouble(item.getLongitude());

                //latngs.add(new LatLng(Latitude, Longtitude));
                //polylineOptions.add(new LatLng(Latitude,Longtitude));
                LatLng Starting_Point = new LatLng(Latitude, Longtitude); // 스타팅 지점
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Starting_Point, 15));

                //tv_marker.setText(item.getPlace_name());
                markerOptions.position(new LatLng(Latitude,Longtitude));
                markerOptions.title(item.getPlace_name());
                //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(parent.getContext(), marker_root_view)));
                //markerOptions.snippet("주소 넣을 자리");
                // 생성된 마커 옵션을 지도에 표시

                marker = googleMap.addMarker(markerOptions);
                marker.showInfoWindow();
            }

        });
        holder.setItem(item);

    }
    private void setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.marker_hydrogenstation, null);
        tv_marker = (TextView) marker_root_view.findViewById(R.id.tv_marker);
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
        return CategoriesCourseDataList.size();
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
        protected TextView Course_detail;

        public CustomViewHolder(@NonNull View itemView, String sensor_ip) {
            super(itemView);
            view = itemView.findViewById(R.id.categories_courses_item);
            ip = sensor_ip;
            Course_name  = (TextView)itemView.findViewById(R.id.categories_courses_name);
            Course_detail = (TextView)itemView.findViewById(R.id.categories_courses_destination);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        public void setItem(course_item item){
            Course_name.setText(item.getPlace_name());
            Course_detail.setText(item.getAddress());
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
