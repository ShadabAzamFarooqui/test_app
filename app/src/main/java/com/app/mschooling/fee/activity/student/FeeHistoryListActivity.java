package com.app.mschooling.fee.activity.student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.fee.adapter.FeeHistoryListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.filter.FeeHistoryCriteria;
import com.mschooling.transaction.response.fee.GetStudentFeeHistoryResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeeHistoryListActivity extends BaseActivity {

    @BindView(R.id.noRecord)
    LinearLayout noRecord;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.printer)
    LinearLayout printer;

    FeeHistoryListAdapter adapter;
    String intent;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_history);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.history));
        apiCallBack(getApiCommonController().getFeeHistory(ParameterConstant.getRole(getApplicationContext()),getIntent().getStringExtra("enrollmentId")));
        printer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FeeHistoryListActivity.this, "Coming soon!!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Subscribe
    public void getFeeList(GetStudentFeeHistoryResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
            adapter = new FeeHistoryListAdapter(this, intent, response.getStudentFeeHistoryResponses());
            recyclerView.setAdapter(adapter);
            if (response.getStudentFeeHistoryResponses().size() == 0) {
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
}
