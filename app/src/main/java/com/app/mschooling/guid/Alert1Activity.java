package com.app.mschooling.guid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.app.mschooling.class_section_subject.activity.AddClassActivity;
import com.app.mschooling.com.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Alert1Activity extends AppCompatActivity {

    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.add)
    LinearLayout add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert1);
        ButterKnife.bind(this);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddClassActivity.class));
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Alert2Activity.class));
            }
        });
    }
}