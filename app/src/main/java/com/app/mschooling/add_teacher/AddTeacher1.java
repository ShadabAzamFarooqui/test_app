package com.app.mschooling.add_teacher;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.teacher.AddTeacherRequest;
import com.mschooling.transaction.response.teacher.AddTeacherResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddTeacher1 extends BaseActivity {

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

    String mob;
    AddTeacherRequest  mRequest=new AddTeacherRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher1);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.sign_up));


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
                mob=mobile.getText().toString();
                mRequest.setfName(firstName.getText().toString());
                mRequest.setlName(lastName.getText().toString());
                mRequest.setMobile(mobile.getText().toString());
                mRequest.setEmail(email.getText().toString());
                mRequest.setDob(dob.getText().toString());
                mRequest.setFather(fatherName.getText().toString());
                mRequest.setGender(Common.Gender.valueOf(gender.getSelectedItem().toString()));
                apiCallBack(getApiCommonController().addTeacher(mRequest));
            }
        });

    }






    @Subscribe
    public void addTeacher(AddTeacherResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())){

            firstName.setText("");
            lastName.setText("");
            mobile.setText("");
            email.setText("");
            fatherName.setText("");
            dob.setText("");
            gender.setSelection(0);



            Dialog dialog = new Dialog(this);
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_add_more);
            dialog.setCancelable(false);
            TextView message = dialog.findViewById(R.id.message);
            TextView addMore = dialog.findViewById(R.id.addMore);
            TextView cancel = dialog.findViewById(R.id.cancel);
            message.setText(response.getMessage().getMessage());
            addMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), AddTeacherProfileImageActivity.class);
                    intent.putExtra("id", response.getEnrollmentId());
                    intent.putExtra("mobile", mob);
                    startActivity(intent);
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }else {
            dialogError(response.getMessage().getMessage());
        }


    }

}
