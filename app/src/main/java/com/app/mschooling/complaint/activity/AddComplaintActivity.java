package com.app.mschooling.complaint.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.complain.AddComplainRequest;
import com.mschooling.transaction.response.complain.AddComplainResponse;
import com.mschooling.transaction.response.complain.GetComplainReasonResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class AddComplaintActivity extends BaseActivity {
    List reasonList;
    Spinner reason_spinner;
    LinearLayout reasonLayout;
    EditText message,other_reason;
    Button submit;
    String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_complaint_activity);
        toolbarClick(getString(R.string.add_omplaint));
        reason_spinner=findViewById(R.id.reason_spinner);
        message=findViewById(R.id.message);
        other_reason=findViewById(R.id.other_reason);
        reasonLayout=findViewById(R.id.reasonLayout);
        submit=findViewById(R.id.submit);
        reasonList = new ArrayList();
        apiCallBack(getApiCommonController().complainReasonList());


        reason_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position==reasonList.size()-1){
                    reasonLayout.setVisibility(View.VISIBLE);
                }else {
                    reasonLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reason_spinner.getSelectedItemPosition()==0) {
                    dialogError(getString(R.string.please_select_reason));
                    return;
                }

                if (reason_spinner.getSelectedItemPosition()==reasonList.size()-1){
                    if (other_reason.getText().toString().trim().isEmpty()) {
                        dialogError(getString(R.string.please_select_reason));
                        return;
                    }
                }
                if (message.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.please_enter_message));
                    return;
                }
                AddComplainRequest addComplainRequest = new AddComplainRequest();
                if (reason_spinner.getItemAtPosition(reasonList.size()-1).equals(title)){
                    addComplainRequest.setTitle(other_reason.getText().toString());
                }else {
                    addComplainRequest.setTitle(reason_spinner.getSelectedItem().toString());
                }
                addComplainRequest.setContent(message.getText().toString());
                apiCallBack(getApiCommonController().addComplain(addComplainRequest));
            }
        });

    }

    @Subscribe
    public void getReason(GetComplainReasonResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
           // getDialog("Success", "Notice has been posted successfully", false, false);
            reasonList = new ArrayList();
            reasonList.add(getString(R.string.select_reason));
            for (int i=0;i<response.getComplainReasonResponses().size();i++){
                reasonList.add(response.getComplainReasonResponses().get(i).getTitle());
            }
            reasonList.add(getString(R.string.other));

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                    (AddComplaintActivity.this, android.R.layout.simple_spinner_item,reasonList); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                    .simple_spinner_dropdown_item);
            reason_spinner.setAdapter(spinnerArrayAdapter);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    @Subscribe
    public void addComplain(AddComplainResponse response) {
        if (response.getStatus().value()== Status.SUCCESS.value()) {
            reason_spinner.setSelection(0);
            message.setText("");
            other_reason.setText("");
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }
}

