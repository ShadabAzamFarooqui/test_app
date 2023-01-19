package com.app.mschooling.class_section_subject.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.class_section_subject.adapter.ClassListExternalAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.student.GetExternalClassResponse;

import org.greenrobot.eventbus.Subscribe;

public class ClassExternalListActivity extends BaseActivity {

    LinearLayout noRecord;
    EditText search;
    RecyclerView recycler_view;
    ClassListExternalAdapter adapter;

    Button submit;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);
        search = findViewById(R.id.search);
        recycler_view = findViewById(R.id.recycler_view);
        noRecord = findViewById(R.id.noRecord);
        submit = findViewById(R.id.submit);
        submit.setVisibility(View.GONE);
        toolbarClick(getString(R.string.classes));


        apiCallBack(getApiCommonController().getClassExternalList(getIntent("schoolId")));
    }


    @Subscribe
    public void timeout(String msg) {

    }


    @Subscribe
    public void getClassList(GetExternalClassResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            recycler_view.setHasFixedSize(true);
            recycler_view.setFocusable(false);
            recycler_view.setNestedScrollingEnabled(false);
            recycler_view.setLayoutManager(new GridLayoutManager(this, 1));
            adapter = new ClassListExternalAdapter(this, response.getExternalClassResponses());
            recycler_view.setAdapter(adapter);

        } else {
            dialogFailed( response.getMessage().getMessage());
        }
    }

}





