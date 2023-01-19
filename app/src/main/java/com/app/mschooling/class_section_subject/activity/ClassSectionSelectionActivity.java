package com.app.mschooling.class_section_subject.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.class_section_subject.adapter.ClassListWithSectionBaseAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.section.GetStandaloneClassResponse;
import com.mschooling.transaction.response.student.SectionResponse;

import org.greenrobot.eventbus.Subscribe;


public class ClassSectionSelectionActivity extends BaseActivity {

    LinearLayout noRecord;
    EditText search;
    RecyclerView recyclerView;
    ClassListWithSectionBaseAdapter adapter;
    String intentString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_class_with_section);
        toolbarClick(getString(R.string.select_class));
        intentString = getIntent("intent");
        search = findViewById(R.id.search);
        recyclerView = findViewById(R.id.recyclerView);
        noRecord = findViewById(R.id.noRecord);
        apiCallBack(getApiCommonController().getClassList(ParameterConstant.getRole(this)));


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Subscribe
    public void getClassList(GetStandaloneClassResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));


            for (int i = 0; i < response.getClassResponses().size(); i++) {
                if (response.getClassResponses().get(i).getSectionResponses().size() == 0) {
                    SectionResponse sectionResponse = new SectionResponse();
                    sectionResponse.setId("");
                    sectionResponse.setName("DEFAULT");
                    sectionResponse.setDescription("");
                    response.getClassResponses().get(i).getSectionResponses().add(sectionResponse);
                }
            }
            adapter = new ClassListWithSectionBaseAdapter(this, intentString, response.getClassResponses());
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
}

