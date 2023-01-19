package com.app.mschooling.other.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.enrollment.LandingPageActivity;
import com.app.mschooling.guid.AlertActivity;
import com.app.mschooling.home.admin.activity.AdminMainActivity;
import com.app.mschooling.home.student.activity.StudentMainActivity;
import com.app.mschooling.home.teacher.activity.TeacherMainActivity;
import com.app.mschooling.students.profile.activity.UpdateStudentProfileActivity;
import com.app.mschooling.teachers.profile.activity.UpdateTeacherProfileActivity;
import com.app.mschooling.utils.AdmobHelper;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
       App ID: ca-app-pub-2465479969938764~8886544691
       unit ID: ca-app-pub-2465479969938764/2708805692
       */

public class SplashActivity extends BaseActivity {

    TextView textView;
    TextView version;


    public static final int MULTIPLE_PERMISSIONS = 4;
    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE};


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ParameterConstant.isMSchooling(this)) {
            setContentView(R.layout.activity_splash);
        } else if (ParameterConstant.isLittleSteps(this)) {
            setContentView(R.layout.little_steps_activity_splash);
        }
        textView = findViewById(R.id.textView);
        version = findViewById(R.id.version);


        AdmobHelper.loadInterstitialAdd(this);


        slideSplashView();
        version.setText("Version " + Helper.getVersion(this));

        if (Preferences.getInstance(this).getLanguage().equals("en")) {
            setLocale("en");
        } else {
            setLocale("hi");
        }


        AppUser.getInstance().setClassResponse(null);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int permission_call = PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA);
//            final int permission_readphonestate = PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            final int permission_readexternal = PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            final int permission_writeexternal = PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            final int call_phone = PermissionChecker.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
//            final int permission_location = PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (/*permission_location == PermissionChecker.PERMISSION_GRANTED
                    &&*/ permission_call == PermissionChecker.PERMISSION_GRANTED
//                    && permission_readphonestate == PermissionChecker.PERMISSION_GRANTED
                    && permission_readexternal == PermissionChecker.PERMISSION_GRANTED
                    && permission_writeexternal == PermissionChecker.PERMISSION_GRANTED
                    && call_phone == PermissionChecker.PERMISSION_GRANTED) {
                launchActivity();
            } else {
                checkPermissions();
            }
        } else {
            launchActivity();
        }


    }


    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(SplashActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);

            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchActivity();
                } else {
                    launchActivity();
                }
                return;
            }
        }
    }


    int i;

    private void launchActivity() {
        if (ParameterConstant.isMSchooling(this)) {
            String str = "Powered by mSchooling ";
            char[] ar = str.toCharArray();
            recursion(ar);
        } else if (ParameterConstant.isLittleSteps(this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    afterRecursion();
                }
            },2000);

        }

    }

    void recursion(char[] ar) {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        textView.append("" + ar[i]);
                        if (i != ar.length - 1) {
                            recursion(ar);
                        } else {
                            afterRecursion();
                        }
                        i++;
                    } catch (Exception e) {
                        afterRecursion();
                    }

                }
            }, 100);
        } catch (Exception e) {
            afterRecursion();
        }


    }


    void afterRecursion() {
//        new FusedLocation(SplashActivity.this);
        if (Preferences.getInstance(getApplicationContext()).isLogin()) {
            try {
                Thread.sleep(1000);
                if (Preferences.getInstance(getApplicationContext()).getConfiguration().getUserSetup().getRole() == Common.Role.TEACHER) {
                    startActivity(new Intent(getApplicationContext(), UpdateTeacherProfileActivity.class));
                    if (!Preferences.getInstance(getApplicationContext()).isProfileComplete()) {
                        startActivity(new Intent(getApplicationContext(), UpdateTeacherProfileActivity.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), TeacherMainActivity.class));
                    }

                } else if (Preferences.getInstance(getApplicationContext()).getConfiguration().getUserSetup().getRole() == Common.Role.ADMIN) {
                    if (!Preferences.getInstance(getApplicationContext()).getConfiguration().getAdminSetup().isClassAdded() || !Preferences.getInstance(getApplicationContext()).getConfiguration().getAdminSetup().isClassAdded()) {
                        startActivity(new Intent(getApplicationContext(), AlertActivity.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), AdminMainActivity.class));
                    }

                } else if (Preferences.getInstance(getApplicationContext()).getConfiguration().getUserSetup().getRole() == Common.Role.STUDENT) {
                    if (!Preferences.getInstance(getApplicationContext()).isProfileComplete()) {
                        startActivity(new Intent(getApplicationContext(), UpdateStudentProfileActivity.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), StudentMainActivity.class));
                    }
                }
            } catch (Exception e) {
                moveToLogin();
            }
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(getApplicationContext(), LandingPageActivity.class));
        }
        finish();
    }


    protected void slideSplashView() {
        findViewById(R.id.a)
                .setAnimation(AnimationUtils.loadAnimation(this, R.anim.enter_from_bottom_centered));

        findViewById(R.id.b)
                .setAnimation(AnimationUtils.loadAnimation(this, R.anim.enter_from_bottom));

        findViewById(R.id.c)
                .setAnimation(AnimationUtils.loadAnimation(this, R.anim.enter_from_bottom));
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }


    void moveToLogin() {
        startActivity(new Intent(getApplicationContext(), LandingPageActivity.class));
    }
}
