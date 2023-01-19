package com.app.mschooling.about;

import android.Manifest;
import android.content.Intent;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.about.us.GetAboutUsResponse;

import org.greenrobot.eventbus.Subscribe;

import java.net.URLEncoder;

public class AboutUsActivity extends BaseActivity implements OnMapReadyCallback {
    TextView header, description;
    String title = "";
    double lat = 28, lon = 77;
    LinearLayout mainLayout;
    String mobileNumber;
    String emailId;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        toolbarClick(getString(R.string.about_us));
        findIds();
        setListeners();
        mainLayout.setVisibility(View.GONE);
    }

    private void setListeners() {


    }

    private void findIds() {
        header = findViewById(R.id.header);
        description = findViewById(R.id.description);
        mainLayout = findViewById(R.id.mainLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().getAboutUs());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(lat, lon);
        googleMap.addMarker(new MarkerOptions().position(sydney).title(title));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(8.0f));
    }


    @Subscribe
    public void getResponse(GetAboutUsResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            mainLayout.setVisibility(View.VISIBLE);
            title = response.getAddress();
            lat = Double.parseDouble(response.getLat());
            lon = Double.parseDouble(response.getLon());
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            header.setText(response.getTitle());
            description.setText(response.getDescription());

            mapFragment.getMapAsync(this);
            mobileNumber="+91"+response.getMobile();
            emailId=response.getEmail();

        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    public void call(View view) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + mobileNumber));
            startActivity(callIntent);
        }

    }

    public void email(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = {emailId};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject_message));
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent, "Send mail"));
    }


    public void whatsApp(View view) {
        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String url = "https://api.whatsapp.com/send?phone=" + mobileNumber + "&text=" + URLEncoder.encode("*"+getString(R.string.email_subject_message)+"*\n", "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), R.string.whatsapp_not_exist, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.whatsapp_not_exist, Toast.LENGTH_SHORT).show();
        }
    }

}
