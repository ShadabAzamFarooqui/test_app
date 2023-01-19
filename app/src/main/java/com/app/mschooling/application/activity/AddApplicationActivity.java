package com.app.mschooling.application.activity;


import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.application.AddApplicationRequest;
import com.mschooling.transaction.response.application.AddApplicationResponse;
import com.mschooling.transaction.response.application.GetApplicationReasonResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class AddApplicationActivity extends BaseActivity {

    EditText title;
    EditText enterReason;
    Spinner reason;
    EditText application;
    Button submit;
    LinearLayout subjectLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_application);
        toolbarClick(getString(R.string.write_application_w));
        title = findViewById(R.id.tittle);
        enterReason = findViewById(R.id.enterReason);
        reason = findViewById(R.id.reason);
        application = findViewById(R.id.notice);
        submit = findViewById(R.id.submit);
        subjectLayout = findViewById(R.id.subjectLayout);
        apiCallBack(getApiCommonController().getApplicationReason());


        reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (reason.getSelectedItem().toString().equals("Other")) {
                    subjectLayout.setVisibility(View.VISIBLE);
                } else {
                    subjectLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reason.getSelectedItem().toString().equals("Other")){
                    if (title.getText().toString().trim().isEmpty()) {
                        dialogError(getString(R.string.please_enter_subject));
                        return;
                    }
                }

                if (application.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.please_write_application));
                    return;
                }


                AddApplicationRequest request = new AddApplicationRequest();
                if (reason.getSelectedItem().toString().equals("Other")) {
                    request.setTitle(title.getText().toString());
                }else {
                    request.setTitle(reason.getSelectedItem().toString());
                }
                request.setContent(application.getText().toString());
                apiCallBack(getApiCommonController().addApplication(request));
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Subscribe
    public void getApplicationReason(GetApplicationReasonResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
//            List<String> applicationResponseList = Optional.ofNullable(response.getApplicationReasonResponses()).orElseGet(ArrayList::new).stream().map(e -> e.getTitle()).collect(Collectors.toList());

            List<String> reasonList=new ArrayList<>();
            for (int i=0;i<response.getApplicationReasonResponses().size();i++){
                reasonList.add(response.getApplicationReasonResponses().get(i).getTitle());
            }
            reasonList.add("Other");
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                    (AddApplicationActivity.this, android.R.layout.simple_spinner_item, reasonList); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                    .simple_spinner_dropdown_item);
            reason.setAdapter(spinnerArrayAdapter);
        } else {
            dialogError(response.getMessage().getMessage());        }
    }

    @Subscribe
    public void addApplication(AddApplicationResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            dialogSuccessFinish( response.getMessage().getMessage());
        } else {
            dialogFailed( response.getMessage().getMessage());
        }
    }

}

