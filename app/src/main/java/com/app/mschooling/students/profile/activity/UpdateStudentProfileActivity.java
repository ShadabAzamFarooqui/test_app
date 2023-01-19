package com.app.mschooling.students.profile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.app.mschooling.home.student.activity.StudentMainActivity;
import com.app.mschooling.enrollment.LandingPageActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventToolbar;
import com.app.mschooling.students.profile.fragment.StudentProfileAddressFragment;
import com.app.mschooling.students.profile.fragment.StudentProfileImageFragment;
import com.app.mschooling.students.profile.fragment.StudentProfileOtherFragment;
import com.app.mschooling.students.profile.fragment.StudentProfilePersonalFragment;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.profile.GetStudentProfileResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateStudentProfileActivity extends BaseActivity {

    @BindView(R.id.logout)
    LinearLayout logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.update_profile));
        apiCallBack(getApiCommonController().getStudentProfile());

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
    public void getProfile(GetStudentProfileResponse response) {
        if (Status.SUCCESS.value() == response.getStatus().value()) {
            StudentProfileImageFragment studentProfileImageFragment = new StudentProfileImageFragment();
            studentProfileImageFragment.init(response,true);

            StudentProfilePersonalFragment studentProfilePersonalFragment=new StudentProfilePersonalFragment();
            studentProfilePersonalFragment.init(response,true);

            StudentProfileAddressFragment studentProfileAddressFragment=new StudentProfileAddressFragment();
            studentProfileAddressFragment.init(response,true);

            StudentProfileOtherFragment studentProfileOtherFragment=new StudentProfileOtherFragment();
            studentProfileOtherFragment.init(response,true);

            if (response.getStudentProfileResponse().isEmpty()) {
                toolbarClick(getString(R.string.image));
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, studentProfileImageFragment).commit();
            } else if (response.getStudentPersonalResponse().isEmpty()) {
                toolbarClick(getString(R.string.personal));
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, studentProfilePersonalFragment).commit();
            } else if (response.getStudentAddressResponse().isEmpty()) {
                toolbarClick(getString(R.string.address));
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, studentProfileAddressFragment).commit();
            }  else if (response.getStudentOtherResponse().isEmpty()) {
                toolbarClick(getString(R.string.other));
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, studentProfileOtherFragment).commit();
            } else {
                Preferences.getInstance(getApplicationContext()).setProfileComplete(true);
                Intent intent = new Intent(getApplicationContext(), StudentMainActivity.class);
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
