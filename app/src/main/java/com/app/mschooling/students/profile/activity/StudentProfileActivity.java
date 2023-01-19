package com.app.mschooling.students.profile.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.students.profile.fragment.StudentProfileAddressFragment;
import com.app.mschooling.students.profile.fragment.StudentProfileBasicDetailFragment;
import com.app.mschooling.students.profile.fragment.StudentProfileImageFragment;
import com.app.mschooling.students.profile.fragment.StudentProfileOtherFragment;
import com.app.mschooling.students.profile.fragment.StudentProfilePersonalFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.profile.GetStudentProfileResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class StudentProfileActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppBarLayout appBar;
    LinearLayout edit;
    private GetStudentProfileResponse response;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_complete_detail);
        toolbarClick(getString(R.string.profile));
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        appBar = findViewById(R.id.appBar);
        edit = findViewById(R.id.edit);
        edit.setVisibility(View.GONE);

        apiCallBack(getApiCommonController().getStudentProfile());
    }

    private void setupViewPager(ViewPager viewPager) {

        StudentProfileImageFragment studentProfileImageFragment=new StudentProfileImageFragment();
        studentProfileImageFragment.init(response,false);

        StudentProfileBasicDetailFragment studentProfileBasicDetailFragment=new StudentProfileBasicDetailFragment();
        studentProfileBasicDetailFragment.init(response,false);

        StudentProfilePersonalFragment studentProfilePersonalFragment=new StudentProfilePersonalFragment();
        studentProfilePersonalFragment.init(response,false);

        StudentProfileAddressFragment studentProfileAddressFragment=new StudentProfileAddressFragment();
        studentProfileAddressFragment.init(response,false);

        StudentProfileOtherFragment studentProfileOtherFragment=new StudentProfileOtherFragment();
        studentProfileOtherFragment.init(response,false);

        StudentProfileActivity.ViewPagerAdapter adapter = new StudentProfileActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(studentProfileImageFragment, getString(R.string.profile));
        adapter.addFragment(studentProfileBasicDetailFragment, getString(R.string.academic));
        adapter.addFragment(studentProfilePersonalFragment, getString(R.string.tool_parent));
        adapter.addFragment(studentProfileAddressFragment, getString(R.string.address));
//        adapter.addFragment(new StudentProfileIdentificationFragment(response,false), getString(R.string.tool_identification));
        adapter.addFragment(studentProfileOtherFragment, getString(R.string.other));
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
    public void getStudentDetails(GetStudentProfileResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(0);
        }
    }
}
