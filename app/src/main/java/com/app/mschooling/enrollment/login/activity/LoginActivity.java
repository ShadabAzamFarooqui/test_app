package com.app.mschooling.enrollment.login.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityLoginBinding;
import com.app.mschooling.enrollment.forget.activity.ForgetEnrolmentActivity;
import com.app.mschooling.enrollment.otp.activity.OtpActivity;
import com.app.mschooling.network.ThisApp;
import com.app.mschooling.other.activity.TncActivity;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.login.AuthenticationRequest;
import com.mschooling.transaction.response.login.AuthenticationResponse;

import org.greenrobot.eventbus.Subscribe;


public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;
    Drawable drawable;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        toolbarClick(getString(R.string.tool_login));
//        new FusedLocation(this);

        if (ParameterConstant.isMSchooling(this)) {
            binding.tooltip.setVisibility(View.VISIBLE);
            binding.logo.setImageDrawable(getDrawable(R.drawable.mschooling_text_logo));
        } else if (ParameterConstant.isLittleSteps(this)) {
            binding.tooltip.setVisibility(View.GONE);
            binding.logo.setImageDrawable(getDrawable(R.drawable.little_steps));
        }

        drawable = getResources().getDrawable(R.drawable.error);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        if (BuildConfig.DEBUG) {
            binding.debugLayout.setVisibility(View.VISIBLE);
            if (Preferences.getInstance(getApplicationContext()).getBuildType().equals("Staging")) {
                binding.enrolmentId.setText("1111111");
                binding.debug.setChecked(true);
            } else if (Preferences.getInstance(getApplicationContext()).getBuildType().equals("Production")) {
                binding.enrolmentId.setText("");
                binding.production.setChecked(true);
            }
            binding.debug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        binding.enrolmentId.setText("1111111");
                        ThisApp.setApi();
                        Preferences.getInstance(getApplicationContext()).setBuildType("Staging");
                    }
                }
            });
            binding.production.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        binding.enrolmentId.setText("");
                        ThisApp.setApi();
                        Preferences.getInstance(getApplicationContext()).setBuildType("Production");
                    }
                }
            });

        } else {
            binding.debugLayout.setVisibility(View.GONE);
        }

        binding.terms.setClickable(true);
        binding.terms.setMovementMethod(LinkMovementMethod.getInstance());
        String text = getString(R.string.login_privacy_poicy);
        binding.terms.setText(Html.fromHtml(text));
        SpannableString ss = new SpannableString(text);
        ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(LoginActivity.this, TncActivity.class);
                intent.putExtra("from", "tnc");
                startActivity(intent);
            }
        };

        ClickableSpan span2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(LoginActivity.this, TncActivity.class);
                intent.putExtra("from", "");
                startActivity(intent);
            }
        };

        ss.setSpan(span1, text.indexOf("Te"), text.lastIndexOf("e") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(span2, text.indexOf("Pr"), text.lastIndexOf("y") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.terms.setText(ss);
        binding.terms.setMovementMethod(LinkMovementMethod.getInstance());

        binding.forgetId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgetEnrolmentActivity.class);
                intent.putExtra("intent", "id");
                startActivity(intent);
            }
        });
        binding.forgetPassCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgetEnrolmentActivity.class);
                intent.putExtra("intent", "code");
                startActivity(intent);
            }
        });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean isValid = true;
                    if (binding.enrolmentId.getText().toString().isEmpty()) {
                        isValid = setError(binding.mobileLayout, binding.enrolmentId, getString(R.string.enter_enrollment_id));
                    } else if (binding.enrolmentId.getText().toString().length() != 7 && binding.enrolmentId.getText().toString().length() != 10) {
                        isValid = setError(binding.mobileLayout, binding.enrolmentId, getString(R.string.invalid_enrollment_id));
                    }
                    if (isValid) {
                        AuthenticationRequest request = new AuthenticationRequest();
                        request.setIdentifier(binding.enrolmentId.getText().toString());
                        request.setAppName(getResources().getString(R.string.app_name));
                        request.setVersion(Helper.getVersion(LoginActivity.this));
//                        request.setImei(Helper.getDeviceId(LoginActivity.this));
//                        request.setLatitude(FusedLocation.getLatitude());
//                        request.setLongitude(FusedLocation.getLongitude());
//                        request.setAccuracy(FusedLocation.getAccuracy());
                        Preferences.getInstance(getApplicationContext()).setAuthenticationRequest(request);
                        apiCallBack(getApiCommonController().authenticate(request));
                    } else {
                        dialogError(getString(R.string.please_check_form));
                    }

                } catch (Exception e) {
                    dialogError(e.getMessage());
                }
            }
        });

    }


    @Subscribe
    public void authentication(AuthenticationResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            Preferences.getInstance(getApplicationContext()).setAuthenticationResponse(response);
            startActivity(new Intent(getApplicationContext(), OtpActivity.class));
        } else {
            if (response.isForceUpdate()) {
                dialogUpdate(response.getMessage().getMessage(), response.isForceUpdate());
            } else {
                dialogFailed(response.getMessage().getMessage());

            }

        }

    }

}

