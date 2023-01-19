package com.app.mschooling.fee.activity.student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.fee.adapter.StudentFeeDetailListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.filter.FeeDetailCriteria;
import com.mschooling.transaction.request.fee.UpdateStudentFeeRequest;
import com.mschooling.transaction.response.fee.GetStudentFeeDetailResponse;
import com.mschooling.transaction.response.fee.UpdateStudentFeeResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentFeeDetailListActivity extends BaseActivity {

    @BindView(R.id.noRecord)
    LinearLayout noRecord;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.edit)
    LinearLayout edit;
    @BindView(R.id.submit)
    Button submit;

    StudentFeeDetailListAdapter adapter;
    String intent;

    GetStudentFeeDetailResponse response;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_list);
        context = this;
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.fee_details));

        intent = getIntent().getStringExtra("intent");
        FeeDetailCriteria feeDetailCriteria = new FeeDetailCriteria();
        feeDetailCriteria.setEnrollmentId(getIntent().getStringExtra("enrollmentId"));
        apiCallBack(getApiCommonController().getStudentFee(feeDetailCriteria));

        if (intent.equals("edit")) {
            edit.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
        }else if (intent.equals("selection")) {
            edit.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
        }else {
            edit.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateStudentFeeRequest request=new UpdateStudentFeeRequest();
                request.setEnrollmentId(getIntent().getStringExtra("enrollmentId"));

                request.setStudentFees(response.getStudentFees());
                apiCallBack(getApiCommonController().updateStudentFee(request));
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), StudentFeeDetailListActivity.class);
                intent.putExtra("intent", "edit");
                intent.putExtra("enrollmentId", getIntent().getStringExtra("enrollmentId"));
                startActivityForResult(intent,1);
                finish();
            }
        });


        if (!Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())) {
            edit.setVisibility(View.GONE);
        }
    }


    @Subscribe
    public void getFeeList(GetStudentFeeDetailResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response=response;
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
            adapter = new StudentFeeDetailListAdapter(this, intent, response.getStudentFees());
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
