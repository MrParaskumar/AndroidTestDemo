package com.androidtestdemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.startapp.sdk.adsbase.StartAppAd;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    // admob interstitial ads
    private InterstitialAd mInterstitialAd;
    String MyAdUnitId;
    Button show_ad,mutiple_lng;
    // native banner fb ads
    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;
    private final String TAG = MainActivity.class.getSimpleName();
    private NativeBannerAd nativeBannerAd;
    private NativeAdLayout nativeAdsLayout;
    private LinearLayout adsView;
    private NativeAd nativeAds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocal();
        StartAppAd.disableSplash();
        setContentView(R.layout.activity_main);
        show_ad = findViewById(R.id.show_ad);
        mutiple_lng =findViewById(R.id.mutiple_lng);

        controladmobid();
        mInterstitialAd = new InterstitialAd(this);
        nativebannerfbads();
        AudienceNetworkAds.initialize(this);
        loadNativeAd();
        mutiplelan();
    }

    private void mutiplelan() {
        mutiple_lng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickevent();
            }
        });
    }

    private void clickevent() {
        final String[] itemlist={"English","Hindi","Gujrati"};
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.selectlang);
        builder.setSingleChoiceItems(itemlist, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    setLocale("eng");
                    recreate();
                }else if (i==1){
                    setLocale("hi");
                    recreate();
                }else if (i==2){
                    setLocale("gu");
                    recreate();
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void setLocale(String locale) {
        Locale locale1=new Locale(locale);
        Locale.setDefault(locale1);
        Configuration configuration= new Configuration();
        configuration.locale =locale1;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor sharedPreferences=getSharedPreferences("Settings",MODE_PRIVATE).edit();
        sharedPreferences.putString("My_lang",locale);
        sharedPreferences.apply();
    }

    public void loadLocal(){
        SharedPreferences sharedPreferences=getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang=sharedPreferences.getString("My_lang","");
        setLocale(lang);
    }

    private void controladmobid() {
        MobileAds.initialize(this, getString(R.string.admobappid));
        Firebase.setAndroidContext(this);
        Firebase myFirebase = new Firebase("https://controlads-422f3.firebaseio.com/admob");
        myFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MyAdUnitId = dataSnapshot.getValue(String.class);
                mInterstitialAd.setAdUnitId(MyAdUnitId);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        show_ad.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                    startActivity(new Intent(MainActivity.this,MainActivity2.class));
                    StartAppAd.showAd(getApplicationContext());
                }

            }

        });
    }

    private void nativebannerfbads() {
        nativeBannerAd = new NativeBannerAd(this, "IMG_16_9_APP_INSTALL#2850987638247407_3570323352980495");
        nativeBannerAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                if (nativeBannerAd == null || nativeBannerAd != ad) {
                    return;
                }
                // Inflate Native Banner Ad into Container
                inflateAd(nativeBannerAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        });
        // load the ad
        nativeBannerAd.loadAd();

    }

    private void inflateAd(NativeBannerAd nativeBannerAd) {
        nativeBannerAd.unregisterView();
        nativeAdLayout = findViewById(R.id.native_banner_ad_container);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

        adView = (LinearLayout) inflater.inflate(R.layout.native_banner_fb_ads, nativeAdLayout, false);
        nativeAdLayout.addView(adView);
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(MainActivity.this, nativeBannerAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
//        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        AdIconView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
//        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
    }

    private void loadNativeAd() {
        nativeAds = new NativeAd(this, "IMG_16_9_APP_INSTALL#2850987638247407_3570323352980495");

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                if (nativeAds == null || nativeAds != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAds);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }

        };

        // Request an ad
        nativeAds.loadAd(
                nativeAds.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }

    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeAdsLayout = findViewById(R.id.native_ads_container);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adsView = (LinearLayout) inflater.inflate(R.layout.native_fb_ads, nativeAdsLayout, false);
        nativeAdsLayout.addView(adsView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = findViewById(R.id.ads_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(MainActivity.this, nativeAd, nativeAdsLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = adsView.findViewById(R.id.native_ads_icon);
        TextView nativeAdTitle = adsView.findViewById(R.id.native_ads_title);
        MediaView nativeAdMedia = adsView.findViewById(R.id.native_ads_media);
        TextView nativeAdSocialContext = adsView.findViewById(R.id.native_ads_social_context);
        TextView nativeAdBody = adsView.findViewById(R.id.native_ads_body);
        TextView sponsoredLabel = adsView.findViewById(R.id.native_ads_sponsored_label);
        Button nativeAdCallToAction = adsView.findViewById(R.id.native_ads_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAds.getAdvertiserName());
        nativeAdBody.setText(nativeAds.getAdBodyText());
        nativeAdSocialContext.setText(nativeAds.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAds.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAds.getAdCallToAction());
        sponsoredLabel.setText(nativeAds.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adsView, nativeAdMedia, nativeAdIcon, clickableViews);
    }

    @Override
    public void onBackPressed() {
        StartAppAd.onBackPressed(this);
        super.onBackPressed();
    }

}