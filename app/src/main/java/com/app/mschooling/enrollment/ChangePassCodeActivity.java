package com.app.mschooling.enrollment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.ParameterConstant;
import com.chaos.view.PinView;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.login.ChangePasscodeRequest;
import com.mschooling.transaction.response.login.ChangePasscodeResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePassCodeActivity extends BaseActivity {


    @BindView(R.id.old)
    PinView old;
    @BindView(R.id.pass)
    PinView pass;
    @BindView(R.id.cpass)
    PinView cpass;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.tooltip)
    LinearLayout tooltip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passcode);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.change_passcode));

        if (ParameterConstant.isMSchooling(this)) {
            tooltip.setVisibility(View.VISIBLE);
            logo.setImageDrawable(getDrawable(R.drawable.mschooling_text_logo));
        } else if (ParameterConstant.isLittleSteps(this)) {
            tooltip.setVisibility(View.GONE);
            logo.setImageDrawable(getDrawable(R.drawable.little_steps));
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (old.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.enter_old_passcode));
                    return;
                }

                if (old.getText().toString().length() != 4) {
                    dialogError(getString(R.string.enter_complete_old_passcode));
                    return;
                }


                if (pass.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.enter_new_passcode));
                    return;
                }

                if (pass.getText().toString().length() != 4) {
                    dialogError(getString(R.string.enter_complete_new_passcode));
                    return;
                }


                if (cpass.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.enter_confirm_passcode));
                    return;
                }

                if (cpass.getText().toString().length() != 4) {
                    dialogError(getString(R.string.enter_complete_confirm_passcode));
                    return;
                }


                if (!cpass.getText().toString().equals(pass.getText().toString())) {
                    dialogError(getString(R.string.password_confirm_does_not_match));
                    return;
                }
                ChangePasscodeRequest request = new ChangePasscodeRequest();
//                request.setEnrollmentId(Preferences.getInstance(getApplicationContext()).getUserId());
                request.setOldCode(old.getText().toString());
                request.setNewCode(pass.getText().toString());
                apiCallBack(getApiCommonController().changePasscode(request));

            }
        });


    }


    @Subscribe
    public void change(ChangePasscodeResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
            old.setText("");
            pass.setText("");
            cpass.setText("");
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


}
