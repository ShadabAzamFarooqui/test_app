package com.app.mschooling.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.mschooling.home.admin.activity.AdminMainActivity;
import com.app.mschooling.home.student.activity.StudentMainActivity;
import com.app.mschooling.home.teacher.activity.TeacherMainActivity;
import com.google.gson.Gson;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.request.login.AuthenticationRequest;
import com.mschooling.transaction.request.register.AddRegisterRequest;
import com.mschooling.transaction.response.attendance.GetAttendanceMonthResponse;
import com.mschooling.transaction.response.configuration.ConfigurationResponse;
import com.mschooling.transaction.response.login.AuthenticationResponse;

/**
 * Created by pc on 10/7/2016.
 */
public class Preferences {

    private static final String login = "login";
    private static final String profileComplete = "profileComplete";
    private static final String token = "token";

    private static final String language = "language";
    private static Preferences instance;
    private static final String ROLE = "role";
    private static final String CODE = "code";
    private static final String pageNo1 = "pageNo1";
    private static final String pageNo2 = "pageNo2";
    private static final String enrollmentId = "userId";
    private static final String firstTimeAdminLogin = "firstTimeAdminLogin";

    private static final String userName = "userName";
    private static final String buildType = "buildType";
    private static final String paid = "paid";
    private static final String zoomSetup = "zoomSetup";
    private static final String attendanceMode = "attendanceMode";
    private static final String adsCycle = "adsCycle";
    private static final String academicSession = "academicSession";
    private static final String configuration = "configuration";
    private static final String authentication = "authentication";
    private static final String authenticationRequest = "authenticationRequest";
    private static final String monthResponse = "monthResponse";
    private static final String googlePlaceApiKey = "googlePlaceApiKey";
    private static final String youtubeApiKey = "youtubeApiKey";
    private static final String getApiCallDate = "getApiCallDate";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;


    private Preferences(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences("My_pref", 0);
        editor = pref.edit();
    }


    public static Preferences getInstance(Context context) {
        if (instance == null) {
            instance = new Preferences(context);
        }
        return instance;
    }

    public String getToken() {
        return pref.getString(token, "");
    }

    public void setToken(String cname) {
        editor.putString(token, cname);
        editor.commit();
    }

    public boolean isLogin() {
        return pref.getBoolean(login, false);
    }

    public void setLogin(Boolean cname) {
        editor.putBoolean(login, cname);
        editor.commit();
    }

    public boolean isPaid() {
        return pref.getBoolean(paid, false);
    }

    public void setPaid(Boolean cname) {
        editor.putBoolean(paid, cname);
        editor.commit();
    }

    public boolean isZoomSetup() {
        return pref.getBoolean(zoomSetup, false);
    }

    public void setZoomSetup(Boolean cname) {
        editor.putBoolean(zoomSetup, cname);
        editor.commit();
    }

    public String getAttendanceMode() {
        return pref.getString(attendanceMode, "");
    }

    public void setAttendanceMode(String cname) {
        editor.putString(attendanceMode, cname);
        editor.commit();
    }

    public boolean isProfileComplete() {
        return pref.getBoolean(profileComplete, false);
    }

    public void setProfileComplete(Boolean cname) {
        editor.putBoolean(profileComplete, cname);
        editor.commit();
    }

    public String getLanguage() {
        return pref.getString(language, "en");
    }

    public void setLanguage(String cname) {
        editor.putString(language, cname);
        editor.commit();
    }


    public String getGooglePlaceApiKey() {
        return pref.getString(googlePlaceApiKey, "");
    }

    public void setGooglePlaceApiKey(String cname) {
        editor.putString(googlePlaceApiKey, cname);
        editor.commit();
    }



    public String getYoutubeApiKey() {
        return pref.getString(youtubeApiKey, "");
    }

    public void setYoutubeApiKey(String cname) {
        editor.putString(youtubeApiKey, cname);
        editor.commit();
    }


    public String getROLE() {
        return pref.getString(ROLE, "");
    }

    public void setROLE(String role) {
        editor.putString(ROLE, role);
        editor.commit();
    }


