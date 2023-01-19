package com.app.mschooling.study_material.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.study_material.adapter.StudyMaterialListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.study.DeleteStudyResponse;
import com.mschooling.transaction.response.study.GetStudyResponse;

import org.greenrobot.eventbus.Subscribe;


public class StudyMaterialListActivity extends BaseActivity {

    LinearLayout noRecord;
    GetStudyResponse response;
    RecyclerView recyclerView;

    LinearLayout add;

    StudyMaterialListAdapter adapter;
    String categoryId;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus_list);
        noRecord = (LinearLayout) findViewById(R.id.noRecord);
        recyclerView = findViewById(R.id.recyclerView);
//        delete = findViewById(R.id.delete);
        add = findViewById(R.id.add);

        toolbarClick(getString(R.string.study_material));
        categoryId = getIntent().getStringExtra("categoryId");
        apiCallBack(getApiCommonController().getStudyMaterialList(categoryId));

        if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.STUDENT.value())) {
            add.setVisibility(View.GONE);
        } else {
            add.setVisibility(View.VISIBLE);
        }


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UploadStudyMaterialActivity.class);
                intent.putExtra("categoryId", categoryId);
                startActivityForResult(intent, 1);
            }
        });

    }


    @SuppressLint("RestrictedApi")
    @Subscribe
    public void getStudyMaterial(GetStudyResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            if (response.getStudyResponses() == null) {
                noRecord.setVisibility(View.VISIBLE);
            } else if (response.getStudyResponses().size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
            } else {
                noRecord.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
                adapter = new StudyMaterialListAdapter(this, response, categoryId);
                recyclerView.setAdapter(adapter);

            }

        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @SuppressLint("RestrictedApi")
    @Subscribe
    public void delete(DeleteStudyResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response.getStudyResponses().remove(event.getPosition());
            adapter.notifyDataSetChanged();
            if (this.response.getStudyResponses().size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
            }
//            dialogSuccessFinish(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            apiCallBack(getApiCommonController().getStudyMaterialList(getIntent().getStringExtra("categoryId")));
        }
    }


    EventDelete event;

    @Subscribe
    public void deleteEvent(EventDelete event) {
        this.event = event;

        Dialog dialog = new Dialog(StudyMaterialListActivity.this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_delete_notice);
        dialog.setCancelable(false);
        TextView delete = dialog.findViewById(R.id.delete);
        TextView cancel = dialog.findViewById(R.id.cancel);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                apiCallBack(getApiCommonController().deleteStudyMaterial(categoryId, event.getId()));
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

}

