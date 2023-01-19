package com.app.mschooling.enrollment.signup.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.mschooling.transaction.request.teacher.AddTeacherRequest;
import com.mschooling.transaction.response.qrcode.GetQRCodeResponse;
import com.mschooling.transaction.response.teacher.AddTeacherResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class TeacherSignUpFragment extends BaseFragment {
    @BindView(R.id.firstName)
    EditText firstName;
    @BindView(R.id.lastName)
    EditText lastName;
    @BindView(R.id.fatherName)
    EditText fatherName;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.gender)
    Spinner gender;
    @BindView(R.id.dobLayout)
    RelativeLayout dobLayout;
    @BindView(R.id.dob)
    TextView dob;
    @BindView(R.id.schoolName)
    TextView schoolName;
    @BindView(R.id.submit)
    Button submit;

    GetQRCodeResponse response;
    AddTeacherRequest mRequest;



    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_sign_up, container, false);
        ButterKnife.bind(this, view);
        imageName = Helper.getRandom();
        response = AppUser.getInstance().getQrCodeDetailResponse();
        schoolName.setText(response.getQrCodeResponse().getSchoolName());



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
                if (fatherName.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_enter_father_husband_name));
                    return;
                }
                mRequest = new AddTeacherRequest();
                mRequest.setfName(firstName.getText().toString());
                mRequest.setlName(lastName.getText().toString());
                mRequest.setMobile(mobile.getText().toString());
                mRequest.setEmail(email.getText().toString());
                mRequest.setDob(dob.getText().toString());
                mRequest.setFather(fatherName.getText().toString());
                mRequest.setGender(Common.Gender.valueOf(gender.getSelectedItem().toString()));
                mRequest.setEncodedSchoolId(response.getQrCodeResponse().getSchoolId());
                apiCallBack(getApiCommonController().signUpTeacher(mRequest));
            }
        });

        return view;
    }


    @Subscribe
    public void timeout(String msg) {

    }


    @Subscribe
    public void addTeacher(AddTeacherResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            dialogSuccessFinish(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


}