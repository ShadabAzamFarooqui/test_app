package com.app.mschooling.syllabus.activity;

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

import com.app.mschooling.syllabus.adapter.SyllabusListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.syllabus.SyllabusCriteria;
import com.mschooling.transaction.response.syllabus.DeleteSyllabusResponse;
import com.mschooling.transaction.response.syllabus.GetSyllabusResponse;

import org.greenrobot.eventbus.Subscribe;


public class SyllabusListActivity extends BaseActivity {

    LinearLayout noRecord;
    GetSyllabusResponse response;
    RecyclerView recyclerView;

    LinearLayout add;

    SyllabusListAdapter adapter;
    String syllabusId;
    String classId;
    String subjectId;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus_list);
        noRecord = (LinearLayout) findViewById(R.id.noRecord);
        recyclerView = findViewById(R.id.recyclerView);
//        delete = findViewById(R.id.delete);
        add = findViewById(R.id.add);

        toolbarClick(getString(R.string.syllabus));
        classId = getIntent().getStringExtra("classId");
        subjectId = getIntent().getStringExtra("subjectId");
        SyllabusCriteria criteria = new SyllabusCriteria();
        criteria.setClassId(classId);
        criteria.setSubjectId(subjectId);
        apiCallBack(getApiCommonController().getSyllabus(criteria));

        if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())) {
            add.setVisibility(View.VISIBLE);
        } else {
            add.setVisibility(View.GONE);
        }


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UploadSyllabusActivity.class);
                intent.putExtra("syllabusId", syllabusId);
                intent.putExtra("classId", classId);
                intent.putExtra("subjectId", subjectId);
                startActivityForResult(intent, 1);
            }
        });

    }


    @SuppressLint("RestrictedApi")
    @Subscribe
    public void getSyllabus(GetSyllabusResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;

            if (response.getSyllabusResponse() == null) {
                noRecord.setVisibility(View.VISIBLE);
            } else if (response.getSyllabusResponse().getFirebases() == null) {
                noRecord.setVisibility(View.VISIBLE);
            } else if (response.getSyllabusResponse().getFirebases().size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
            } else {

                noRecord.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
                adapter = new SyllabusListAdapter(this, response.getSyllabusResponse(), classId, subjectId);
                recyclerView.setAdapter(adapter);

            }
            syllabusId = response.getSyllabusResponse().getId();
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @SuppressLint("RestrictedApi")
    @Subscribe
    public void delete(DeleteSyllabusResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response.getSyllabusResponse().getFirebases().remove(event.getPosition());
            adapter.notifyDataSetChanged();
            if (this.response.getSyllabusResponse() == null) {
                noRecord.setVisibility(View.VISIBLE);
            } else if (this.response.getSyllabusResponse().getFirebases() == null) {
                noRecord.setVisibility(View.VISIBLE);
            } else if (this.response.getSyllabusResponse().getFirebases().size() == 0) {
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
            SyllabusCriteria criteria = new SyllabusCriteria();
            criteria.setClassId(classId);
            criteria.setSubjectId(subjectId);
            apiCallBack(getApiCommonController().getSyllabus(criteria));
        }
    }


    EventDelete event;

    @Subscribe
    public void deleteEvent(EventDelete event) {
        this.event = event;

        Dialog dialog = new Dialog(SyllabusListActivity.this);
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
                apiCallBack(getApiCommonController().deleteSyllabus(response.getSyllabusResponse().getId(), event.getId()));
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

