package com.app.mschooling.students.detail.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.mschooling.attendance.student.adapter.CustomSpinnerAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.attendance.student.fragment.StudentAttendanceFragment;
import com.app.mschooling.students.detail.fragment.AddressDetailFragment;
import com.app.mschooling.students.detail.fragment.BasicDetailFragment;
import com.app.mschooling.students.detail.fragment.OtherFragment;
import com.app.mschooling.students.detail.fragment.PersonalDetailFragment;
import com.app.mschooling.students.detail.fragment.StudentImageFragment;
import com.app.mschooling.students.detail.fragment.StudentFeeDetailListFragment;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Preferences;
import com.google.android.material.tabs.TabLayout;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.attendance.GetAttendanceMonthResponse;
import com.mschooling.transaction.response.student.GetStudentDetailResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class StudentCompleteDetailActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    LinearLayout edit;
    private GetStudentDetailResponse response;
    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_complete_detail);
        toolbarClick(getString(R.string.tool_student_details));
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        edit = findViewById(R.id.edit);
        if (getIntent().getBooleanExtra("update",false)) {
            edit.setVisibility(View.GONE);
        }

        if (!Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())) {
            edit.setVisibility(View.GONE);
        }
        if (getIntent().getBooleanExtra("isDeleted", false)) {
            edit.setVisibility(View.GONE);
        }

        apiCallBackWithout(getApiCommonController().getMonth());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                StudentCompleteDetailActivity.this.position=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        edit.setOnClickListener(v -> {
            AppUser.getInstance().setUpdate(true);
            Intent intent = new Intent(getApplicationContext(), StudentCompleteDetailActivity.class);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            intent.putExtra("classId", getIntent().getStringExtra("classId"));
            intent.putExtra("sectionId", getIntent().getStringExtra("sectionId"));
            intent.putExtra("enrollmentId", getIntent().getStringExtra("enrollmentId"));
            intent.putExtra("position",position);
            intent.putExtra("update",true);
            startActivity(intent);
            finish();
        });
        apiCallBack(getApiCommonController().getStudentDetails(getIntent().getStringExtra("id")));
    }

    private void setupViewPager(ViewPager viewPager) {
        StudentCompleteDetailActivity.ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StudentImageFragment(response, getIntent().getBooleanExtra("update",false)),"Profile");
        adapter.addFragment(new BasicDetailFragment(response, getIntent().getBooleanExtra("update",false)),"Basic");
        adapter.addFragment(new PersonalDetailFragment(response, getIntent().getBooleanExtra("update",false)),"Personal");
        adapter.addFragment(new AddressDetailFragment(response, getIntent().getBooleanExtra("update",false)),"Address");
//        adapter.addFragment(new IdentificationFragment(response), getIntent().getBooleanExtra("update",false)),"Identification");
        adapter.addFragment(new OtherFragment(response, getIntent().getBooleanExtra("update",false)),"Other");
        adapter.addFragment(new StudentFeeDetailListFragment(response,getIntent().getBooleanExtra("update",false)), "Fee");
        adapter.addFragment(new StudentAttendanceFragment(), "Attendance");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    static class ViewPagerAdapter extends FragmentPagerAdapter {
        public final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
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
    public void getStudentDetails(GetStudentDetailResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(0);
            viewPager.setCurrentItem(getIntent().getIntExtra("position",0));
        }
    }

    @Subscribe
    public void getMonth(GetAttendanceMonthResponse response) {
        if (Status.SUCCESS.value().equals(response.getStatus().value())) {
           Preferences.getInstance(getApplicationContext()).setMonthResponse(response);
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }
}
