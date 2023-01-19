package com.app.mschooling.teachers.detail.fragment;

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
import com.mschooling.transaction.common.teacher.request.TeacherBankRequest;
import com.mschooling.transaction.common.teacher.response.TeacherBankResponse;
import com.mschooling.transaction.request.teacher.UpdateTeacherRequest;
import com.mschooling.transaction.response.teacher.GetTeacherDetailResponse;
import com.mschooling.transaction.response.teacher.UpdateTeacherResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BankDetailFragment extends BaseFragment {


    UpdateTeacherRequest mRequest=new UpdateTeacherRequest();
    TeacherBankResponse response;
    String enrollmentId;

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
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;
    @BindView(R.id.submit)
    Button submit;
 @BindView(R.id.skipLayout)
    LinearLayout skipLayout;

    public BankDetailFragment(GetTeacherDetailResponse response){
        this.response=response.getTeacherDetailResponse().getTeacherBankResponse();
        this.enrollmentId=response.getTeacherDetailResponse().getTeacherBasicResponse().getEnrollmentId();
    }
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bank_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, AppUser.getInstance().isUpdate());
        skipLayout.setVisibility(View.GONE);
        mRequest.getTeacherBasicRequest().setEnrollmentId(enrollmentId);

        accountName.setText(response.getAccountName());
        accountNumber.setText(response.getAccountNumber());
        bankName.setText(response.getBankName());
        bankBranchName.setText(response.getBankBranchName());
        ifsc.setText(response.getIfsc());
        bankAddress.setText(response.getBankAddress());


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherBankRequest bankDetail=new TeacherBankRequest();
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

        return view;
    }


    @Subscribe
    public void updateTeacher(UpdateTeacherResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

}
