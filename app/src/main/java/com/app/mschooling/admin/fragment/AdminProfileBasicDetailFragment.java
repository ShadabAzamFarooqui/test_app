package com.app.mschooling.admin.fragment;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.chaos.view.PinView;
import com.google.android.material.textfield.TextInputLayout;
import com.mschooling.transaction.common.admin.request.AdminBasicRequest;
import com.mschooling.transaction.common.admin.response.AdminBasicResponse;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.common.user.UserSetup;
import com.mschooling.transaction.request.profile.UpdateAdminProfileRequest;
import com.mschooling.transaction.response.configuration.ConfigurationResponse;
import com.mschooling.transaction.response.profile.GetAdminProfileResponse;
import com.mschooling.transaction.response.profile.UpdateAdminMobileResponse;
import com.mschooling.transaction.response.profile.UpdateAdminProfileResponse;
import com.mschooling.transaction.response.registration.SendOTPResponse;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdminProfileBasicDetailFragment extends BaseFragment {

    @BindView(R.id.firstName)
    EditText firstName;
    @BindView(R.id.firstNameError)
    TextInputLayout firstNameError;
    @BindView(R.id.lastName)
    EditText lastName;
    @BindView(R.id.lastNameError)
    TextInputLayout lastNameError;
    @BindView(R.id.mobile)
    TextView mobile;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.enrolmentId)
    TextView enrolmentId;
    @BindView(R.id.gender)
    Spinner gender;
    @BindView(R.id.dob)
    TextView dob;
    @BindView(R.id.dateLayout)
    RelativeLayout dateLayout;
    @BindView(R.id.mobileLayout)
    LinearLayout mobileLayout;
    @BindView(R.id.submit)
    Button submit;

    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    GetAdminProfileResponse response;
    boolean update;
    AdminBasicResponse adminBasicResponse;

    public AdminProfileBasicDetailFragment(GetAdminProfileResponse response, boolean update) {
        this.response = response;
        this.update = update;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_profile_basic_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        Helper.enableDisableView(mainLayout, submit, update);

        adminBasicResponse = response.getAdminBasicResponse();
        imageName = Helper.getRandom();
        firstName.setText(adminBasicResponse.getfName());
        lastName.setText(adminBasicResponse.getlName());
        mobile.setText(adminBasicResponse.getMobile());
        email.setText(adminBasicResponse.getEmail());
        enrolmentId.setText(adminBasicResponse.getEnrollmentId());
        if (adminBasicResponse.getGender() == null) {
            adminBasicResponse.setGender(Common.Gender.Male);
        }

        mobileLayout.setOnClickListener(v ->
                changeMobileNumber());

        if (Common.Gender.Male.value().equals(adminBasicResponse.getGender().value())) {
            gender.setSelection(0);
        } else if (Common.Gender.Female.value().equals(adminBasicResponse.getGender().value())) {
            gender.setSelection(1);
        } else if (Common.Gender.Other.value().equals(adminBasicResponse.getGender().value())) {
            gender.setSelection(2);
        }
        dob.setText(adminBasicResponse.getDob());


        dateLayout.setOnClickListener(view1 -> {
            final Calendar myCalendar = Calendar.getInstance();
            myCalendar.set(Integer.parseInt(Helper.getCurrentYear()) - 2, Helper.getCurrentMonth() - 1, Helper.getCurrentDay());

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view11, year, monthOfYear, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf1 = new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.getDefault());
                dob.setText(sdf1.format(myCalendar.getTime()));
                dob.setError(null);

            }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());
            datePickerDialog.show();
        });


        submit.setOnClickListener(v -> {

            if (firstName.getText().toString().isEmpty()) {
                dialogError(getString(R.string.please_enter_name));
                return;
            }
            if (dob.getText().toString().isEmpty()) {
                dialogError(getString(R.string.please_select_dob));
                return;
            }
            UpdateAdminProfileRequest request = new UpdateAdminProfileRequest();
            AdminBasicRequest basicRequest = new AdminBasicRequest();
            basicRequest.setfName(firstName.getText().toString());
            basicRequest.setlName(lastName.getText().toString());
            basicRequest.setMobile(mobile.getText().toString());
            basicRequest.setEnrollmentId(enrolmentId.getText().toString());
            basicRequest.setEmail(email.getText().toString());
            basicRequest.setGender(Common.Gender.valueOf(gender.getSelectedItem().toString()));
            basicRequest.setDob(dob.getText().toString());
            request.setAdminBasicRequest(basicRequest);
            apiCallBack(getApiCommonController().updateAdminProfile(request));

        });

        return view;
    }


    @Subscribe
    public void updateStudent(UpdateAdminProfileResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            ConfigurationResponse configuration = Preferences.getInstance(getContext()).getConfiguration();
            UserSetup user = configuration.getUserSetup();
            user.setName(firstName.getText().toString() + " " + lastName.getText().toString());
            user.setMobile(mobile.getText().toString());
            Preferences.getInstance(getContext()).setConfiguration(configuration);
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    TextView timer;
    LinearLayout otpView;
    RelativeLayout resendOtpLayout;
    Dialog mobileNumberDialog;
    EditText mobileNumber;

    public void changeMobileNumber() {
        mobileNumberDialog = new Dialog(getContext());
        mobileNumberDialog.getWindow().requestFeature(1);
        mobileNumberDialog.setContentView(R.layout.change_mobile_number_dialog);
        mobileNumberDialog.setCancelable(false);
        mobileNumber = mobileNumberDialog.findViewById(R.id.mobile);
        timer = mobileNumberDialog.findViewById(R.id.timer);
        otpView = mobileNumberDialog.findViewById(R.id.otpView);
        PinView pinView = mobileNumberDialog.findViewById(R.id.pinView);
        resendOtpLayout = mobileNumberDialog.findViewById(R.id.resendOtpLayout);
        LinearLayout close = mobileNumberDialog.findViewById(R.id.close);
        Button submit = mobileNumberDialog.findViewById(R.id.submit);
        mobileNumberDialog.show();
        otpView.setVisibility(View.GONE);
        submit.setOnClickListener(view -> {
            if (otpView.getVisibility() == View.GONE) {

                if (mobileNumber.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_mobile_number));
                    return;
                }
                if (mobileNumber.getText().toString().length() != 10) {
                    dialogError(getString(R.string.please_valid_mobile_number));
                    return;
                }

                apiCallBack(getApiCommonController().sendOTP(mobileNumber.getText().toString()));

            } else {
                if (pinView.getText().toString().length() == 0) {
                    dialogError(getString(R.string.enter_otp));
                    return;
                }
                if (pinView.getText().toString().length() != 4) {
                    dialogError(getString(R.string.enter_complete_otp));
                    return;
                }
                mobileNumber.setEnabled(false);
                otpView.setVisibility(View.VISIBLE);
                apiCallBack(getApiCommonController().updateMobileNumber(mobileNumber.getText().toString(), pinView.getText().toString()));


            }

        });
        resendOtpLayout.setOnClickListener(view -> {
            mobileNumber.setEnabled(false);
            otpView.setVisibility(View.VISIBLE);
            countDownTimer(resendOtpLayout, timer, 5 * 60 * 1000);
        });
        close.setOnClickListener(v ->
                mobileNumberDialog.dismiss());


    }

    int sec = 0;


    void countDownTimer(RelativeLayout resendOtpLayout, TextView timer, final int time) {
        new CountDownTimer(time, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millis) {
                resendOtpLayout.setVisibility(View.GONE);
                timer.setVisibility(View.VISIBLE);
                String hms = Helper.getTimeHM(millis);
                timer.setText(hms);
            }

            public void onFinish() {
                resendOtpLayout.setVisibility(View.VISIBLE);
                timer.setVisibility(View.GONE);
            }

        }.start();
    }

    @Subscribe
    public void otpResponse(SendOTPResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            mobileNumber.setEnabled(false);
            otpView.setVisibility(View.VISIBLE);
            countDownTimer(resendOtpLayout, timer, 5 * 60 * 1000);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    @Subscribe
    public void otpResponse(UpdateAdminMobileResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            mobileNumberDialog.dismiss();
            mobile.setText(mobileNumber.getText().toString());
            ConfigurationResponse configurationResponse = Preferences.getInstance(getContext()).getConfiguration();
            UserSetup userSetup = configurationResponse.getUserSetup();
            userSetup.setMobile(mobileNumber.getText().toString());
            configurationResponse.setUserSetup(userSetup);
            Preferences.getInstance(getContext()).setConfiguration(configurationResponse);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

}

