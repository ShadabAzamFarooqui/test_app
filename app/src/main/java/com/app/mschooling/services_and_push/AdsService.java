package com.app.mschooling.services_and_push;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.app.mschooling.utils.AdmobHelper;

public class AdsService extends IntentService {


    static Context context;

    public AdsService() {
        super("AdsService");
    }

    public static void startService(Context context) {
        AdsService.context = context;
        Intent intent = new Intent(context, AdsService.class);
        context.startService(intent);
        AdmobHelper.loadInterstitialAdd(context);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            Thread.sleep(20*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AdmobHelper.showInterstitialAds(this);
        startService(context);
    }



}

