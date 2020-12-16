package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Thread.ThreadTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
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
import java.util.logging.Handler;
import java.util.logging.LogRecord;

class RecycleAdaptors_recommended_Curse_rank extends RecyclerView.Adapter<RecycleAdaptors_recommended_Curse_rank.CustomViewHolder> implements ItemTouchHelperListener{
    /**
     * 추천 코스보기 리싸이클러뷰
     * */
    private int position;
    private ViewGroup parent;
    private ArrayList<Recommanded_course_item> RecommandedCourseDataList = new ArrayList<>();
    private String ip;
    private GoogleMap googleMap;
    private View marker_root_view;
    private TextView tv_marker;
    private String Course_number;
    private Recommanded_course_item item;
    int selectedPosition = -1;
    int scrollPosition;
    private SharedPreferences sensor_status_pref;
    private SharedPreferences.Editor sensor_status_editor;

    public RecycleAdaptors_recommended_Curse_rank(String ip, GoogleMap googleMap) {
        this.ip = ip;
        this.googleMap = googleMap;
    }

    public void addItem(Recommanded_course_item item){
        RecommandedCourseDataList.add(item);
    }

    public void setItems(ArrayList<Recommanded_course_item> items){
        RecommandedCourseDataList = items;
    }

    public void setScrollPosition(int scrollPosition) {
        this.scrollPosition = scrollPosition;
    }

    public int getScrollPosition() {
        return scrollPosition;
    }

    public void setCourse_number(String course_number) {
        Course_number = course_number;
    }

    public String getCourse_number() {
        return Course_number;
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
    public RecycleAdaptors_recommended_Curse_rank.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_items, parent,false );

