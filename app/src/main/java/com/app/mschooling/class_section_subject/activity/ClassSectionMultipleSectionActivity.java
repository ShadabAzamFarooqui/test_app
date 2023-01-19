package com.app.mschooling.class_section_subject.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.class_section_subject.adapter.ClassSectionMultipleBaseAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.section.GetStandaloneClassResponse;
import com.mschooling.transaction.response.student.ClassResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;


public class ClassSectionMultipleSectionActivity extends BaseActivity {

    LinearLayout noRecord;
    EditText search;
    RecyclerView recyclerView;
    ClassSectionMultipleBaseAdapter adapter;
    List<ClassResponse> responseList;
    Button submit;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_section_multiple_selection_activity);
        toolbarClick(getString(R.string.select_class));
        submit = findViewById(R.id.submit);
        search = findViewById(R.id.search);
        recyclerView = findViewById(R.id.recyclerView);
        noRecord = findViewById(R.id.noRecord);
        apiCallBack(getApiCommonController().getClassList(ParameterConstant.getRole(this)));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSelection();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Subscribe
    public void getClassList(GetStandaloneClassResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            responseList = response.getClassResponses();
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

//            ClassResponse classResponse = new ClassResponse();
//            classResponse.setSectionResponses(new ArrayList<>());
//            classResponse.setName("ALL CLASSES");
//            classResponse.setDescription("ALL SECTIONS");
//            response.getClassResponses().add(0, classResponse);
            adapter = new ClassSectionMultipleBaseAdapter(this, responseList);
            recyclerView.setAdapter(adapter);

            if (response.getClassResponses().size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                noRecord.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    void checkSelection() {
        boolean selected = false;
        String names = "";
        String ids = "";
        for (int i = 0; i < responseList.size(); i++) {
            for (int j = 0; j < responseList.get(i).getSectionResponses().size(); j++) {
                if (responseList.get(i).getSectionResponses().get(j).isSelected()) {
                    selected = true;
                    ids = ids + responseList.get(i).getSectionResponses().get(j).getId() + ",";
                    if (names.contains(responseList.get(i).getName())) {
                        names = names.concat("," + responseList.get(i).getSectionResponses().get(j).getName());
                    } else {
                        if (names.isEmpty()) {
                            names = names.concat(responseList.get(i).getName() + "(" + responseList.get(i).getSectionResponses().get(j).getName());
                        } else {
                            names = names.concat(")\n" + responseList.get(i).getName() + "(" + responseList.get(i).getSectionResponses().get(j).getName());
                        }
                    }
                }
            }
        }

        if (!selected) {
            dialogError(getString(R.string.Ppease_select_at_least_class_section));
        } else {
//            if (responseList.get(0).isSelected()) {
//                Intent intent = new Intent();
//                intent.putExtra("ids", ids);
//                intent.putExtra("names", "All classes and sections");
//                setResult(RESULT_OK, intent);
//                finish();
//            } else {
                names = names + ")";
                Intent intent = new Intent();
                intent.putExtra("ids", ids);
                intent.putExtra("names", names);
                setResult(RESULT_OK, intent);
                finish();
//            }
        }
    }

}

