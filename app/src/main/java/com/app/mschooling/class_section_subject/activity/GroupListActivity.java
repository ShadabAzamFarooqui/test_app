package com.app.mschooling.class_section_subject.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.class_section_subject.adapter.GroupListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.filter.SubjectCriteria;
import com.mschooling.transaction.request.subject.DeleteSubjectGroupRequest;
import com.mschooling.transaction.response.subject.DeleteSubjectGroupResponse;
import com.mschooling.transaction.response.subject.GetSubjectResponse;
import com.mschooling.transaction.response.subject.SubjectResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupListActivity extends BaseActivity {

    List<SubjectResponse> responseList;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.add)
    LinearLayout add;
    @BindView(R.id.noRecord)
    LinearLayout noRecord;

    GroupListAdapter adapter;
    EventDelete event;
    String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);
        add = findViewById(R.id.add);
        toolbarClick(getString(R.string.tool_groups));

        classId=getIntent().getStringExtra("classId");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), AddGroupActivity.class), 1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SubjectCriteria criteria = new SubjectCriteria();
        criteria.setClassId(classId);
        apiCallBack(getApiCommonController().getSubjectList(criteria));
    }

    @Subscribe
    public void getSubject(GetSubjectResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            responseList = new ArrayList<>();
            for (int i = 0; i < response.getSubjectResponses().size(); i++) {
                if (response.getSubjectResponses().get(i).getSubjectType().getGroupName() != null) {
                    responseList.add(response.getSubjectResponses().get(i));
                }
            }

            if (responseList.size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
            } else {
                noRecord.setVisibility(View.GONE);
            }

            for (int i = 0; i < responseList.size(); i++) {
                for (int j = i + 1; j < responseList.size(); j++) {
                    if (responseList.get(i).getSubjectType().getGroupName().
                            equalsIgnoreCase(responseList.get(j).getSubjectType().getGroupName())) {
                        responseList.remove(j);
                    }
                }
            }

            for (int i = 0; i < responseList.size(); i++) {
                if (responseList.get(i).getSubjectType().getGroupName().equalsIgnoreCase("Other")) {
                    responseList.remove(i);
                }
            }


            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new GroupListAdapter(this, responseList,classId);
            recyclerView.setAdapter(adapter);
//            GroupListAdapter
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

    @Subscribe
    public void delete(DeleteSubjectGroupResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            toastSuccess(response.getMessage().getMessage());
            responseList.remove(event.getPosition());
            adapter.notifyDataSetChanged();
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    Intent intent = new Intent();
                    intent.putExtra("name", data.getStringExtra("name"));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        } catch (Exception ex) {
        }
    }




    @Subscribe
    public void delete(EventDelete event) {
        this.event = event;

        Dialog dialog = new Dialog(this);
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
                DeleteSubjectGroupRequest request = new DeleteSubjectGroupRequest();
                request.setClassId(getIntent().getStringExtra("classId"));
                request.setGroupName(event.getId());
                apiCallBack(getApiCommonController().deleteSubjectGroup(request));
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
