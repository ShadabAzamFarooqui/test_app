package com.app.mschooling.class_section_subject.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.class_section_subject.adapter.ClassSectionListBaseAdapter;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDeleteBase;
import com.app.mschooling.event_handler.EventDeleteChild;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.student.DeleteClassRequest;
import com.mschooling.transaction.request.student.DeleteSectionRequest;
import com.mschooling.transaction.response.section.GetStandaloneClassResponse;
import com.mschooling.transaction.response.student.ClassResponse;
import com.mschooling.transaction.response.student.DeleteClassResponse;
import com.mschooling.transaction.response.student.DeleteSectionResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;


public class ClassSectionListActivity extends BaseActivity {

    LinearLayout noRecord;
    EditText search;
    RecyclerView recycler_view;
    LinearLayout add;
    ClassSectionListBaseAdapter adapter;
    List<ClassResponse> responseList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_list);
        search = findViewById(R.id.search);
        recycler_view = findViewById(R.id.recycler_view);
        noRecord = findViewById(R.id.noRecord);
        add = findViewById(R.id.add);
        toolbarClick(getString(R.string.classes));


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddClassActivity.class));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().getClassList(ParameterConstant.getRole(this)));
    }

    @Subscribe
    public void getClassList(GetStandaloneClassResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.responseList = response.getClassResponses();
            recycler_view.setHasFixedSize(true);
            recycler_view.setFocusable(false);
            recycler_view.setNestedScrollingEnabled(false);
            recycler_view.setLayoutManager(new GridLayoutManager(this, 1));
            adapter = new ClassSectionListBaseAdapter(this,  responseList);
            recycler_view.setAdapter(adapter);
            if (response.getClassResponses().size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
                recycler_view.setVisibility(View.GONE);
            } else {
                noRecord.setVisibility(View.GONE);
                recycler_view.setVisibility(View.VISIBLE);
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    EventDeleteBase eventDeleteBase;

    @Subscribe
    public void deleteClassEvent(EventDeleteBase eventDeleteBase) {
        this.eventDeleteBase = eventDeleteBase;
        DeleteClassRequest request=new DeleteClassRequest();
        request.setClassId(responseList.get(eventDeleteBase.getPosition()).getId());
        request.setDescription( eventDeleteBase.getMessage());
        apiCallBack(getApiCommonController().deleteClass(request));
    }

    EventDeleteChild eventDeleteChild;

    @Subscribe
    public void deleteSectionEvent(EventDeleteChild eventDeleteChild) {
        this.eventDeleteChild = eventDeleteChild;
        DeleteSectionRequest request=new DeleteSectionRequest();
        request.setSectionId(responseList.get(eventDeleteChild.getGroupPosition()).getSectionResponses().get(eventDeleteChild.getChildPosition()).getId());
        request.setDescription(eventDeleteChild.getMessage());
        apiCallBack(getApiCommonController().deleteSection(request));
    }


    @Subscribe
    public void deleteSection(DeleteSectionResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            responseList.get(eventDeleteChild.getGroupPosition()).getSectionResponses().remove(eventDeleteChild.getChildPosition());
            adapter.notifyDataSetChanged();
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void deleteClass(DeleteClassResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            responseList.remove(eventDeleteBase.getPosition());
            adapter.notifyDataSetChanged();
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

}





