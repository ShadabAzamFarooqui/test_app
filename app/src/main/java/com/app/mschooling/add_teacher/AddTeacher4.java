package com.app.mschooling.add_teacher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.teacher.request.TeacherBankRequest;
import com.mschooling.transaction.request.teacher.UpdateTeacherRequest;
import com.mschooling.transaction.response.teacher.UpdateTeacherResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddTeacher4 extends BaseActivity {


    UpdateTeacherRequest mRequest;
    @BindView(R.id.accountName)
    EditText accountName;
    @BindView(R.id.accountNumber)
    EditText accountNumber;
    @BindView(R.id.bankName)
    EditText bankName;
    @BindView(R.id.bankBranchName)
    EditText bankBranchName;
    @BindView(R.id.ifsc)
    EditText ifsc;
    @BindView(R.id.bankAddress)
    EditText bankAddress;
    @BindView(R.id.submit)
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher4);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.tool_bank_details));

        mRequest = new UpdateTeacherRequest();
        mRequest.getTeacherBasicRequest().setEnrollmentId(getIntent().getStringExtra("id"));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherBankRequest bankDetail = new TeacherBankRequest();
                bankDetail.setAccountName(accountName.getText().toString());
                bankDetail.setAccountNumber(accountNumber.getText().toString());
                bankDetail.setBankBranchName(bankBranchName.getText().toString());
                bankDetail.setBankName(bankName.getText().toString());
                bankDetail.setBankAddress(bankAddress.getText().toString());
                bankDetail.setIfsc(ifsc.getText().toString());
                mRequest.setTeacherBankRequest(bankDetail);
                apiCallBack(getApiCommonController().updateTeacher(mRequest));
            }
        });
    }


    @Subscribe
    public void updateTeacher(UpdateTeacherResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.success)
                    .setMessage(response.getMessage().getMessage())
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            AddTeacher2.context.finish();
                            AddTeacherProfileImageActivity.context.finish();
//                            AddTeacher3.context.finish();
                            finish();
                        }
                    })
                    .setIcon(R.drawable.mschooling_logo)
                    .show();
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

}
