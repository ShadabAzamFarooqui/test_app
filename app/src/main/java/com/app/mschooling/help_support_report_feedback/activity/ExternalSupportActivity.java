package com.app.mschooling.help_support_report_feedback.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.about.us.GetAboutUsResponse;

import org.greenrobot.eventbus.Subscribe;

import java.net.URLEncoder;

public class ExternalSupportActivity extends BaseActivity {

    LinearLayout mainLayout;
    GetAboutUsResponse response;
    TextView mobile,mailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external_support_small);
        toolbarClick(getString(R.string.support));

        TextView version = findViewById(R.id.version);
        mainLayout = findViewById(R.id.mainLayout);
        mobile = findViewById(R.id.mobile);
        mailId = findViewById(R.id.mailId);
        mainLayout.setVisibility(View.GONE);

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version.setText(getString(R.string.version)+" " + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        apiCallBack(getApiCommonController().getAboutUs());
    }


    public void call(View view) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + response.getMobile()));
            startActivity(callIntent);
        }

    }

    public void email(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = {response.getEmail()};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, response.getQuery());
        intent.putExtra(Intent.EXTRA_TEXT, "");
//        intent.putExtra(Intent.EXTRA_CC,"mailcc@gmail.com");
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent, "Send mail"));
    }


    public void whatsApp(View view) {
        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String url = "https://api.whatsapp.com/send?phone=91" + response.getMobile() + "&text=" + URLEncoder.encode("*"+response.getQuery()+"*\n", "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.whatsapp_not_exist), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.whatsapp_not_exist), Toast.LENGTH_SHORT).show();
        }
    }


    @Subscribe
    public void getResponse(GetAboutUsResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            mainLayout.setVisibility(View.VISIBLE);
            this.response = response;
            mobile.setText(response.getMobile());
            mailId.setText(response.getEmail());

        } else {
            dialogError(response.getMessage().getMessage());
        }
    }
}
