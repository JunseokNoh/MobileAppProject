package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;


public class DaumWebViewActivity extends AppCompatActivity {

    private WebView browser;
    private String ip;

    private List<Address> list = null;
    private double Starting_latitude;
    private double Starting_longitude;

    private SharedPreferences location_information_pref;
    private SharedPreferences.Editor location_infromation_editor;

    class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processDATA(String data) {
            Bundle extra = new Bundle();
            Intent intent = new Intent();
            extra.putString("data", data);
            intent.putExtras(extra);

            location_information_pref = getSharedPreferences("location_information", Activity.MODE_PRIVATE);
            location_infromation_editor = location_information_pref.edit();

            //Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
            final Geocoder geocoder = new Geocoder(DaumWebViewActivity.this);
            try {
                list = geocoder.getFromLocationName(data, 10);
                Address addr = list.get(0);
                Starting_latitude = addr.getLatitude();
                Starting_longitude = addr.getLongitude();
                location_infromation_editor.putString("current_latitude" , Double.toString(Starting_latitude));
                location_infromation_editor.putString("current_longitude" , Double.toString(Starting_longitude));
                location_infromation_editor.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum_web_view);

        ip = getString(R.string.server_ip);
        browser = (WebView) findViewById(R.id.webView);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new MyJavaScriptInterface(), "Android");

        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                browser.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

        browser.loadUrl(ip+"/daum");
    }
}
