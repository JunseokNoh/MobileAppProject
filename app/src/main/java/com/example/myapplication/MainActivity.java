package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;

import android.view.View;
import android.widget.Toast;

import com.example.myapplication.Thread.ThreadTask;
import com.example.myapplication.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.nhn.android.naverlogin.OAuthLogin;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity<pirvate> extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentCallback{

    private static final String CHANNEL_ID = "1000" ;
    private int i = 0;

    private static final int SEARCH_ADDRESS_ACTIVITY = 10001;

    private Home_fragment home_fragment;
    private CustomCourse customCourse_fragment;
    private Mypage_fragment mypage_fragment;

    private String ip;
    DrawerLayout drawer;

    private MaterialToolbar toolbar;
    private ActionBar actionBar;
    private MaterialTextView toolbartext;

    private SharedPreferences login_information_pref;
    private String Email;
    private String Address;

    private SharedPreferences location_information_pref;
    private SharedPreferences.Editor location_infromation_editor;

    Bundle mbundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Utils.setStatusBarColor(this, Utils.StatusBarcolorType.BLACK_STATUS_BAR);
        home_fragment = new Home_fragment();
        customCourse_fragment = new CustomCourse();
        mypage_fragment = new Mypage_fragment();
        //hospital_fragment = new Hospital_fragment();

        login_information_pref = getSharedPreferences("login_information", Context.MODE_PRIVATE);
        Email = login_information_pref.getString("email", "");

        location_information_pref = getSharedPreferences("location_information", Activity.MODE_PRIVATE);
        //location_infromation_editor = location_information_pref.edit();
        Address = location_information_pref.getString("address", "주소를 선택해주세요.");
        if(Address.equals("주소를 선택해주세요.")){
            showDialog();
        }

        ip = getString(R.string.server_ip);

        /*홈 fragment로 내용 채워줌*/
        getSupportFragmentManager().beginTransaction().replace(R.id.container, home_fragment).commit();

        toolbar = (MaterialToolbar)findViewById(R.id.MainActiviy_toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), DaumWebViewActivity.class);
                startActivityForResult(intent, SEARCH_ADDRESS_ACTIVITY);
            }
        });

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        toolbartext = (MaterialTextView)findViewById(R.id.toolbar_textview);
        toolbartext.setText(Address);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container , home_fragment).commit();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Address = location_information_pref.getString("address", "주소를 선택해주세요.");
                        switch (item.getItemId()){
                            case R.id.tab1:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, home_fragment).commit();
                                toolbartext.setText(Address);
                                return true;
                            case R.id.tab2 :
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, customCourse_fragment).commit();
                                toolbartext.setText(Address);
                                return true;
                            case R.id.tab3 :
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, mypage_fragment).commit();
                                toolbartext.setText(Address);
                                return true;
                            case R.id.tab4 :
                                //Intent intent = new Intent(MainActivity.this, NearHospital.class);
                                //startActivity(intent);
                                return true;
                        }
                        return false;
                    }
                }
        );

        //getHashKey(); // fire base 해쉬 값 받아오기


        //createNotificationChannel();

        FirebaseMessaging.getInstance().subscribeToTopic("falldown");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken(); //토큰 값이 바뀌면 new token call back
                Log.e("Fire base Token", newToken.toString());
                System.out.println("Fire base Token :" + newToken);
                //new JSONTask(newToken).execute("http://10.0.2.2:3000/post");//AsyncTask 시작시킴
                makeThread(newToken);
                int response = send_token_response(Email, newToken);

                if(response == 1){
                    Log.e("토큰 전송 : ", "이메일 형식 에러");
                }
                else if(response == 2){
                    Log.e("토큰 전송 : ", "성공");
                }
                else if(response == 3){
                    Log.e("토큰 전송 : ", "실패3");
                }
                else if(response == 4){
                    Log.e("토큰 전송 : ", "실패4");
                }
                else if(response == 0){
                    Log.e("토큰 전송 : ", "시스템에러");
                }

            }

        });

    }

    String getAddress(){
        return Address;
    }

    void showDialog() {
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("알림")
                .setMessage("주소가 설정되어있지 않습니다.\n검색할 주소를 먼저 설정해주세요.")
                .setPositiveButton("주소 설정하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplication(), DaumWebViewActivity.class);
                        startActivityForResult(intent, SEARCH_ADDRESS_ACTIVITY);
                    }
                });
