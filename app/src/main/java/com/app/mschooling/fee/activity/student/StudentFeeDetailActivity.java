package com.app.mschooling.fee.activity.student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.fee.adapter.StudentFeeDetailAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.filter.FeeDetailCriteria;
import com.mschooling.transaction.response.fee.GetStudentFeeDetailResponse;
import com.mschooling.transaction.response.fee.UpdateStudentFeeResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentFeeDetailActivity extends BaseActivity {

    @BindView(R.id.noRecord)
    LinearLayout noRecord;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.history)
    FloatingActionButton history;

    StudentFeeDetailAdapter adapter;

    GetStudentFeeDetailResponse response;

    String enrollmentId;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_student_detail);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.fee_details));

        enrollmentId=Preferences.getInstance(getApplicationContext()).getEnrollmentId();
        FeeDetailCriteria feeDetailCriteria = new FeeDetailCriteria();
        feeDetailCriteria.setEnrollmentId(enrollmentId);
        apiCallBack(getApiCommonController().getStudentFee(feeDetailCriteria));

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),FeeHistoryListActivity.class);
                intent.putExtra("enrollmentId",enrollmentId);
                startActivity(intent);
            }
        });
    }


    @Subscribe
    public void getFeeList(GetStudentFeeDetailResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
            adapter = new StudentFeeDetailAdapter(this, response.getStudentFees());
            recyclerView.setAdapter(adapter);
            if (response.getStudentFees().size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
            } else {
                noRecord.setVisibility(View.GONE);
            }
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            apiCallBack(getApiCommonController().getFeeList(ParameterConstant.getRole(getApplicationContext()),getIntent().getStringExtra("classId")));
        }
    }


    @Subscribe
    public void update(UpdateStudentFeeResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccessFinish(response.getMessage().getMessage());
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

}
