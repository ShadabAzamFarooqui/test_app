package com.app.mschooling.complaint.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.complain.AddComplainReasonRequest;
import com.mschooling.transaction.response.complain.AddComplainReasonResponse;

import org.greenrobot.eventbus.Subscribe;


public class AddComplaintReasonActivity extends BaseActivity {

    EditText reason;
    Button submit;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reason_fragment);
        toolbarClick(getString(R.string.add_complaint_reason));
        reason =findViewById(R.id.reason);
        submit=findViewById(R.id.submit);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reason.getText().toString().trim().isEmpty()) {
                    AddComplainReasonRequest request = new AddComplainReasonRequest();
                    request.setTitle(reason.getText().toString().trim());
                    apiCallBack(getApiCommonController().addReason(request));
                }else {
                    dialogError(getString(R.string.you_have_added_any_reason));
                }
            }
        });
    }


    @Subscribe
    public void addReason(AddComplainReasonResponse response){
        if (response.getStatus().value()==Status.SUCCESS.value()){
            reason.setText("");
            dialogSuccess(response.getMessage().getMessage());
        }else {
            dialogError(response.getMessage().getMessage());        }
    }





}

