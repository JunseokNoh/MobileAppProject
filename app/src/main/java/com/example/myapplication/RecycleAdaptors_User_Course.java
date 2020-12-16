package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Thread.ThreadTask;
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

class RecycleAdaptors_User_Course extends RecyclerView.Adapter<RecycleAdaptors_User_Course.CustomViewHolder> implements ItemTouchHelperListener{

    private int position;
    private ViewGroup parent;
    private ArrayList<Recommanded_course_item> RecommandedCourseDataList;
    private String ip;
    private String email;
    private MaterialTextView User_course_Text;

    private SharedPreferences sensor_status_pref;
    private SharedPreferences.Editor sensor_status_editor;

    public RecycleAdaptors_User_Course(String sensor_ip) {
        ip = sensor_ip;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
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
    public RecycleAdaptors_User_Course.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_rank_list, parent,false );
        User_course_Text = parent.findViewById(R.id.User_course_text);
        return new CustomViewHolder(view, ip, email, this.position, parent);
    }

    @Override // 실제 추가될 때
    public void onBindViewHolder(@NonNull final RecycleAdaptors_User_Course.CustomViewHolder holder, final int position) {
        Recommanded_course_item item = RecommandedCourseDataList.get(position);
        this.position = position;
        holder.setItem(item, position, this);

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

    public void setUserCourseText(MaterialTextView user_course_text) {
        User_course_Text = user_course_text;
    }

    public MaterialTextView getUser_course_Text() {
        return User_course_Text;
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        protected String ip;

        protected LinearLayout view;
        protected TextView Course_name;
        protected TextView Course_like;
        protected TextView Course_Rank;
        private String Email;
        private  RecycleAdaptors_User_Course recycleAdaptors_user_course;

        int position;
        Recommanded_course_item item;

        public CustomViewHolder(@NonNull View itemView, String sensor_ip, String email, int position, ViewGroup parent) {
            super(itemView);
            ip = sensor_ip;
            this.position = position;
            view = itemView.findViewById(R.id.Course_Rank_list_item);
            Course_name  = (TextView)itemView.findViewById(R.id.Course_Rank_name);
            Course_like = (TextView)itemView.findViewById(R.id.Course_Rank_like);
            Course_Rank = (TextView)itemView.findViewById(R.id.Course_Rank);
            Email = email;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(parent.getContext(), Recommanded_Course.class);
                    intent.putExtra("Course_number", item.getCourse_id());
                    //Toast.makeText(parent.getContext(), String.format("%s번째 입니다.",item.getCourse_id() ),Toast.LENGTH_SHORT).show();
                    view.getContext().startActivity(intent);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder msgBuilder = new AlertDialog.Builder(parent.getContext())
                            .setTitle("알림")
                            .setMessage("삭제하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    ThreadTask<Object> result = getThreadTask_Delete_Course(item.getCourse_id(), email, "/Delete_User_Course");
                                    result.execute(ip);

                                    if(result.getResult() == 1){
                                        Toast.makeText(parent.getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                        //recycleAdaptors_user_course.notifyDataSetChanged();
                                        recycleAdaptors_user_course.RecommandedCourseDataList.remove(position);
//                                        if(recycleAdaptors_user_course.RecommandedCourseDataList.size() == 0){
//                                            recycleAdaptors_user_course.getUser_course_Text().setText("생성한 코스가 없습니다.");
//                                        }
                                        recycleAdaptors_user_course.notifyItemChanged(position);

                                    }
                                    //notify();
                                    //CustomViewHolder.this.notifyAll();
                                } })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialogInterface, int i) {

                                } });
                    AlertDialog msgDlg = msgBuilder.create(); msgDlg.show();

                    return false;
                }
            });
        }

        private ThreadTask<Object> getThreadTask_Delete_Course(String Course_id, String email, String Router_name){

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
                        response_result = (Integer) responseJSON.get("key");
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

        @SuppressLint("DefaultLocale")
        public void setItem(Recommanded_course_item item, int position, RecycleAdaptors_User_Course recycleAdaptors_user_course){
            this.item = item;
            this.recycleAdaptors_user_course = recycleAdaptors_user_course;
            Course_name.setText(item.getCourse_name());
            Course_like.setText(item.getPreference());
            Course_Rank.setText(String.format("%d",position+1));

        }

    }

}
