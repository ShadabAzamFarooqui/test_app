package com.app.mschooling.add_student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.student.request.StudentPersonalRequest;
import com.mschooling.transaction.request.student.UpdateStudentRequest;
import com.mschooling.transaction.response.student.UpdateStudentResponse;

import org.greenrobot.eventbus.Subscribe;

public class AddStudent2 extends BaseActivity {

    Button submit;


    EditText fatherName;
    EditText fatherMobile;
    EditText fatherOccupation;
    EditText fatherQualification;
    EditText fatherIncome;


    EditText motherName;
    EditText motherMobile;
    EditText motherOccupation;
    EditText motherQualification;
    EditText motherIncome;
    LinearLayout skipLayout;

    UpdateStudentRequest mRequest;
    public static AddStudent2 context;


    void init() {
        submit = findViewById(R.id.submit);
        fatherName = findViewById(R.id.fatherName);
        fatherMobile = findViewById(R.id.fatherMobile);
        fatherOccupation = findViewById(R.id.fatherOccupation);
        fatherQualification = findViewById(R.id.fatherQualification);
        fatherIncome = findViewById(R.id.fatherIncome);

        motherName = findViewById(R.id.motherName);
        motherMobile = findViewById(R.id.motherMobile);
        motherOccupation = findViewById(R.id.motherOccupation);
        motherQualification = findViewById(R.id.motherQualification);
        motherIncome = findViewById(R.id.motherIncome);
        skipLayout = findViewById(R.id.skipLayout);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_student2);
        toolbarClick(getString(R.string.tool_parents_detail));
        init();
        context = this;
        mRequest = new UpdateStudentRequest();
        mRequest.getStudentBasicRequest().setEnrollmentId(getIntent().getStringExtra("id"));

        fatherMobile.setText(getIntent().getStringExtra("mobile"));

        skipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),AddStudent3.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (fatherName.getText().toString().isEmpty()) {
                    alerter("Enter father name");
                    return;
                }
                if (fatherMobile.getText().toString().isEmpty()) {
                    alerter("Enter mobile number");
                    return;
                }
                if (fatherOccupation.getText().toString().isEmpty()) {
                    alerter("Enter father occupation");
                    return;
                }
                if (fatherQualification.getText().toString().isEmpty()) {
                    alerter("Enter father qualification");
                    return;
                }
                if (fatherIncome.getText().toString().isEmpty()) {
                    alerter("Enter father annual income");
                    return;
                }
                if (motherName.getText().toString().isEmpty()) {
                    alerter("Enter mother name");
                    return;
                }
                if (motherOccupation.getText().toString().isEmpty()) {
                    alerter("Enter mother occupation");
                    return;
                }
                if (motherQualification.getText().toString().isEmpty()) {
                    alerter("Enter mother qualification");
                    return;
                }
                if (motherIncome.getText().toString().isEmpty()) {
                    alerter("Enter mother annual income");
                    return;
                }*/
                StudentPersonalRequest request = mRequest.getStudentPersonalRequest();
                request.setfName(fatherName.getText().toString());
                request.setfMobile(fatherMobile.getText().toString());
                request.setfOccupation(fatherOccupation.getText().toString());
                request.setfQualification(fatherQualification.getText().toString());
                request.setfAnnualIncome(fatherIncome.getText().toString());
                request.setmName(motherName.getText().toString());
                request.setmMobile(motherMobile.getText().toString());
                request.setmOccupation(motherOccupation.getText().toString());
                request.setmQualification(motherQualification.getText().toString());
                request.setmAnnualIncome(motherIncome.getText().toString());
                apiCallBack(getApiCommonController().updateStudent(mRequest));
            }
        });

    }


    @Subscribe
    public void updateStudent(UpdateStudentResponse response) {
        if (response.getStatus().value()== Status.SUCCESS.value()) {
            Intent intent = new Intent(getApplicationContext(), AddStudent3.class);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
            startActivity(intent);
        }else {
            dialogError(response.getMessage().getMessage());
        }
    }


}