        return new CustomViewHolder(view, ip, parent);
    }

    @Override // 실제 추가될 때
    public void onBindViewHolder(@NonNull final RecycleAdaptors_recommended_Curse_rank.CustomViewHolder holder, final int position) {

        item = RecommandedCourseDataList.get(position);
        ArrayList<course_item> course_list = item.getCourse_list();
//        sensor_status_pref = parent.getContext().getSharedPreferences("Sensor_status", Activity.MODE_PRIVATE);
//        sensor_status_editor = sensor_status_pref.edit();
        LinearLayout layout = holder.view.findViewById(R.id.recommended_courses_item);

        if(Course_number == null){
            if(selectedPosition == position && item != null){ //선택되면
                layout.setBackground(parent.getContext().getDrawable(R.drawable.edge21));
            }
            else{//선택이 안되면 나머지는 다 흰색으로 바꿔줌
                layout.setBackground(parent.getContext().getDrawable(R.drawable.edge24));
                holder.view.setSelected(false);
            }
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Course_number == null){
                    selectedPosition = position;
                    holder.view.setSelected(true);

                   // notifyItemChanged(selectedPosition);
                    notifyDataSetChanged();
                }

                double Latitude;
                double Longtitude;

                setCustomMarkerView();

                ArrayList<LatLng> latngs = new ArrayList<>();
                MarkerOptions markerOptions = new MarkerOptions();
                PolylineOptions polylineOptions = new PolylineOptions().clickable(true);
                Marker marker = null;
                googleMap.clear();
                for(int i = 0 ; i < course_list.size() ; i++){

                    Latitude = Double.parseDouble(course_list.get(i).getLatitude());
                    Longtitude = Double.parseDouble(course_list.get(i).getLongitude());
                    latngs.add(new LatLng(Latitude, Longtitude));
                    polylineOptions.add(new LatLng(Latitude,Longtitude));
                    LatLng Starting_Point = new LatLng(Latitude, Longtitude); // 스타팅 지점
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Starting_Point, 14));

                    tv_marker.setText(course_list.get(i).getPlace_name());
                    markerOptions.position(new LatLng(Latitude,Longtitude));
                    markerOptions.title(course_list.get(i).getPlace_name());
                    //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(parent.getContext(), marker_root_view)));
                    markerOptions.snippet(course_list.get(i).getAddress());
                    // 생성된 마커 옵션을 지도에 표시

                    marker = googleMap.addMarker(markerOptions);
                    //marker.showInfoWindow();
                }
                Polyline polyline1 = googleMap.addPolyline(polylineOptions);
                polyline1.setColor(Color.RED);
                polyline1.setTag("A");
                //marker.showInfoWindow();
            }
        });

        if(Course_number != null && item.getCourse_id().equals(Course_number)){
            holder.view.setSelected(true);
            if(holder.view.performClick()){
                layout.setBackground(parent.getContext().getDrawable(R.drawable.edge21));
                Course_number = null;
                //Toast.makeText(parent.getContext(), "선택됐다", Toast.LENGTH_SHORT).show();
            }
        }

        holder.setItem(item);
        holder.setLikeButton(parent);
    }

    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private void setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.marker_hydrogenstation, null);
        tv_marker = (TextView) marker_root_view.findViewById(R.id.tv_marker);
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
        protected TextView Course_detail;
        protected ToggleButton like_button;

        private Recommanded_course_item item;

        private SharedPreferences Recommend_information_pref;
        private SharedPreferences.Editor Recommend_infromation_editor;

        public CustomViewHolder(@NonNull View itemView, String ip, ViewGroup parent) {
            super(itemView);
            view = itemView.findViewById(R.id.recommended_courses_item);
            this.ip = ip;
            Course_name  = (TextView)itemView.findViewById(R.id.recommended_courses_name);
            Course_detail = (TextView)itemView.findViewById(R.id.recommended_courses_destination);

        }
        public void setLikeButton( ViewGroup parent){
            like_button = itemView.findViewById(R.id.like_button);
            Recommend_information_pref = parent.getContext().getSharedPreferences("Toggle_information", Activity.MODE_PRIVATE);
            Recommend_infromation_editor = Recommend_information_pref.edit();

            String Toggle_state = Recommend_information_pref.getString("Toggle" + item.getPreference(), "false");
            if(Toggle_state.equals("true")){
                like_button.setChecked(true);
                like_button.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.ic_heart));
            }

            like_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){//좋아요 누른거
                        String Course_id = item.getCourse_id();
                        ThreadTask<Object> result = getThreadTask_getMAPInform("on",Course_id, "/like_button");
                        result.execute(ip);
                        item.setPreference(Integer.toString( (Integer.parseInt(item.getPreference()) + 1)));
                        Recommend_infromation_editor.putString("Toggle" + item.getPreference() , "true");
                        Recommend_infromation_editor.commit();
                        like_button.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.ic_heart));
                        Course_detail.setText("추천수 : " +item.getPreference());
                        Log.e("추천코스 좋아요 눌러짐 ",Course_id);
                    }
                    else{//안누른거
                        String Course_id = item.getCourse_id();
                        ThreadTask<Object> result = getThreadTask_getMAPInform("off", Course_id, "/like_button");
                        result.execute(ip);
                        Recommend_infromation_editor.putString("Toggle" + item.getPreference() , "false");
                        Recommend_infromation_editor.commit();

                        item.setPreference(Integer.toString( (Integer.parseInt(item.getPreference()) - 1)));
                        Course_detail.setText("추천수 : " +item.getPreference());
                        like_button.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.ic_like));
                        Log.e("추천코스 좋아요 꺼짐 ",Course_id);
                    }
                }
            });
        }


        public void setItem(Recommanded_course_item item){
            this.item = item;
            Course_name.setText(item.getCourse_name());
            String Course_detail_temp ="";
            for(course_item course : item.getCourse_list()){
                Course_detail_temp += course.getPlace_name() + " ";
            }
            Course_detail.setText("추천수 : " + item.getPreference());
        }

        private ThreadTask<Object> getThreadTask_getMAPInform(String Button_state, String Course_id, String Router_name){

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
                    sendObject.put("Course_number", Course_id);
                    sendObject.put("Button_state", Button_state);

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
                        response_result = (Integer) responseJSON.get("key");

                        //Log.e("twtwtwsdfw", String.format("%s", Course_total_array));
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
