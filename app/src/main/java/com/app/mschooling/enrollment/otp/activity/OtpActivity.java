package com.app.mschooling.enrollment.otp.activity;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.mschooling.guid.AlertActivity;
import com.app.mschooling.students.profile.activity.UpdateStudentProfileActivity;
import com.app.mschooling.teachers.profile.activity.UpdateTeacherProfileActivity;
import com.app.mschooling.enrollment.UserSelectionSpinnerAdapter;
import com.app.mschooling.home.admin.activity.AdminMainActivity;
import com.app.mschooling.home.student.activity.StudentMainActivity;
import com.app.mschooling.home.teacher.activity.TeacherMainActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.app.mschooling.utils.otp_reader.SMSReceiver;
import com.chaos.view.PinView;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.login.LoginRequest;
import com.mschooling.transaction.response.login.AuthenticationResponse;
import com.mschooling.transaction.response.login.AuthenticationSchool;
import com.mschooling.transaction.response.login.LoginResponse;
import com.mschooling.transaction.response.profile.GetStudentProfileResponse;
import com.mschooling.transaction.response.profile.GetTeacherProfileResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;


public class OtpActivity extends BaseActivity implements SMSReceiver.OTPReceiveListener {

    Button otp;
    LinearLayout mainLayout;
    PinView pinView;
    RelativeLayout resendOtpLayout;
    TextView mobile;
    TextView timer;
    Spinner users;
    List<AuthenticationSchool> authenticationSchools;
    private SMSReceiver smsReceiver;
    ImageView logo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_otp_new);
        toolbarClick(getString(R.string.tool_verify_passcode));
        users = findViewById(R.id.users);
        otp = findViewById(R.id.login);
        mobile = findViewById(R.id.mobile);
        mainLayout = findViewById(R.id.mainLayout);
        pinView = findViewById(R.id.pinView);
        timer = findViewById(R.id.timer);
        logo = findViewById(R.id.logo);
        resendOtpLayout = findViewById(R.id.resendOtpLayout);
        mobile.setText("+91 " + Preferences.getInstance(getApplicationContext()).getAuthenticationResponse().getMobile());

        if (ParameterConstant.isMSchooling(this)) {
            logo.setImageDrawable(getDrawable(R.drawable.mschooling_text_logo));
        } else if (ParameterConstant.isLittleSteps(this)) {
            logo.setImageDrawable(getDrawable(R.drawable.little_steps));
        }
        authenticationSchools = Preferences.getInstance(getApplicationContext()).getAuthenticationResponse().getAuthenticationSchools();
        users.setAdapter(new UserSelectionSpinnerAdapter(this, authenticationSchools));

        if (BuildConfig.DEBUG) {
            if (Preferences.getInstance(getApplicationContext()).getBuildType().equals("Staging")) {
                pinView.setText("2580");
            }
        }
        otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinView.getText().toString().length() == 0) {
                    dialogError(getString(R.string.enter_passcode));
                    return;
                }
                if (pinView.getText().toString().length() != 4) {
                    dialogError(getString(R.string.enter_complete_passcode));
                    return;
                }

                LoginRequest loginRequest = new LoginRequest();
                try {
                    loginRequest.setEnrollmentId(authenticationSchools.get(users.getSelectedItemPosition()).getEnrollmentId());
                } catch (Exception e) {
                    dialogError(getString(R.string.something_went_wrong_please_try_again));
                }

                loginRequest.setPasscode(pinView.getText().toString());
                apiCallBack(getApiCommonController().login(loginRequest));

            }
        });


        resendOtpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiCallBack(getApiCommonController().authenticate(Preferences.getInstance(getApplicationContext()).getAuthenticationRequest()));
            }
        });
        countDownTimer(31 * 1000);


        startSMSListener();
    }


    void countDownTimer(int time) {
        new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {
                resendOtpLayout.setVisibility(View.GONE);
                timer.setVisibility(View.VISIBLE);
                timer.setVisibility(View.GONE);
                timer.setText(getString(R.string.time_remaining) + millisUntilFinished / 1000 + " second");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
//                resendOtpLayout.setVisibility(View.VISIBLE);
                resendOtpLayout.setVisibility(View.GONE);
                timer.setVisibility(View.GONE);
                timer.setText(R.string.done);
            }

        }.start();
    }


    @Subscribe
    public void login(LoginResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            Preferences.getInstance(OtpActivity.this).setToken(response.getConfigurationResponse().getUserSetup().getToken());
            Preferences.getInstance(OtpActivity.this).setEnrollmentId(response.getConfigurationResponse().getUserSetup().getEnrollmentId());
            Preferences.getInstance(OtpActivity.this).setLogin(true);
            Preferences.getInstance(OtpActivity.this).setROLE(response.getConfigurationResponse().getUserSetup().getRole().value());
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
            if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.STUDENT.value())) {
                apiCallBack(getApiCommonController().getStudentProfile());
            }
            if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())) {
                if (!response.getConfigurationResponse().getAdminSetup().isClassAdded() || !response.getConfigurationResponse().getAdminSetup().isSubjectAdded()) {
                    Intent intent = new Intent(OtpActivity.this, AlertActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(OtpActivity.this, AdminMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
            if (response.getConfigurationResponse().getUserSetup().getRole() == Common.Role.TEACHER) {
                apiCallBack(getApiCommonController().getTeacherProfile());
            }

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
        otp = otp.replace(" OCclhRt7PP0", "");
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
    public void getStudentProfile(GetStudentProfileResponse response) {
        if (Status.SUCCESS.value() == response.getStatus().value()) {
            if (response.getStudentProfileResponse().isEmpty()
                    || response.getStudentPersonalResponse().isEmpty()
                    || response.getStudentAddressResponse().isEmpty()) {

                Preferences.getInstance(getApplicationContext()).setProfileComplete(false);
                Intent intent = new Intent(getApplicationContext(), UpdateStudentProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (response.getStudentIdentificationResponse().isEmpty()
                    && response.getStudentOtherResponse().isEmpty()) {
                Preferences.getInstance(getApplicationContext()).setProfileComplete(false);
                Intent intent = new Intent(getApplicationContext(), UpdateStudentProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Preferences.getInstance(getApplicationContext()).setProfileComplete(true);
                Intent intent = new Intent(getApplicationContext(), StudentMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }


    @Subscribe
    public void getTeacherProfile(GetTeacherProfileResponse response) {
        if (Status.SUCCESS.value() == response.getStatus().value()) {
            if (response.getTeacherProfileResponse().isEmpty()
                    || response.getTeacherBasicResponse() == null
                    || response.getTeacherAddressResponse().isEmpty()) {

                Preferences.getInstance(getApplicationContext()).setProfileComplete(false);
                Intent intent = new Intent(getApplicationContext(), UpdateTeacherProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (response.getTeacherIdentificationResponse().isEmpty()
                    && response.getTeacherBankResponse().isEmpty()) {
                Preferences.getInstance(getApplicationContext()).setProfileComplete(false);
                Intent intent = new Intent(getApplicationContext(), UpdateTeacherProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Preferences.getInstance(getApplicationContext()).setProfileComplete(true);
                Intent intent = new Intent(getApplicationContext(), TeacherMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void authentication(AuthenticationResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            countDownTimer(180 * 1000);
            Preferences.getInstance(getApplicationContext()).setAuthenticationResponse(response);
        }
    }


}

