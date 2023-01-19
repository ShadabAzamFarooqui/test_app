package com.app.mschooling.examination.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExaminationDashboardActivity extends BaseActivity {

    @BindView(R.id.exams)
    RelativeLayout exams;
    @BindView(R.id.createResult)
    RelativeLayout createResult;
    @BindView(R.id.publishResult)
    RelativeLayout publishResult;
    @BindView(R.id.result)
    RelativeLayout result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination_dashboard);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.examination));

        exams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExaminationListActivity.class);
                intent.putExtra("intentString", "exam");
                startActivity(intent);
            }
        });
        publishResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExaminationListActivity.class);
                intent.putExtra("intentString","publish");
                startActivity(intent);
            }
        });

        createResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectExaminationActivity.class);
                startActivity(intent);
            }
        });

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectClassExaminationActivity.class);
                startActivity(intent);
            }
        });
    }

}