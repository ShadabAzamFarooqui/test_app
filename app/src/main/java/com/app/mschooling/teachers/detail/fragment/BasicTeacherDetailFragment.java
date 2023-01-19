package com.app.mschooling.teachers.detail.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.teacher.request.TeacherBasicRequest;
import com.mschooling.transaction.common.teacher.response.TeacherBasicResponse;
import com.mschooling.transaction.request.teacher.UpdateTeacherRequest;
import com.mschooling.transaction.response.student.UpdateStudentResponse;
import com.mschooling.transaction.response.teacher.GetTeacherDetailResponse;
import com.mschooling.transaction.response.teacher.UpdateTeacherResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BasicTeacherDetailFragment extends BaseFragment {

    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;
    @BindView(R.id.firstName)
    EditText firstName;
    @BindView(R.id.lastName)
    EditText lastName;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.fatherName)
    EditText fatherName;
    @BindView(R.id.gender)
    Spinner gender;
    @BindView(R.id.dobLayout)
    RelativeLayout dobLayout;
    @BindView(R.id.dob)
    TextView dob;
    @BindView(R.id.submit)
    Button submit;


    UpdateTeacherRequest mRequest = new UpdateTeacherRequest();
    TeacherBasicResponse response;
    String enrollmentId;

    public BasicTeacherDetailFragment(GetTeacherDetailResponse response) {
        this.response = response.getTeacherDetailResponse().getTeacherBasicResponse();
        this.enrollmentId = response.getTeacherDetailResponse().getTeacherBasicResponse().getEnrollmentId();
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.basic_teacher_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, AppUser.getInstance().isUpdate());
        mRequest.getTeacherBasicRequest().setEnrollmentId(response.getEnrollmentId());


        firstName.setText(response.getfName());
        lastName.setText(response.getlName());
        mobile.setText(response.getMobile());
        dob.setText(response.getDob());
        email.setText(response.getEmail());
        fatherName.setText(response.getFather());
        if (response.getGender().value().equals(Common.Gender.Male.value())) {
            gender.setSelection(0);
        }else if (response.getGender().value().equals(Common.Gender.Female.value())) {
            gender.setSelection(1);
        } else {
            gender.setSelection(2);
        }
        dob.setText(response.getDob());






        dobLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog(dob,dob);
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (firstName.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_enter_name));
                    return;
                }
                if (mobile.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_mobile_number));
                    return;
                }
                if (mobile.getText().toString().length() != 10) {
                    dialogError(getString(R.string.please_valid_mobile_number));
                    return;
                }
                if (dob.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_select_dob));
                    return;
                }
                TeacherBasicRequest request=new TeacherBasicRequest();
                request.setEnrollmentId(enrollmentId);
                request.setfName(firstName.getText().toString());
                request.setlName(lastName.getText().toString());
                request.setMobile(mobile.getText().toString());
                request.setEmail(email.getText().toString());
                request.setFather(fatherName.getText().toString());
                request.setDob(dob.getText().toString());
                request.setGender(Common.Gender.valueOf(gender.getSelectedItem().toString()));
                mRequest.setTeacherBasicRequest(request);
                apiCallBack(getApiCommonController().updateTeacher(mRequest));
            }
        });


        return view;
    }

    @Subscribe
    public void updateStudent(UpdateStudentResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }



    @Subscribe
    public void addTeacher(UpdateTeacherResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())){
            dialogSuccess(response.getMessage().getMessage());
        }else {
            dialogFailed(response.getMessage().getMessage());
        }


    }

}

