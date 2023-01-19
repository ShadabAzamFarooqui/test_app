package com.app.mschooling.students.detail.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.student.request.StudentPersonalRequest;
import com.mschooling.transaction.common.student.response.StudentPersonalResponse;
import com.mschooling.transaction.request.student.UpdateStudentRequest;
import com.mschooling.transaction.response.student.GetStudentDetailResponse;
import com.mschooling.transaction.response.student.UpdateStudentResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonalDetailFragment extends BaseFragment {

    StudentPersonalResponse response;

    @BindView(R.id.fatherName)
    EditText fatherName;
    @BindView(R.id.fatherMobile)
    EditText fatherMobile;
    @BindView(R.id.fatherOccupation)
    EditText fatherOccupation;
    @BindView(R.id.fatherQualification)
    EditText fatherQualification;
    @BindView(R.id.fatherIncome)
    EditText fatherIncome;
    @BindView(R.id.motherName)
    EditText motherName;
    @BindView(R.id.motherMobile)
    EditText motherMobile;
    @BindView(R.id.motherOccupation)
    EditText motherOccupation;
    @BindView(R.id.motherQualification)
    EditText motherQualification;
    @BindView(R.id.motherIncome)
    EditText motherIncome;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    UpdateStudentRequest mRequest=new UpdateStudentRequest();

    String enrollmentId;
    boolean update ;
    public PersonalDetailFragment(GetStudentDetailResponse response,boolean update) {
        this.response = response.getStudentDetailResponse().getStudentPersonalResponse();
        this.enrollmentId = response.getStudentDetailResponse().getStudentBasicResponse().getEnrollmentId();
        this.update = update;
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personal_details, container, false);
        ButterKnife.bind(this,view);
        Helper.enableDisableView(mainLayout, submit, update);


        fatherName.setText(response.getfName());
        fatherMobile.setText(response.getfMobile());
        fatherOccupation.setText(response.getfOccupation());
        fatherQualification.setText(response.getfQualification());
        fatherIncome.setText(response.getfAnnualIncome());

        motherName.setText(response.getmName());
        motherMobile.setText(response.getmMobile());
        motherOccupation.setText(response.getmOccupation());
        motherQualification.setText(response.getmQualification());
        motherIncome.setText(response.getmAnnualIncome());



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
                mRequest.getStudentBasicRequest().setEnrollmentId(enrollmentId);
                StudentPersonalRequest parent = new StudentPersonalRequest();
                parent.setfName(fatherName.getText().toString());
                parent.setfMobile(fatherMobile.getText().toString());
                parent.setfOccupation(fatherOccupation.getText().toString());
                parent.setfQualification(fatherQualification.getText().toString());
                parent.setfAnnualIncome(fatherIncome.getText().toString());
                parent.setmName(motherName.getText().toString());
                parent.setmMobile(motherMobile.getText().toString());
                parent.setmOccupation(motherOccupation.getText().toString());
                parent.setmQualification(motherQualification.getText().toString());
                parent.setmAnnualIncome(motherIncome.getText().toString());
                mRequest.setStudentPersonalRequest(parent);
                apiCallBack(getApiCommonController().updateStudent(mRequest));
            }
        });

        return view;
    }



    @Subscribe
    public void updateStudent(UpdateStudentResponse response) {
        if (response.getStatus().value()== Status.SUCCESS.value()) {
            dialogSuccess(response.getMessage().getMessage());
        }else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

}

