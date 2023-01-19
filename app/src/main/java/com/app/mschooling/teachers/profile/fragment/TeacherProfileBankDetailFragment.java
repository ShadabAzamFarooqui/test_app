package com.app.mschooling.teachers.profile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.app.mschooling.home.teacher.activity.TeacherMainActivity;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.teacher.request.TeacherBankRequest;
import com.mschooling.transaction.common.teacher.response.TeacherAddressResponse;
import com.mschooling.transaction.common.teacher.response.TeacherBankResponse;
import com.mschooling.transaction.request.profile.UpdateTeacherProfileRequest;
import com.mschooling.transaction.response.profile.GetTeacherProfileResponse;
import com.mschooling.transaction.response.profile.UpdateTeacherProfileResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeacherProfileBankDetailFragment extends BaseFragment {


    GetTeacherProfileResponse response;
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
    @BindView(R.id.skipLayout)
    LinearLayout skipLayout;
    @BindView(R.id.submit)
    Button submit;
    boolean update;

    public TeacherProfileBankDetailFragment(GetTeacherProfileResponse response, boolean update) {
        if (response.getTeacherAddressResponse() == null) {
            response.setTeacherAddressResponse(new TeacherAddressResponse());
        }
        this.update = update;
        this.response = response;
    }


    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bank_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, update);
        if (response.getTeacherBankResponse()==null){
            response.setTeacherBankResponse(new TeacherBankResponse());
        }
        if (!update){
            skipLayout.setVisibility(View.GONE);
        }

        TeacherBankResponse teacherBankResponse=response.getTeacherBankResponse();
            accountName.setText(teacherBankResponse.getAccountName());
            accountNumber.setText(teacherBankResponse.getAccountNumber());
            bankName.setText(teacherBankResponse.getBankName());
            bankBranchName.setText(teacherBankResponse.getBankBranchName());
            ifsc.setText(teacherBankResponse.getIfsc());
            bankAddress.setText(teacherBankResponse.getBankAddress());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountName.getText().toString().isEmpty()){
                    dialogError(getString(R.string.enter_account_holder_ame));
                    return;
                }
                if (accountNumber.getText().toString().isEmpty()){
                    dialogError(getString(R.string.enter_account_number));
                    return;
                }
                if (bankBranchName.getText().toString().isEmpty()){
                    dialogError(getString(R.string.enter_name_of_the_bank));
                    return;
                }
                if (bankName.getText().toString().isEmpty()){
                    dialogError(getString(R.string.enter_branch_name));
                    return;
                }
                if (bankAddress.getText().toString().isEmpty()){
                    dialogError(getString(R.string.enter_bank_address));
                    return;
                }
                if (ifsc.getText().toString().isEmpty()){
                    dialogError(getString(R.string.enter_ifsc_code));
                    return;
                }
                TeacherBankRequest bankDetail=new TeacherBankRequest();
                bankDetail.setAccountName(accountName.getText().toString());
                bankDetail.setAccountNumber(accountNumber.getText().toString());
                bankDetail.setBankBranchName(bankBranchName.getText().toString());
                bankDetail.setBankName(bankName.getText().toString());
                bankDetail.setBankAddress(bankAddress.getText().toString());
                bankDetail.setIfsc(ifsc.getText().toString());

                UpdateTeacherProfileRequest request=new UpdateTeacherProfileRequest();
                request.setTeacherBankRequest(bankDetail);
                apiCallBack(getApiCommonController().updateTeacherProfile(request));
            }
        });

        skipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.getInstance(getContext()).setProfileComplete(true);
                Intent intent = new Intent(getContext(), TeacherMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }


    @Subscribe
    public void updateTeacher(UpdateTeacherProfileResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            Preferences.getInstance(getContext()).setProfileComplete(true);
            Intent intent = new Intent(getContext(), TeacherMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

}
