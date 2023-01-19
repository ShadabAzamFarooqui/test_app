package com.app.mschooling.fee.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.fee.adapter.CommonFeeListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.fee.GetFeeSetupResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommonFeeListActivity extends BaseActivity {

    @BindView(R.id.noRecord)
    LinearLayout noRecord;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    CommonFeeListAdapter adapter;
    String intent;



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_fee_list);
        context = this;
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.fees));
        apiCallBack(getApiCommonController().getFeeList(ParameterConstant.getRole(getApplicationContext()),getIntent().getStringExtra("classId")));


    }







    @Subscribe
    public void getFeeList(GetFeeSetupResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
            adapter = new CommonFeeListAdapter(this, intent, response.getFeeSetupResponse());
            recyclerView.setAdapter(adapter);

            if (response.getFeeSetupResponse().size()==0){
                noRecord.setVisibility(View.VISIBLE);
            }else {
                noRecord.setVisibility(View.GONE);
            }
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            apiCallBack(getApiCommonController().getFeeList(ParameterConstant.getRole(getApplicationContext()),getIntent().getStringExtra("classId")));
        }
    }
}
