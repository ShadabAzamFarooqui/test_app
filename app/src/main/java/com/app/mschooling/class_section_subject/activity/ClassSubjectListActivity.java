package com.app.mschooling.class_section_subject.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.class_section_subject.adapter.ClassSubjectBaseAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.network.pojo.SubjectResponseModel;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.filter.ClassCriteria;
import com.mschooling.transaction.request.subject.SubjectType;
import com.mschooling.transaction.response.student.ClassResponse;
import com.mschooling.transaction.response.student.GetClassResponse;
import com.mschooling.transaction.response.subject.SubjectResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class ClassSubjectListActivity extends BaseActivity {

    LinearLayout noRecord;
    EditText search;
    RecyclerView recycler_view;
    LinearLayout add;
    ClassSubjectBaseAdapter adapter;
    List<ClassResponse> responseList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_list);
        search = findViewById(R.id.search);
        recycler_view = findViewById(R.id.recycler_view);
        noRecord = findViewById(R.id.noRecord);
        add = findViewById(R.id.add);
        toolbarClick(getString(R.string.subjects));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddSubjectActivity.class));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().getClassList(new ClassCriteria()));
    }

    @Subscribe
    public void getClassList(GetClassResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            responseList = response.getClassResponses();
            recycler_view.setHasFixedSize(true);
            recycler_view.setFocusable(false);
            recycler_view.setNestedScrollingEnabled(false);
            recycler_view.setLayoutManager(new GridLayoutManager(this, 1));

            for (int i = 0; i < response.getClassResponses().size(); i++) {
                for (int j = 0; j < response.getClassResponses().get(i).getSubjectResponses().size(); j++) {
                    if (response.getClassResponses().get(i).getSubjectResponses().get(j).getSubjectType() == null) {
                        SubjectType subjectType = new SubjectType();
                        subjectType.setGroupName("");
                        response.getClassResponses().get(i).getSubjectResponses().get(j).setSubjectType(subjectType);
                    } else if (response.getClassResponses().get(i).getSubjectResponses().get(j).getSubjectType().getGroupName() == null) {
                        response.getClassResponses().get(i).getSubjectResponses().get(j).getSubjectType().setGroupName("");
                    }
                }
            }


//            ClassResponse temp=response.getClassResponses()().get(0);
//            response.getClassResponses()().clear();
//            response.getClassResponses()().add(temp);

            List<SubjectResponseModel> subjectResponseModelList = new ArrayList<>();
            for (int a = 0; a < response.getClassResponses().size(); a++) {
                List<SubjectResponse> mandatoryList = responseList.get(a).getSubjectResponses();
                List<SubjectResponseModel.OptionSubject> optional = new ArrayList<>();
                List<SubjectResponse> optionalSubjectList = new ArrayList<>();

                try {
                    Collections.sort(mandatoryList, new Comparator() {
                        @Override
                        public int compare(Object a1, Object a2) {
                            final SubjectResponse app1 = (SubjectResponse) a1;
                            final SubjectResponse app2 = (SubjectResponse) a2;
                            return app1.getSubjectType().getGroupName().compareTo(app2.getSubjectType().getGroupName());
                        }
                    });


                    for (int i = 0; i < mandatoryList.size(); i++) {
                        if (!mandatoryList.get(i).getSubjectType().getGroupName().isEmpty()) {
                            optionalSubjectList.add(mandatoryList.get(i));
                        }
                    }


                    for (int i = 0; i < mandatoryList.size(); i++) {
                        if (!mandatoryList.get(i).getSubjectType().getGroupName().isEmpty()) {
                            mandatoryList.remove(i);
                            i = 0;
                        }
                    }


                    Set<String> set = new HashSet<>();
                    for (int i = 0; i < optionalSubjectList.size(); i++) {
                        set.add(optionalSubjectList.get(i).getSubjectType().getGroupName());
                    }


                    Iterator<String> iterator = set.iterator();
                    while (iterator.hasNext()) {
                        String value = iterator.next();
                        List<SubjectResponse> tempList = new ArrayList<>();
                        for (int i = 0; i < optionalSubjectList.size(); i++) {
                            if (value.equals(optionalSubjectList.get(i).getSubjectType().getGroupName())) {
                                tempList.add(optionalSubjectList.get(i));
                            }
                        }
                        SubjectResponseModel.OptionSubject optionSubject = new SubjectResponseModel.OptionSubject();
                        optionSubject.setGroupName(tempList.get(0).getSubjectType().getGroupName());
                        optionSubject.setOptionalSubject(tempList);
                        optional.add(optionSubject);
                    }

                    SubjectResponseModel subjectResponseModel = new SubjectResponseModel();
                    subjectResponseModel.setClassId(response.getClassResponses().get(a).getId());
                    subjectResponseModel.setClassName(response.getClassResponses().get(a).getName());
                    subjectResponseModel.setDescription(response.getClassResponses().get(a).getDescription());
                    subjectResponseModel.setMandatorySubject(mandatoryList);
                    subjectResponseModel.setOptional(optional);
                    subjectResponseModelList.add(subjectResponseModel);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.printf("http sorted map " + subjectResponseModelList);
            adapter = new ClassSubjectBaseAdapter(this, subjectResponseModelList);
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





}





