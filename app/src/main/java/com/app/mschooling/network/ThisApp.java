package com.app.mschooling.network;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Preferences;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ThisApp extends Application {

    private static Api apiCommonController;
    private static ThisApp mInstance;

    public static synchronized ThisApp getInstance() {
        return mInstance;
    }


    public static void setApi() {
        apiCommonController = null;
    }

    public static Api getApiCommonController(Context context) {
        setApi();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);

        client.addInterceptor(chain -> {
            Request.Builder builder = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-APP_NAME", context.getResources().getString(R.string.app_name));
            String token = Preferences.getInstance(context).getToken();
            if (null != token && !token.equals("")) {
                builder.addHeader("Authorization", "Bearer " + token);
            }
            return chain.proceed(builder.build());
        });
        String url;
        if (BuildConfig.DEBUG) {
            if (Preferences.getInstance(context).getBuildType().equals("Staging")) {
                url = Api.BASE_URL_DEVELOPMENT;
            } else {
                url = Api.BASE_URL_PRODUCTION;
            }
            client.addInterceptor(interceptor);
        } else {
            url = Api.BASE_URL_PRODUCTION;
        }
        client.addInterceptor(interceptor);

        if (apiCommonController == null) {
            apiCommonController = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                            .setLenient()
                            .create()))
                    .build()
                    .create(Api.class);
        }
        return apiCommonController;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }


}