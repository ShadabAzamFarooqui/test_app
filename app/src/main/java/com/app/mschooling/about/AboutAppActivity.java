package com.app.mschooling.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.ParameterConstant;

public class AboutAppActivity extends BaseActivity {

    TextView yourVersion;
    TextView availableVersion;
    TextView lastUpdated;
    Button submit;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        toolbarClick(getString(R.string.about_app));
        yourVersion = findViewById(R.id.yourVersion);
        availableVersion = findViewById(R.id.availableVersion);
        submit = findViewById(R.id.submit);
        update = findViewById(R.id.update);
        lastUpdated = findViewById(R.id.lastUpdated);

        try {
            yourVersion.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            availableVersion.setText(AppUser.getInstance().getVersionResponse().getVersionResponse().getVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (availableVersion.getText().toString().equals(yourVersion.getText().toString())) {
            update.setVisibility(View.GONE);
        } else {
            submit.setVisibility(View.GONE);
        }

        submit.setOnClickListener(v -> finish());

        update.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(ParameterConstant.getPlayStoreUrl()  + getApplicationContext().getPackageName()));
            startActivity(i);
        });


    }


}
