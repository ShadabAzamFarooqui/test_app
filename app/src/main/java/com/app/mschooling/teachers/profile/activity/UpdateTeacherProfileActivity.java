package com.app.mschooling.teachers.profile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.app.mschooling.home.teacher.activity.TeacherMainActivity;
import com.app.mschooling.enrollment.LandingPageActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventToolbar;
import com.app.mschooling.teachers.profile.fragment.TeacherProfileAddressFragment;
import com.app.mschooling.teachers.profile.fragment.TeacherProfileBankDetailFragment;
import com.app.mschooling.teachers.profile.fragment.TeacherProfileIdentificationFragment;
import com.app.mschooling.teachers.profile.fragment.TeacherProfileImageFragment;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.profile.GetTeacherProfileResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateTeacherProfileActivity extends BaseActivity {

    @BindView(R.id.logout)
    LinearLayout logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.profile));
        apiCallBack(getApiCommonController().getTeacherProfile());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.getInstance(getApplicationContext()).setLogin(false);
                Intent intent = new Intent(getApplicationContext(), LandingPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
    }


    @Subscribe
    public void getProfile(GetTeacherProfileResponse response) {
        if (Status.SUCCESS.value() == response.getStatus().value()) {
            if (response.getTeacherProfileResponse().isEmpty()) {
                toolbarClick(getString(R.string.image));
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new TeacherProfileImageFragment(response, true)).commit();
            }else if (response.getTeacherAddressResponse().isEmpty()) {
                toolbarClick(getString(R.string.address));
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new TeacherProfileAddressFragment(response,true)).commit();
            }else if (response.getTeacherIdentificationResponse().isEmpty()) {
                toolbarClick(getString(R.string.tool_identification));
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new TeacherProfileIdentificationFragment(response,true)).commit();
            }else if (response.getTeacherBankResponse().isEmpty()) {
                toolbarClick(getString(R.string.bank));
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new TeacherProfileBankDetailFragment(response,true)).commit();
            } else {
                Preferences.getInstance(getApplicationContext()).setProfileComplete(true);
                Intent intent=new Intent(getApplicationContext(), TeacherMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    @Subscribe
    public void changeToolbar(EventToolbar toolbar) {
        toolbarClick(toolbar.getToolbar());
    }
}
