package com.app.mschooling.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.gson.Gson;


public class LocalRepositories {
    static String PREFS_APP_USER = "com.app.mschooling.com";

    public synchronized static void saveAppUser(Context ctx, AppUser user) {

        String jsonString = new Gson().toJson(user);
        PreferenceManager.getDefaultSharedPreferences(ctx)
                .edit()
                .putString(PREFS_APP_USER, jsonString)
                .commit();

    }

    public synchronized static AppUser getAppUser(Context ctx) {

        String jsonString = PreferenceManager.getDefaultSharedPreferences(ctx)
                .getString(PREFS_APP_USER, "");

        return "".equals(jsonString) ?
                null : new Gson().fromJson(jsonString, AppUser.class);
    }
}
