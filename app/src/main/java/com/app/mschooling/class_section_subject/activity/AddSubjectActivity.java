package com.app.mschooling.class_section_subject.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityAddSubjectBinding;
import com.google.gson.Gson;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.subject.AddSubjectRequest;
import com.mschooling.transaction.request.subject.SubjectType;
import com.mschooling.transaction.response.subject.AddSubjectResponse;
import com.mschooling.transaction.response.subject.SubjectResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class AddSubjectActivity extends BaseActivity {

    List<String> subjectNames = new ArrayList<>();
    List<String> subjectIds = new ArrayList<>();
    String classId;
    String update;
    SubjectResponse subjectResponse;

    ActivityAddSubjectBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_subject);
        update = getIntent().getStringExtra("update");
        if (update == null) {
            toolbarClick(getString(R.string.add_subject));
        } else {
            toolbarClick(getString(R.string.update_subject));
            subjectResponse = new Gson().fromJson(getIntent().getStringExtra("data"), SubjectResponse.class);
            classId = getIntent().getStringExtra("classId");
            binding.clazz.setText(getIntent().getStringExtra("className"));
            binding.subject.setText(subjectResponse.getName());
            binding.description.setText(subjectResponse.getDescription());
            binding.priority.setText("" + subjectResponse.getPriority());
            if (subjectResponse.getSubjectType().getGroupName().isEmpty()) {
                binding.type.setSelection(0);
            } else if (subjectResponse.getSubjectType().getGroupName().equalsIgnoreCase("Other")) {
                binding.type.setSelection(2);
            } else {
                binding.type.setSelection(1);
            }
            if (!subjectResponse.getSubjectType().getGroupName().equalsIgnoreCase("Other")) {
                binding.group.setText(subjectResponse.getSubjectType().getGroupName());
            }
        }

//        ApiCallService.action(this,ApiCallService.Action.ACTION_GET_SUBJECT_LIST);
        binding.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    binding.optionalGroupLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.optionalGroupLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        binding.classLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (update == null) {
                    startActivityForResult(new Intent(getApplicationContext(), ClassListActivity.class), 1);
                }
            }
        });
        binding.optionalGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.clazz.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.please_select_class));
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), GroupListActivity.class);
                intent.putExtra("classId", classId);
                startActivityForResult(intent, 2);
            }
        });

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid = true;

                if (binding.clazz.getText().toString().trim().isEmpty()) {
                    isValid = setError(binding.classLayout, binding.clazz, getString(R.string.please_select_class));
                }
                if (binding.subject.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_subject));
                    isValid = setError(binding.subjectLayout, binding.subject, getString(R.string.enter_subject));

                }
                if (binding.type.getSelectedItemPosition() == 1 && binding.group.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.enter_subject));
                    isValid = setError(binding.optionalGroupLayout, binding.group, getString(R.string.select_group));
                }
                if (isValid) {
                    AddSubjectRequest request = new AddSubjectRequest();
                    request.setClassId(classId);
                    request.setDescription(binding.description.getText().toString().trim());
                    if (subjectResponse != null) {
                        request.setId(subjectResponse.getId());
                    }
                    request.setName(binding.subject.getText().toString().toUpperCase().trim());
                    SubjectType subjectType = new SubjectType();
                    subjectType.setType(Common.Type.valueOf(binding.type.getSelectedItem().toString()));
                    if (binding.type.getSelectedItemPosition() == 0) {
                        subjectType.setGroupName("");
                        request.setId("");
                    } else {
                        subjectType.setGroupName(binding.group.getText().toString());
                        request.setId("");
                    }
                    if (update != null) {
                        request.setId(subjectResponse.getId());
                    }
                    try {
                        request.setPriority(Integer.parseInt(binding.priority.getText().toString()));
                    } catch (Exception e) {
                        request.setPriority(0);
                    }
                    request.setSubjectType(subjectType);
                    apiCallBack(getApiCommonController().addSubject(request));

                } else {
                    dialogError(getString(R.string.please_check_form));
                }

            }
        });
    }


    @Subscribe
    public void addSubject(AddSubjectResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            binding.type.setSelection(0);
            binding.clazz.setText("");
            binding.subject.setText("");
            binding.description.setText("");
//            apiCallBack(getApi().getClassList(new ClassCriteria()));
            if (update == null) {
                dialogSuccess(response.getMessage().getMessage());
            } else {
                finish();
            }

        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                subjectNames.clear();
                subjectIds.clear();
                classId = data.getStringExtra("id");
                binding.clazz.setText(data.getStringExtra("name"));
            }
            if (requestCode == 2) {
                binding.group.setText(data.getStringExtra("name"));
            }
        }
    }

}

