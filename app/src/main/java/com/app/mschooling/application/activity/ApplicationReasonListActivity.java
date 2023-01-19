package com.app.mschooling.application.activity;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.mschooling.application.adapter.ApplicationReasonListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityNormalListBinding;
import com.app.mschooling.com.databinding.CustomFabBinding;
import com.app.mschooling.com.databinding.NoRecordBinding;
import com.app.mschooling.event_handler.EventDeleteBase;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.application.ApplicationReasonResponse;
import com.mschooling.transaction.response.application.DeleteApplicationReasonResponse;
import com.mschooling.transaction.response.application.GetApplicationReasonResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class ApplicationReasonListActivity extends BaseActivity {

    ApplicationReasonListAdapter adapter;
    List<ApplicationReasonResponse> responseList;
    ActivityNormalListBinding binding;
    NoRecordBinding noRecordBinding;
    CustomFabBinding customFabBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_normal_list);
        toolbarClick(getString(R.string.application_reason));
        noRecordBinding = binding.noRecord;
        customFabBinding = binding.add;

        customFabBinding.add.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), AddApplicationReasonActivity.class)));


    }

    @Override
    protected void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().getApplicationReason());

    }

    @Subscribe
    public void get(GetApplicationReasonResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            if (response.getApplicationReasonResponses().size() > 0) {
                noRecordBinding.noRecord.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
                responseList = response.getApplicationReasonResponses();
                binding.recyclerView.setHasFixedSize(true);
                binding.recyclerView.setFocusable(false);
                binding.recyclerView.setNestedScrollingEnabled(false);
                binding.recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
                adapter = new ApplicationReasonListAdapter(ApplicationReasonListActivity.this, responseList);
                binding.recyclerView.setAdapter(adapter);
            } else {
                noRecordBinding.noRecord.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    EventDeleteBase event;

    @Subscribe
    public void deleteEvent(EventDeleteBase event) {
        this.event = event;
        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_delete_notice);
        dialog.setCancelable(false);
        TextView delete = dialog.findViewById(R.id.delete);
        TextView cancel = dialog.findViewById(R.id.cancel);
        delete.setOnClickListener(v -> {
            dialog.dismiss();
            apiCallBack(getApiCommonController().deleteApplicationReason(responseList.get(event.getPosition()).getId()));
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    public void delete(DeleteApplicationReasonResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.responseList.remove(event.getPosition());
            if (this.responseList.size() == 0) {
                noRecordBinding.noRecord.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


}

