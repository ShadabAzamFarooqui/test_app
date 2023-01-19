package com.app.mschooling.homework.activity;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityHomeWorkStatusListBinding;
import com.app.mschooling.event_handler.CommonEvent;
import com.app.mschooling.event_handler.EventBasedOnType;
import com.app.mschooling.homework.adapter.HomeWorkStatusListAdapter;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.homework.UpdateStudentWorksheetRequest;
import com.mschooling.transaction.response.homework.GetStudentWorksheetResponse;
import com.mschooling.transaction.response.homework.UpdateStudentWorksheetResponse;

import org.greenrobot.eventbus.Subscribe;

public class HomeWorkStatusListActivity extends BaseActivity {

    ActivityHomeWorkStatusListBinding binding;
    GetStudentWorksheetResponse response;
    HomeWorkStatusListAdapter adapter;
    EventBasedOnType eventBasedOnType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_work_status_list);
        toolbarClick(getString(R.string.homework));

        apiCallBack(getApiCommonController().getWorkSheet(
                ParameterConstant.getRole(this),
                getIntent("homeworkId"),
                getIntent("classId"),
                getIntent("sectionId"),
                getIntent("subjectId")
        ));

    }

    @Subscribe
    public void getResponse(GetStudentWorksheetResponse response) {
        if (Status.SUCCESS.value().equals(response.getStatus().value())) {
            this.response = response;
            binding.recyclerView.setHasFixedSize(true);
            binding.recyclerView.setFocusable(false);
            binding.recyclerView.setNestedScrollingEnabled(false);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new HomeWorkStatusListAdapter(this, response.getStudentWorksheetResponses());
            binding.recyclerView.setAdapter(adapter);
            if (response.getStudentWorksheetResponses().size() == 0) {
                binding.noRecord.noRecord.setVisibility(View.VISIBLE);
            } else {
                binding.noRecord.noRecord.setVisibility(View.GONE);
            }
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @SuppressLint("SetTextI18n")
    @Subscribe
    public void clickOnItem(EventBasedOnType eventBaseOnType) {
        this.eventBasedOnType = eventBaseOnType;
        String text = Helper.capsFirstOtherInLower(eventBaseOnType.getType());
        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(false);
        TextView tittle = dialog.findViewById(R.id.tittle);
        TextView message = dialog.findViewById(R.id.message);
        TextView action = dialog.findViewById(R.id.action);
        TextView cancel = dialog.findViewById(R.id.cancel);

        message.setText(getString(R.string.are_you_sure_you_want_to) + " " + text.toLowerCase() + "?");
        tittle.setText(text);
        action.setText(text);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateStudentWorksheetRequest request = new UpdateStudentWorksheetRequest();
                request.setEnrollmentId(response.getStudentWorksheetResponses().get(eventBaseOnType.getPosition()).getStudentBasicResponse().getEnrollmentId());
                request.setWorksheetStatus(eventBaseOnType.getWorksheetStatus());
                request.setId(getIntent("homeworkId"));
                apiCallBack(getApiCommonController().updateWorkSheetStatus(ParameterConstant.getRole(getApplicationContext()), request));
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Subscribe
    public void clickOnMainLayout(CommonEvent commonEvent) {
        if (response.getStudentWorksheetResponses().get(commonEvent.getPosition()).getWorksheetStatus() == Common.WorksheetStatus.NOT_SUBMITTED) {
            dialogError(getString(R.string.not_submitted_yet));
        } else {
            Intent intent = new Intent(this,StudentHomeworkUploadAndListActivity.class);
            intent.putExtra("homeworkId", getIntent("homeworkId"));
            intent.putExtra("enrollmentId", response.getStudentWorksheetResponses().get(commonEvent.getPosition()).getStudentBasicResponse().getEnrollmentId());
            startActivity(intent);
        }

    }


    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Subscribe
    public void updateResponse(UpdateStudentWorksheetResponse response) {
        if (Status.SUCCESS.value().equals(response.getStatus().value())) {
            this.response.getStudentWorksheetResponses().get(eventBasedOnType.getPosition()).setWorksheetStatus(eventBasedOnType.getWorksheetStatus());
            adapter.notifyDataSetChanged();
        } else {
            dialogError(response.getMessage().getMessage());
        }

    }
}