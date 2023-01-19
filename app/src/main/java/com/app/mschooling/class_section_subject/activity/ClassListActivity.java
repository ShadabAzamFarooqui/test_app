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

import com.app.mschooling.class_section_subject.adapter.ClassListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.section.GetStandaloneClassResponse;
import com.mschooling.transaction.response.student.ClassResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class ClassListActivity extends BaseActivity {

    LinearLayout noRecord;
    EditText search;
    RecyclerView recycler_view;
    ClassListAdapter adapter;

    Button submit;

    String whereFrom = "";
    String intentString = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);
        Intent intent = getIntent();
        whereFrom = intent.getStringExtra("whereFrom");
        search = findViewById(R.id.search);
        recycler_view = findViewById(R.id.recycler_view);
        noRecord = findViewById(R.id.noRecord);
        submit = findViewById(R.id.submit);
        submit.setVisibility(View.GONE);
        toolbarClick(getString(R.string.classes));

        intentString = getIntent().getStringExtra("intent");

        apiCallBack(getApiCommonController().getClassList(ParameterConstant.getRole(this)));

    }


    @Subscribe
    public void timeout(String msg) {

    }


    @Subscribe
    public void getClassList(GetStandaloneClassResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            AppUser.getInstance().setClassResponseList(response.getClassResponses());
            recycler_view.setHasFixedSize(true);
            recycler_view.setFocusable(false);
            recycler_view.setNestedScrollingEnabled(false);
            recycler_view.setLayoutManager(new GridLayoutManager(this, 1));
            adapter = new ClassListAdapter(this, response.getClassResponses(), intentString);
            recycler_view.setAdapter(adapter);

            if (AppUser.getInstance().getClassResponseList().size() == 0) {
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra("id", data.getStringExtra("id"));
            intent.putExtra("name", data.getStringExtra("name"));
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}





