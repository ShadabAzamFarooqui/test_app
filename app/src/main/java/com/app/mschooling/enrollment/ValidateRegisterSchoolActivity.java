package com.app.mschooling.enrollment;

import static com.app.mschooling.base.fragment.BaseFragment.REQUEST_CODE_PICKER;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.enrollment.otp.activity.OtpRegisterActivity;
import com.app.mschooling.utils.Preferences;
import com.app.mschooling.utils.Validation;
import com.mschooling.multimediapicker.GalleryPickerActivity;
import com.mschooling.multimediapicker.MultimediaPicker;
import com.mschooling.multimediapicker.model.Image;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.register.AddRegisterRequest;
import com.mschooling.transaction.response.registration.GetValidateRegistrationResponse;
import com.mschooling.transaction.response.registration.SendOTPResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ValidateRegisterSchoolActivity extends BaseActivity {


    @BindView(R.id.adminName)
    EditText adminName;
    @BindView(R.id.adminMobile)
    EditText adminMobile;
    @BindView(R.id.schoolName)
    EditText schoolName;
    @BindView(R.id.submit)
    LinearLayout submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_register_school);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.setup));
        apiCallBackWithout(getApiCommonController().getGoogleApiKey());

        submit.setOnClickListener(v -> {
            if (schoolName.getText().toString().trim().isEmpty()) {
                dialogError(getString(R.string.enter_school_name));
                return;
            }
            if (adminName.getText().toString().trim().isEmpty()) {
                dialogError(getString(R.string.enter_admin_name));
                return;
            } else if (adminMobile.getText().toString().trim().isEmpty()) {
                dialogError(getString(R.string.enter_admin_mobile));
                return;
            }
            apiCallBack(getApiCommonController().validateRegister(adminMobile.getText().toString()));
        });

    }


    @Subscribe
    public void registerResponse(GetValidateRegistrationResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            apiCallBack(getApiCommonController().sendOTP(adminMobile.getText().toString()));
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    @Subscribe
    public void sentOtpResponse(SendOTPResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            Intent intent = new Intent(getApplicationContext(), OtpRegisterActivity.class);
            intent.putExtra("name", adminName.getText().toString());
            intent.putExtra("mobile", adminMobile.getText().toString());
            intent.putExtra("schoolName", schoolName.getText().toString());
            startActivity(intent);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


}