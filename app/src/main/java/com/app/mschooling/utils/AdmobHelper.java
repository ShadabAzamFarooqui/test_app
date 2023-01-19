package com.app.mschooling.utils;

import static com.app.mschooling.utils.otp_reader.AppSignatureHashHelper.TAG;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.app.mschooling.com.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdmobHelper {
    public static InterstitialAd mInterstitialAd;

    public static void loadInterstitialAdd(Context context) {


        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context, context.getResources().getString(R.string.interstitial_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }


                });



    }

    public static void showInterstitialAds(Context context) {
        if (count == Preferences.getInstance(context).getAdsCycle()) {
            updateCount(context);
            if (mInterstitialAd != null) {
                mInterstitialAd.show((Activity) context);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showInterstitialAds(context);
                    }
                }, 2000);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }

        }

    }

    public static void loadBannerAds(AdView adView) {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


    }


    public static int count = 0;
    public static void updateCount(Context context) {
        if (Preferences.getInstance(context).getAdsCycle() >= 0) {
            if (count == Preferences.getInstance(context).getAdsCycle()) {
                count = 0;
            } else {
                count++;
            }
        }

    }

}
