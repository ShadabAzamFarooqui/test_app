package com.app.mschooling.class_section_subject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.databinding.DataBindingUtil;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityAddGroupBinding;
import com.app.mschooling.com.databinding.ActivityAddSubjectBinding;
import com.google.gson.Gson;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.subject.UpdateSubjectGroupRequest;
import com.mschooling.transaction.response.subject.SubjectResponse;
import com.mschooling.transaction.response.subject.UpdateSubjectGroupResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddGroupActivity extends BaseActivity {
    SubjectResponse response;
    String classId;

    ActivityAddGroupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_add_group);
        ButterKnife.bind(this);


        response = new Gson().fromJson(getIntent("response"), SubjectResponse.class);
        classId = getIntent("classId");

        if (response == null) {
            toolbarClick(getString(R.string.tool_add_group));
        } else {
            toolbarClick(getString(R.string.update_add_group));
            binding.name.setText(response.getSubjectType().getGroupName());
        }

        binding. submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid=true;
                if (binding.name.getText().toString().isEmpty()) {
                   isValid= setError(binding.addGroupLayout,binding.name,getString(R.string.enter_group_name));

                }
                if (binding.name.getText().toString().equalsIgnoreCase("Other")) {
                    isValid= setError(binding.addGroupLayout,binding.name,getString(R.string.group_name_cant_other));
                }
                if (isValid){
                    if (response == null) {
                        Intent intent = new Intent();
                        intent.putExtra("name", binding.name.getText().toString().toUpperCase());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        UpdateSubjectGroupRequest request = new UpdateSubjectGroupRequest();
                        request.setClassId(classId);
                        request.setOldGroupName(response.getSubjectType().getGroupName());
                        request.setNewGroupName(binding.name.getText().toString());
                        apiCallBack(getApiCommonController().updateSubjectGroup(request));
                    }
                }else {
                    dialogError(getString(R.string.please_check_form));
                }

            }
        });


    }


    @Subscribe
    public void update(UpdateSubjectGroupResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            toastSuccess(response.getMessage().getMessage());
            Intent intent = new Intent();
            intent.putExtra("name", binding.name.getText().toString().toUpperCase());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }
}
