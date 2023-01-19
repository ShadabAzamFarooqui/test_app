package com.app.mschooling.paid.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Paid2Activity extends BaseActivity {



    @BindView(R.id.attendanceMessage)
    TextView attendanceMessage;
    @BindView(R.id.planMessage)
    TextView planMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid2);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra("intent") == null) {
            toolbarClick(getString(R.string.plan));
            planMessage.setVisibility(View.VISIBLE);
            attendanceMessage.setVisibility(View.GONE);
        } else {
            attendanceMessage.setVisibility(View.VISIBLE);
            planMessage.setVisibility(View.GONE);
            toolbarClick(getString(R.string.pending));
        }


    }


}
