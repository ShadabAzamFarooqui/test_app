package com.app.mschooling.examination.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.examination.adapter.SelectSubjectListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.subject.GetSubjectResponse;

import org.greenrobot.eventbus.Subscribe;


public class SelectSubjectListActivity extends BaseActivity {

    LinearLayout noRecord;
    RecyclerView recyclerView;
    SelectSubjectListAdapter adapter;
    String intentString;
    Button submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);
        recyclerView = findViewById(R.id.recyclerView);
        submit = findViewById(R.id.submit);
        noRecord = findViewById(R.id.noRecord);
        submit.setVisibility(View.GONE);
        intentString = getIntent().getStringExtra("intent");
        toolbarClick(getString(R.string.tool_subjects));

        apiCallBack(getApiCommonController().getSubjectList(ParameterConstant.getRole(this),
                getIntent().getStringExtra("classId"),getIntent().getStringExtra("sectionId")));



    }


    @Subscribe
    public void getSubject(GetSubjectResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            adapter = new SelectSubjectListAdapter(this, intentString,response.getSubjectResponses());
            recyclerView.setAdapter(adapter);
            if (response.getSubjectResponses().size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                noRecord.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


}







