package com.app.mschooling.students.profile.fragment;


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
import com.app.mschooling.event_handler.EventToolbar;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.student.request.StudentPersonalRequest;
import com.mschooling.transaction.common.student.response.StudentPersonalResponse;
import com.mschooling.transaction.request.profile.UpdateStudentProfileRequest;
import com.mschooling.transaction.response.profile.GetStudentProfileResponse;
import com.mschooling.transaction.response.profile.UpdateStudentProfileResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentProfilePersonalFragment extends BaseFragment {

    GetStudentProfileResponse response;
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

    boolean update;

    public StudentProfilePersonalFragment() {
    }

    public void init(GetStudentProfileResponse response, boolean update) {
        if (response.getStudentPersonalResponse() == null) {
            response.setStudentPersonalResponse(new StudentPersonalResponse());
        }
        this.update = update;
        this.response = response;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_personal_detail, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, update);
        StudentPersonalResponse studentPersonalResponse = response.getStudentPersonalResponse();
        fatherName.setText(studentPersonalResponse.getfName());
        fatherMobile.setText(studentPersonalResponse.getfMobile());
        fatherOccupation.setText(studentPersonalResponse.getfOccupation());
        fatherQualification.setText(studentPersonalResponse.getfQualification());
        fatherIncome.setText(studentPersonalResponse.getfAnnualIncome());


        motherName.setText(studentPersonalResponse.getmName());
        motherMobile.setText(studentPersonalResponse.getmMobile());
        motherOccupation.setText(studentPersonalResponse.getmOccupation());
        motherQualification.setText(studentPersonalResponse.getmQualification());
        motherIncome.setText(studentPersonalResponse.getmAnnualIncome());


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fatherName.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_father_name));
                    return;
                }
                if (fatherMobile.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_fathers_mobile));
                    return;
                }
                if (motherName.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_mother_name));
                    return;
                }


                StudentPersonalRequest studentPersonalRequest = new StudentPersonalRequest();
                studentPersonalRequest.setfName(fatherName.getText().toString());
                studentPersonalRequest.setfMobile(fatherMobile.getText().toString());
                studentPersonalRequest.setfOccupation(fatherOccupation.getText().toString());
                studentPersonalRequest.setfQualification(fatherQualification.getText().toString());
                studentPersonalRequest.setfAnnualIncome(fatherIncome.getText().toString());
                studentPersonalRequest.setmName(motherName.getText().toString());
                studentPersonalRequest.setmMobile(motherMobile.getText().toString());
                studentPersonalRequest.setmOccupation(motherOccupation.getText().toString());
                studentPersonalRequest.setmQualification(motherQualification.getText().toString());
                studentPersonalRequest.setmAnnualIncome(motherIncome.getText().toString());
                UpdateStudentProfileRequest request = new UpdateStudentProfileRequest();
                request.setStudentPersonalRequest(studentPersonalRequest);
                apiCallBack(getApiCommonController().updateStudentProfile(request));
            }
        });

        return view;
    }


    @Subscribe
    public void uploadResponse(UpdateStudentProfileResponse updateStudentProfileResponse) {
        if (Status.SUCCESS.value() == updateStudentProfileResponse.getStatus().value()) {
            StudentProfileAddressFragment studentProfileAddressFragment=new StudentProfileAddressFragment();
            studentProfileAddressFragment.init(response,update);
            fragmentSwitching(studentProfileAddressFragment);
            EventBus.getDefault().post(new EventToolbar(getString(R.string.address)));
        } else {
            dialogError(updateStudentProfileResponse.getMessage().getMessage());
        }
    }


}

