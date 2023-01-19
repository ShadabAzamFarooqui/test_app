package com.app.mschooling.class_section_subject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.class_section_subject.adapter.ClassListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.section.GetStandaloneClassResponse;
import com.mschooling.transaction.response.student.ClassResponse;

import org.greenrobot.eventbus.Subscribe;

public class AppendedClassListActivity extends BaseActivity {

    LinearLayout noRecord;
    EditText search;
    RecyclerView recycler_view;
    ClassListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list_appended);
        search = findViewById(R.id.search);
        recycler_view = findViewById(R.id.recycler_view);
        noRecord = findViewById(R.id.noRecord);
        toolbarClick(getString(R.string.classes));

        apiCallBack(getApiCommonController().getClassList(ParameterConstant.getRole(this)));

    }



    @Subscribe
    public void getClassList(GetStandaloneClassResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            recycler_view.setHasFixedSize(true);
            recycler_view.setFocusable(false);
            recycler_view.setNestedScrollingEnabled(false);
            recycler_view.setLayoutManager(new GridLayoutManager(this, 1));
            ClassResponse classResponse=new ClassResponse();
            classResponse.setName(getString(R.string.public_resource));
            classResponse.setDescription(getString(R.string.defaults));
            response.getClassResponses().add(0,classResponse);
            adapter = new ClassListAdapter(this, response.getClassResponses(), getIntent("intent"));
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





