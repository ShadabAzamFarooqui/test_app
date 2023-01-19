package com.app.mschooling.other.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.mschooling.home.admin.activity.AdminMainActivity;
import com.app.mschooling.home.student.activity.StudentMainActivity;
import com.app.mschooling.home.teacher.activity.TeacherMainActivity;
import com.app.mschooling.enrollment.LandingPageActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;

import java.util.Locale;

public class LanguageSelectionActivity extends BaseActivity {


    LinearLayout english;
    LinearLayout hindi;
    Button submit;

    String selection = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
        toolbarClick(getString(R.string.tool_select_language));
        english = findViewById(R.id.english);
        hindi = findViewById(R.id.hindi);
        submit = findViewById(R.id.submit);

        if (Preferences.getInstance(getApplicationContext()).getLanguage().equals("en")) {
            selection = "en";
            english.setBackground(getResources().getDrawable(R.drawable.view_rounded_selected));
            hindi.setBackground(getResources().getDrawable(R.drawable.view_rounded_unselected));
        } else if (Preferences.getInstance(getApplicationContext()).getLanguage().equals("hi")) {
            selection = "hi";
            english.setBackground(getResources().getDrawable(R.drawable.view_rounded_unselected));
            hindi.setBackground(getResources().getDrawable(R.drawable.view_rounded_selected));
        }

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection = "en";
                english.setBackground(getResources().getDrawable(R.drawable.view_rounded_selected));
                hindi.setBackground(getResources().getDrawable(R.drawable.view_rounded_unselected));
//                submit.setBackground(getResources().getDrawable(R.drawable.login_button_style));
            }
        });


        hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection = "hi";
                english.setBackground(getResources().getDrawable(R.drawable.view_rounded_unselected));
                hindi.setBackground(getResources().getDrawable(R.drawable.view_rounded_selected));
//                submit.setBackground(getResources().getDrawable(R.drawable.login_button_style));
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selection.equals("en")) {
                    Preferences.getInstance(getApplicationContext()).setLanguage("en");
                    setLocale("en");
                } else {
                    Preferences.getInstance(getApplicationContext()).setLanguage("hi");
                    setLocale("hi");
                }
                if (getIntent().getStringExtra("intent") == null) {
                    Intent intent;
                    if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())) {
                        intent = new Intent(LanguageSelectionActivity.this, AdminMainActivity.class);
                    } else if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.TEACHER.value())) {
                        intent = new Intent(LanguageSelectionActivity.this, TeacherMainActivity.class);
                    } else {
                        intent = new Intent(LanguageSelectionActivity.this, StudentMainActivity.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(LanguageSelectionActivity.this, LandingPageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                finish();


            }
        });
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