//                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                    @Override public void onClick(DialogInterface dialogInterface, int i) {
//
//                    } });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }
    public void setToolbar(){
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), DaumWebViewActivity.class);
                startActivityForResult(intent, SEARCH_ADDRESS_ACTIVITY);
            }
        });
    }
    public void resetToolbar(){
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).commit();
    }

    public void onMoveCustomcourse(Bundle args){
        mbundle = args;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, customCourse_fragment).commit();
    }
    private int send_token_response(String request_email, String token){

        ThreadTask<Object> result = new ThreadTask<Object>() {

            String Request_email = request_email;
            String Token = token;

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

                URL url = new URL(urls[0] +"/firebase_token_save");
               // URL url = new URL(getString(R.string.test_FCM_ip) +"/firebase_token_save");
                con = (HttpURLConnection) url.openConnection();

                sendObject.put("email_address",Request_email);
                sendObject.put("fcm_token",Token);

                Log.e("토큰 전송", "토큰 전송 합니당.");
                con.setRequestMethod("POST");//POST방식으로 보냄
                con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                con.setRequestProperty("Accept", "application/json");//서버에 response 데이터를 html로 받음
                con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미

                OutputStream outStream = con.getOutputStream();
                outStream.write(sendObject.toString().getBytes());
                outStream.flush();
                Log.e("토큰 전송", "토큰 전송 합니당.");
                int responseCode = con.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    InputStream stream = con.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData = null;
                    int nLength = 0;

                    while ((nLength = stream.read(byteBuffer, 0, byteBuffer.length)) != -1){
                        baos.write(byteBuffer, 0, nLength);
                    }
                    byteData = baos.toByteArray();

                    String response = new String(byteData);

                    JSONObject responseJSON = new JSONObject(response);
                    this.response_result = (Integer) responseJSON.get("result");
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

        result.execute(ip);
        return result.getResult();
    }

    public void makeThread(Object newToken){
        ThreadTask<Object> result = new ThreadTask<Object>() {
            Object NewToken = newToken;
            @Override
            protected void onPreExecute() {// excute 전에

            }

            @Override
            protected void doInBackground(String... urls) {//background로 돌아갈것
                try {
                    //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                    JSONObject jsonObject = new JSONObject();

                    Object jsonStr = "tlqkftlqkf";

                    Log.e("jsonTask", "실행됐다4");
                    //jsonObject.accumulate("name", "yun");
                    HttpURLConnection con = null;
                    BufferedReader reader = null;

                    jsonObject.accumulate("token", NewToken);

                    try{
                        //URL url = new URL("http://10.0.2.2:3000/post");

                        URL url = new URL(urls[0]+"/post");
                        Log.e("Mainactivity", urls[0]+"/post");
                        con = (HttpURLConnection) url.openConnection();

                        con.setRequestMethod("POST");//POST방식으로 보냄
                        con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                        con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송

                        con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                        con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                        con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미

                        con.setConnectTimeout(15000);
                        con.connect();
                        //서버로 보내기위해서 스트림 만듬

                        OutputStream outStream = con.getOutputStream();
                        //버퍼를 생성하고 넣음
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                        writer.write(jsonObject.toString());
                        writer.flush();
                        writer.close();//버퍼를 받아줌

                        //서버로 부터 데이터를 받음
                        InputStream stream = con.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(stream));
                        StringBuilder buffer = new StringBuilder();
                        String line = "";
                        while((line = reader.readLine()) != null){
                            buffer.append(line);
                        }

                        //return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                    } catch (MalformedURLException e){
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if(con != null){
                            con.disconnect();
                        }
                        try {
                            if(reader != null){
                                reader.close();//버퍼를 닫아줌
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();//
                }
            }

            @Override
            protected void onPostExecute() {

            }

            @Override
            public int getResult() {
                return 0;
            }

            @Override
            public String getErrorCode() {
                return null;
            }

        };
        result.execute(ip);

    }


    private long time = 0;
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - time >= 2000){
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        }
        else if(System.currentTimeMillis() - time < 2000){
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu1) {
            Toast.makeText(this, "첫번째 메뉴 선택됨.", Toast.LENGTH_LONG).show();
            onFragmentSelected(0, null);
        } else if (id == R.id.menu2) {
            Toast.makeText(this, "두번째 메뉴 선택됨.", Toast.LENGTH_LONG).show();
            onFragmentSelected(1, null);
        } else if (id == R.id.menu3) {
            Toast.makeText(this, "세번째 메뉴 선택됨.", Toast.LENGTH_LONG).show();
            onFragmentSelected(2, null);
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onFragmentSelected(int position, Bundle bundle) {
        Fragment curFragment = null;

        if (position == 0) {
           System.out.println("첫번째 menu");
            UserManagement.getInstance()
                    .requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        } else if (position == 1) {
            System.out.println("두번째 menu");
            OAuthLogin mOAuthLogin = OAuthLogin.getInstance();
            String loginState = mOAuthLogin.getState(MainActivity.this).toString();
            if(!loginState.equals("NEED_LOGIN")){
                Log.e("Main Logout", "로그아웃 성공");
                mOAuthLogin.logout(MainActivity.this);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                Log.e("Main Logout", "로그아웃 실패");
            }
        } else if (position == 2) {
            System.out.println("세번째 menu");
            /*회원탈퇴 할 때 필요한 부분*/
            UserManagement.getInstance()
                    .requestUnlink(new UnLinkResponseCallback() {
                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                        }

                        @Override
                        public void onFailure(ErrorResult errorResult) {
                            Log.e("KAKAO_API", "연결 끊기 실패: " + errorResult);

                        }
                        @Override
                        public void onSuccess(Long result) {
                            Log.i("KAKAO_API", "연결 끊기 성공. id: " + result);
                        }
                    });
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        toolbartext.setText(data);
                        location_information_pref = getSharedPreferences("location_information", Activity.MODE_PRIVATE);
                        location_infromation_editor = location_information_pref.edit();
                        location_infromation_editor.putString("address", data);
                        location_infromation_editor.commit();
                    }
                }
                break;
        }
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

    @TargetApi(Build.VERSION_CODES.M)

    private void checkPermission() {

        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (String permission : permissions) {
            permissionCheck = this.checkSelfPermission(permission);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            }
        }

    }
}



