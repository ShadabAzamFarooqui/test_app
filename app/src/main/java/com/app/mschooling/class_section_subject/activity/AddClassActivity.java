package com.app.mschooling.class_section_subject.activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.class_section_subject.adapter.AddSectionAdapter;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityAddClassBinding;
import com.app.mschooling.utils.AppUser;
import com.google.gson.Gson;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.student.AddClassRequest;
import com.mschooling.transaction.request.student.AddSectionRequest;
import com.mschooling.transaction.response.student.AddClassResponse;
import com.mschooling.transaction.response.student.ClassResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class AddClassActivity extends BaseActivity {

    ActivityAddClassBinding binding;

    AddSectionAdapter addSectionAdapter;
    List<AddSectionRequest> sectionList;
    String id;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_class);
        sectionList = new ArrayList<>();
        id = getIntent().getStringExtra("id");
        if (id == null) {
            toolbarClick(getString(R.string.add_class));
            AddSectionRequest section = new AddSectionRequest();
            section.setDescription("Default");
            section.setName("Default");
            sectionList.add(section);
        } else {
            toolbarClick(getString(R.string.update_class));
            ClassResponse classResponse = new Gson().fromJson(getIntent().getStringExtra("data"), ClassResponse.class);
            for (int i = 0; i < classResponse.getSectionResponses().size(); i++) {
                AddSectionRequest request = new AddSectionRequest();
                request.setId(classResponse.getSectionResponses().get(i).getId());
                request.setName(classResponse.getSectionResponses().get(i).getName());
                request.setDescription(classResponse.getSectionResponses().get(i).getName());
                request.setPriority(classResponse.getSectionResponses().get(i).getPriority());
                request.setEnable(false);
                sectionList.add(request);
            }

            binding.clazz.setText(classResponse.getName());
            binding.description.setText(classResponse.getDescription());
            binding.priority.setText("" + classResponse.getPriority());
        }

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                boolean isValid = true;

                if (binding.clazz.getText().toString().trim().isEmpty()) {
                    isValid=setError(binding.classLayout,binding.clazz,getString(R.string.enter_class_w));
                }
                for (int i = 0; i < sectionList.size(); i++) {
                    if (sectionList.get(i).getName().trim().isEmpty()) {
                        sectionList.get(i).setError(true);
                        isValid=false;
                        addSectionAdapter.notifyDataSetChanged();
                    }
                }

                if (isValid){
                    AddClassRequest request = new AddClassRequest();
                    request.setDescription(binding.description.getText().toString().trim());
                    if (binding.priority.getText().toString().trim().isEmpty()) {
                        request.setPriority(0);
                    } else {
                        request.setPriority(Integer.parseInt(binding.priority.getText().toString().trim()));
                    }

                    request.setName(binding.clazz.getText().toString().toUpperCase().trim());
                    request.setId(id);

                    request.setAddSectionRequests(sectionList);
                    apiCallBack(getApiCommonController().addClass(request));
                }else {
                    dialogError(getString(R.string.please_check_form));
                }
               

            }
        });
        sectionViewAction();
    }

    void sectionViewAction() {


        binding.sectionRecyclerView.setHasFixedSize(true);
        binding.sectionRecyclerView.setFocusable(false);
        binding.sectionRecyclerView.setNestedScrollingEnabled(false);
        binding.sectionRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        binding.addSectionLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if (sectionList.size() == 6) {
                    dialogError(getString(R.string.cant_create_more_section));
                    return;
                }
                AddSectionRequest score = new AddSectionRequest();
                score.setDescription("");
                score.setName("");
                score.setEnable(true);
                sectionList.add(score);
                addSectionAdapter.notifyDataSetChanged();
            }
        });

        addSectionAdapter = new AddSectionAdapter(this, sectionList);
        binding.sectionRecyclerView.setAdapter(addSectionAdapter);

    }


    @Subscribe
    public void addClass(AddClassResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            sectionList.clear();
            AddSectionRequest section = new AddSectionRequest();
            section.setDescription("Default");
            section.setName("Default");
            sectionList.add(section);
            sectionViewAction();
            binding.clazz.setText("");
            binding.description.setText("");
            binding.priority.setText("");
            binding.clazz.requestFocus();
            AppUser.getInstance().setClassResponse(null);
            if (id == null) {
                dialogSuccess(response.getMessage().getMessage());
            } else {
                finish();
            }
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


}