    public String getCode() {
        return pref.getString(CODE, "");
    }

    public void setCode(String cname) {
        editor.putString(CODE, cname);
        editor.commit();
    }

    public int getPageNo1() {
        return pref.getInt(pageNo1, 0);
    }

    public void setPageNo1(int cname) {
        editor.putInt(pageNo1, cname);
        editor.commit();
    }

    public int getAdsCycle() {
        return pref.getInt(adsCycle, 0);
    }

    public void setAdsCycle(int cname) {
        editor.putInt(adsCycle, cname);
        editor.commit();
    }

    public int getPageNo2() {
        return pref.getInt(pageNo2, 0);
    }

    public void setPageNo2(int cname) {
        editor.putInt(pageNo2, cname);
        editor.commit();
    }

    public String getEnrollmentId() {
        return pref.getString(enrollmentId, "");
    }

    public void setEnrollmentId(String cname) {
        editor.putString(enrollmentId, cname);
        editor.commit();
    }

    public String getUserName() {
        return pref.getString(userName, "");
    }

    public void setUserName(String userName1) {
        editor.putString(userName, userName1);
        editor.commit();
    }


    public int getFirstTimeLogin() {
        return pref.getInt(firstTimeAdminLogin, 0);
    }

    public void setFirstTimeLogin(int cname) {
        editor.putInt(firstTimeAdminLogin, cname);
        editor.commit();
    }

    public String getBuildType() {
        return pref.getString(buildType, "Production");
    }

    public void setBuildType(String cname) {
        editor.putString(buildType, cname);
        editor.commit();
    }

    public String getAcademicSession() {
        return pref.getString(academicSession, "");
    }

    public void setAcademicSession(String cname) {
        editor.putString(academicSession, cname);
        editor.commit();
    }

    public ConfigurationResponse getConfiguration() {
        String userString = pref.getString(configuration, "");
        return new Gson().fromJson(userString, ConfigurationResponse.class);
    }

    public void setConfiguration(ConfigurationResponse configurationResponse) {
        editor.putString(configuration, new Gson().toJson(configurationResponse));
        editor.apply();
    }

    public AuthenticationResponse getAuthenticationResponse() {
        String userString = pref.getString(authentication, "");
        return new Gson().fromJson(userString, AuthenticationResponse.class);
    }

    public void setAuthenticationResponse(AuthenticationResponse configurationResponse) {
        editor.putString(authentication, new Gson().toJson(configurationResponse));
        editor.apply();
    }

    public AuthenticationRequest getAuthenticationRequest() {
        String userString = pref.getString(authenticationRequest, "");
        return new Gson().fromJson(userString, AuthenticationRequest.class);
    }

    public void setAuthenticationRequest(AuthenticationRequest request) {
        editor.putString(authenticationRequest, new Gson().toJson(request));
        editor.apply();
    }

    public GetAttendanceMonthResponse getMonthResponse() {
        String userString = pref.getString(monthResponse, "");
        return new Gson().fromJson(userString, GetAttendanceMonthResponse.class);
    }

    public void setMonthResponse(GetAttendanceMonthResponse request) {
        editor.putString(monthResponse, new Gson().toJson(request));
        editor.apply();
    }


    public String getApiCallDate() {
        return pref.getString(getApiCallDate, "");
    }

    public void setApiCallDate(String request) {
        editor.putString(getApiCallDate, request);
        editor.apply();
    }



    public static boolean isAdmin(Context context){
        return Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value());
    }

    public static boolean isTeacher(Context context){
        return Preferences.getInstance(context).getROLE().equals(Common.Role.TEACHER.value());
    }

    public static boolean isStudent(Context context){
        return Preferences.getInstance(context).getROLE().equals(Common.Role.STUDENT.value());
    }




    public void clear() {
        AppUser.clearInstance(_context);
        SharedPreferences preferences = _context.getSharedPreferences("My_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        AdminMainActivity.context = null;
        TeacherMainActivity.context = null;
        StudentMainActivity.context = null;
    }

}