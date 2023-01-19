package com.app.mschooling.enrollment.otp.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.mschooling.guid.AlertActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.Preferences;
import com.app.mschooling.utils.otp_reader.SMSReceiver;
import com.chaos.view.PinView;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.login.LoginRequest;
import com.mschooling.transaction.request.register.AddAccountRequest;
import com.mschooling.transaction.request.register.AddRegisterRequest;
import com.mschooling.transaction.response.login.LoginResponse;
import com.mschooling.transaction.response.registration.AddAccountResponse;
import com.mschooling.transaction.response.registration.AddRegistrationResponse;
import com.mschooling.transaction.response.registration.SendOTPResponse;

import org.greenrobot.eventbus.Subscribe;


public class OtpRegisterActivity extends BaseActivity implements SMSReceiver.OTPReceiveListener {

    Button submit;
    PinView pinView;
    RelativeLayout resendOtpLayout;
    TextView mobile;
    TextView timer;
    private SMSReceiver smsReceiver;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_otp);
        toolbarClick(getString(R.string.verify_otp));
        submit = findViewById(R.id.submit);
        mobile = findViewById(R.id.mobile);
        pinView = findViewById(R.id.pinView);
        timer = findViewById(R.id.timer);
        resendOtpLayout = findViewById(R.id.resendOtpLayout);
        mobile.setText("+91 " + getIntent("mobile"));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinView.getText().toString().length() == 0) {
                    dialogError(getString(R.string.enter_otp));
                    return;
                }
                if (pinView.getText().toString().length() != 4) {
                    dialogError(getString(R.string.enter_complete_otp));
                    return;
                }
                AddAccountRequest request = new AddAccountRequest();
                request.setOtp(pinView.getText().toString());
                request.setName(getIntent("name"));
                request.setMobile(getIntent("mobile"));
                request.setSchoolName(getIntent("schoolName"));
                apiCallBack(getApiCommonController().registerSchool(request));

            }
        });


        resendOtpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiCallBack(getApiCommonController().sendOTP(getIntent("mobile")));
            }
        });
        countDownTimer();

        startSMSListener();
    }


    void countDownTimer() {
        new CountDownTimer(180 * 1000, 1000) {
            public void onTick(long millis) {
                resendOtpLayout.setVisibility(View.GONE);
                timer.setVisibility(View.VISIBLE);
                String hms = Helper.getTimeHM(millis);
                timer.setText(hms);
            }

            public void onFinish() {
                resendOtpLayout.setVisibility(View.VISIBLE);
                timer.setVisibility(View.GONE);
                timer.setText(R.string.done);
            }

        }.start();
    }

    @Subscribe
    public void addRegistration(AddAccountResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            LoginRequest request = new LoginRequest();
            request.setEnrollmentId(response.getAccountResponse().getEnrollmentId());
            request.setPasscode(response.getAccountResponse().getPasscode());
            apiCallBack(getApiCommonController().login(request));
        } else {
            if (response.getAccountResponse().isRedirect()) {
                finish();
            } else {
                dialogFailed(response.getMessage().getMessage());
            }
        }
    }

    @Subscribe
    public void login(LoginResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            Preferences.getInstance(OtpRegisterActivity.this).setToken(response.getConfigurationResponse().getUserSetup().getToken());
            Preferences.getInstance(OtpRegisterActivity.this).setEnrollmentId(response.getConfigurationResponse().getUserSetup().getEnrollmentId());
            Preferences.getInstance(OtpRegisterActivity.this).setLogin(true);
            Preferences.getInstance(OtpRegisterActivity.this).setROLE(response.getConfigurationResponse().getUserSetup().getRole().value());
            Preferences.getInstance(getApplicationContext()).setAcademicSession(response.getConfigurationResponse().getCommonSetup().getAcademicSession().getName());
            Preferences.getInstance(getApplicationContext()).setUserName(response.getConfigurationResponse().getUserSetup().getName());
            Preferences.getInstance(getApplicationContext()).setROLE(response.getConfigurationResponse().getUserSetup().getRole().value());
            Preferences.getInstance(getApplicationContext()).setPaid(response.getConfigurationResponse().getCommonSetup().getSubscription().isPaid());
            Preferences.getInstance(getApplicationContext()).setZoomSetup(response.getConfigurationResponse().getZoomSetup().isZoomAdded());
            Preferences.getInstance(getApplicationContext()).setAdsCycle(response.getConfigurationResponse().getCommonSetup().getAdsCycle());
            if (response.getConfigurationResponse().getCommonSetup().getSettingSetup().getAttendanceMode() != null) {
                Preferences.getInstance(getApplicationContext()).setAttendanceMode(response.getConfigurationResponse().getCommonSetup().getSettingSetup().getAttendanceMode());
            }
            Preferences.getInstance(getApplicationContext()).setConfiguration(response.getConfigurationResponse());
            Intent intent = new Intent(OtpRegisterActivity.this, AlertActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


//    auto otp reader code


    private void startSMSListener() {
        try {
            smsReceiver = new SMSReceiver();
            smsReceiver.setOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            this.registerReceiver(smsReceiver, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(this);

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // API successfully started
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail to start API
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onOTPReceived(String otp) {
        otp = otp.replace("<#> mSchooling: Your verification code is ", "");
//        otp = otp.replace("{#var#}: Your verification code is {#var#} {#var#} - mSchooling", "");
        otp = otp.replace("<#>: Your verification code is ", "");
        otp = otp.replace(" - mSchooling", "");
        otp = otp.replace("OCclhRt7PP0", "");
        pinView.setText(otp);
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
            smsReceiver = null;
        }
    }

    @Override
    public void onOTPTimeOut() {

    }

    @Override
    public void onOTPReceivedError(String error) {

    }


    @Subscribe
    public void authentication(SendOTPResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            countDownTimer();
        }
    }


}

