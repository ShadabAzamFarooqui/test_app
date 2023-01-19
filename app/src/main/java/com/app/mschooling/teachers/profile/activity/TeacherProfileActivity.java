package com.app.mschooling.teachers.profile.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.teachers.profile.fragment.TeacherProfileAddressFragment;
import com.app.mschooling.teachers.profile.fragment.TeacherProfileBankDetailFragment;
import com.app.mschooling.teachers.profile.fragment.TeacherProfileBasicDetailFragment;
import com.app.mschooling.teachers.profile.fragment.TeacherProfileIdentificationFragment;
import com.app.mschooling.teachers.profile.fragment.TeacherProfileImageFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.profile.GetTeacherProfileResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class TeacherProfileActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppBarLayout appBar;
    LinearLayout edit;
    private GetTeacherProfileResponse response;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_complete_detail);
        toolbarClick("Profile");
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        appBar = findViewById(R.id.appBar);
        edit = findViewById(R.id.edit);
        edit.setVisibility(View.GONE);

        apiCallBack(getApiCommonController().getTeacherProfile());
    }

    private void setupViewPager(ViewPager viewPager) {
        TeacherProfileActivity.ViewPagerAdapter adapter = new TeacherProfileActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TeacherProfileImageFragment(response,false), getString(R.string.profile));
        adapter.addFragment(new TeacherProfileBasicDetailFragment(response,false), getString(R.string.basic));
        adapter.addFragment(new TeacherProfileAddressFragment(response,false), getString(R.string.address));
        adapter.addFragment(new TeacherProfileIdentificationFragment(response,false), getString(R.string.tool_identification));
        adapter.addFragment(new TeacherProfileBankDetailFragment(response,false), getString(R.string.bank));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(10);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        public  final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Subscribe
    public void getStudentDetails(GetTeacherProfileResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(0);
        }
    }
}
