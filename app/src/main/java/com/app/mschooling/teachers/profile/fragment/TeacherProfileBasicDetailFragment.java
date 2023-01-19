package com.app.mschooling.teachers.profile.fragment;


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
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.teacher.request.TeacherBasicRequest;
import com.mschooling.transaction.common.teacher.response.TeacherBasicResponse;
import com.mschooling.transaction.request.profile.UpdateTeacherProfileRequest;
import com.mschooling.transaction.response.profile.GetTeacherProfileResponse;
import com.mschooling.transaction.response.student.UpdateStudentResponse;
import com.mschooling.transaction.response.teacher.UpdateTeacherResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeacherProfileBasicDetailFragment extends BaseFragment {

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


    TeacherBasicRequest mRequest = new TeacherBasicRequest();
    GetTeacherProfileResponse response;
    boolean update;

    public TeacherProfileBasicDetailFragment(GetTeacherProfileResponse response, boolean update) {
        this.response = response;
        this.update = update;
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.basic_teacher_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, update);

        if (response.getTeacherBasicResponse() == null) {
            response.setTeacherBasicResponse(new TeacherBasicResponse());
        }
        TeacherBasicResponse teacherBasicResponse = response.getTeacherBasicResponse();

        mRequest.setEnrollmentId(teacherBasicResponse.getEnrollmentId());


        firstName.setText(teacherBasicResponse.getfName());
        lastName.setText(teacherBasicResponse.getlName());
        mobile.setText(teacherBasicResponse.getMobile());
        dob.setText(teacherBasicResponse.getDob());
        email.setText(teacherBasicResponse.getEmail());
        fatherName.setText(teacherBasicResponse.getFather());
        if (teacherBasicResponse.getGender().value().equals(Common.Gender.Male.value())) {
            gender.setSelection(0);
        }
        if (teacherBasicResponse.getGender().value().equals(Common.Gender.Female.value())) {
            gender.setSelection(1);
        } else {
            gender.setSelection(2);
        }
        dob.setText(teacherBasicResponse.getDob());


        dobLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog(dob, dob);
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
                mRequest.setfName(firstName.getText().toString());
                mRequest.setlName(lastName.getText().toString());
                mRequest.setMobile(mobile.getText().toString());
                mRequest.setEmail(email.getText().toString());
                mRequest.setFather(fatherName.getText().toString());
                mRequest.setDob(dob.getText().toString());
                mRequest.setGender(Common.Gender.valueOf(gender.getSelectedItem().toString()));

                UpdateTeacherProfileRequest request = new UpdateTeacherProfileRequest();
                request.setTeacherBasicRequest(mRequest);
                apiCallBack(getApiCommonController().updateTeacherProfile(request));
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
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }


    }

}

