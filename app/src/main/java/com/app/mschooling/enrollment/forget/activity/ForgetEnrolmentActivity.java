package com.app.mschooling.enrollment.forget.activity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.enrollment.UserSelectionSpinnerAdapter;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.login.AuthenticationRequest;
import com.mschooling.transaction.response.login.AuthenticationResponse;
import com.mschooling.transaction.response.login.ForgetResponse;

import org.greenrobot.eventbus.Subscribe;

public class ForgetEnrolmentActivity extends BaseActivity {


    EditText mobile;
    Button submit;
    LinearLayout back;
    String intentString;
    Spinner users;

    LinearLayout mobileLayout;
    LinearLayout spinnerLayout;
    ImageView logo;
    LinearLayout tooltip;
    AuthenticationResponse response;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_enrollment);
        intentString = getIntent().getStringExtra("intent");
        if (intentString.equals("id")) {
            toolbarClick(getString(R.string.forget));
        } else {
            toolbarClick(getString(R.string.forget_passcode));
        }
        mobileLayout = findViewById(R.id.mobileLayout);
        spinnerLayout = findViewById(R.id.spinnerLayout);
        mobile = findViewById(R.id.mobile);
        submit = findViewById(R.id.submit);
        back = findViewById(R.id.back);
        users = findViewById(R.id.users);
        logo = findViewById(R.id.logo);
        tooltip = findViewById(R.id.tooltip);

        mobileLayout.setVisibility(View.VISIBLE);
        spinnerLayout.setVisibility(View.GONE);

        if (ParameterConstant.isMSchooling(this)){
            tooltip.setVisibility(View.VISIBLE);
            logo.setImageDrawable(getDrawable(R.drawable.mschooling_text_logo));
        }else if(ParameterConstant.isLittleSteps(this)){
            tooltip.setVisibility(View.GONE);
            logo.setImageDrawable(getDrawable(R.drawable.little_steps));
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mobileLayout.getVisibility() == View.VISIBLE) {
                    if (mobile.getText().toString().length() == 7 || mobile.getText().toString().length() == 10) {

                        AuthenticationRequest request = new AuthenticationRequest();
                        request.setIdentifier(mobile.getText().toString());
                        request.setAppName(getResources().getString(R.string.app_name));
                        request.setVersion(Helper.getVersion(ForgetEnrolmentActivity.this));
//                        request.setImei(Helper.getDeviceId(ForgetEnrolmentActivity.this));
//                        request.setLatitude(FusedLocation.getLatitude());
//                        request.setLongitude(FusedLocation.getLongitude());
//                        request.setAccuracy(FusedLocation.getAccuracy());
                        apiCallBack(getApiCommonController().authenticate(request));

                    } else {
                        dialogError(getString(R.string.please_valid_mobile_number));
                        return;
                    }

                } else {
                    try {
                        apiCallBack(getApiCommonController().forgetEnrol(response.getAuthenticationSchools().get(users.getSelectedItemPosition()).getEnrollmentId()));
                    } catch (Exception e) {
                        dialogError(getString(R.string.something_went_wrong_please_try_again));
                    }
                }


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_forget_enrollment, container, false);

        return view;
    }


    @Subscribe
    public void response(ForgetResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            mobile.setText("");
            dialogSuccessFinish(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }

    }


    @Subscribe
    public void authentication(AuthenticationResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            this.response = response;
            mobileLayout.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.VISIBLE);
            users.setAdapter(new UserSelectionSpinnerAdapter(this, response.getAuthenticationSchools()));
        } else {
            dialogFailed(response.getMessage().getMessage());
        }

    }

}

