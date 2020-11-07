package com.androidtestdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        StartAppSDK.init(this, getString(R.string.startapp_app_id), false);
        StartAppSDK.setTestAdsEnabled(true);
    }
    @Override
    public void onBackPressed() {
        StartAppAd.onBackPressed(this);
        super.onBackPressed();
    }
}
