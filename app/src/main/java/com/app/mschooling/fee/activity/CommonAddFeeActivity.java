package com.app.mschooling.fee.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityFeeAddBinding;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.fee.AddFeeSetupRequest;
import com.mschooling.transaction.response.fee.AddFeeSetupResponse;

import org.greenrobot.eventbus.Subscribe;


public class CommonAddFeeActivity extends BaseActivity {

    String id;
    ActivityFeeAddBinding binding;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fee_add);
        id = getIntent().getStringExtra("id");

        if (id == null) {
            toolbarClick(getResources().getString(R.string.add_fee));
        } else {
            toolbarClick(getResources().getString(R.string.update_fee));
        }

        binding.name.setText(getIntent().getStringExtra("name"));
        binding.amount.setText(getIntent().getStringExtra("amount"));

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid = true;
                if (binding.name.getText().toString().isEmpty()) {
                    isValid = setError(binding.feeTypeLayout, binding.name, getString(R.string.enter_fee_type_name));
                }
                if (binding.amount.getText().toString().isEmpty()) {
                    isValid = setError(binding.amountLayout, binding.amount, getString(R.string.enter_fee_amount_msg));
                }
                if (isValid) {
                    AddFeeSetupRequest request = new AddFeeSetupRequest();
                    request.setId(id);
                    request.setFeeType(binding.name.getText().toString());
                    request.setFee(Double.parseDouble(binding.amount.getText().toString()));
                    apiCallBack(getApiCommonController().addFee(request));
                } else {
                    dialogError(getString(R.string.please_check_form));
                }
            }
        });

    }


    @Subscribe
    public void addFee(AddFeeSetupResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccessFinish(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


}
