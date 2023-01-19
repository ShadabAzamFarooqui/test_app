package com.app.mschooling.setting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;

import com.app.mschooling.application.activity.AddApplicationReasonActivity;
import com.app.mschooling.class_section_subject.activity.AddClassActivity;
import com.app.mschooling.complaint.activity.AddComplaintReasonActivity;
import com.app.mschooling.class_section_subject.activity.AddSubjectActivity;
import com.app.mschooling.other.activity.QRCodeActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;

public class SettingActivity extends BaseActivity {

    LinearLayout generateQRCode;
    LinearLayout addSubject;
    LinearLayout addClass;
    LinearLayout addComplaintReason;
    LinearLayout addApplicationReason;
    CardView configuration;
    LinearLayout theme;
    public static String customFontString="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        toolbarClick(getString(R.string.action_settings));
        generateQRCode= findViewById(R.id.generate_qr_code);
        addSubject= findViewById(R.id.addSubject);
        addClass= findViewById(R.id.addClass);
        addComplaintReason= findViewById(R.id.addComplaintReason);
        addApplicationReason= findViewById(R.id.addApplicationReason);
        theme= findViewById(R.id.theme);
        configuration= findViewById(R.id.configuration);

        if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())){
            configuration.setVisibility(View.VISIBLE);
        }else {
            configuration.setVisibility(View.GONE);
        }

        generateQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), QRCodeActivity.class));
            }
        });

        addSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getApplicationContext(), AddSubjectActivity.class);
                intent.putExtra("id","");
                startActivity(intent);
            }
        });

        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(), AddClassActivity.class));
            }
        });

        addComplaintReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddComplaintReasonActivity.class));
            }
        });
        addApplicationReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddApplicationReasonActivity.class));
            }
        });

        theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

}
