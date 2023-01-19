package com.app.mschooling.teachers.detail.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.attendance.teacher.fragment.TeacherTimeTableAllocationFragment;
import com.app.mschooling.teachers.detail.fragment.BankDetailFragment;
import com.app.mschooling.teachers.detail.fragment.BasicTeacherDetailFragment;
import com.app.mschooling.teachers.detail.fragment.TeacherAddressDetailFragment;
import com.app.mschooling.teachers.detail.fragment.TeacherIdentificationFragment;
import com.app.mschooling.teachers.detail.fragment.TeacherImageFragment;
import com.app.mschooling.utils.AppUser;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.teacher.GetTeacherDetailResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class ShowTeacherDetailsActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppBarLayout appBar;
    LinearLayout edit;
    private GetTeacherDetailResponse response;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_complete_detail);
        toolbarClick(getString(R.string.tool_teacher_details));
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        appBar = findViewById(R.id.appBar);
        edit = findViewById(R.id.edit);
        if (AppUser.getInstance().isUpdate()){
            edit.setVisibility(View.GONE);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                AppUser.getInstance().setPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUser.getInstance().setUpdate(true);
                Intent intent=new Intent(getApplicationContext(), ShowTeacherDetailsActivity.class);
                intent.putExtra("id",getIntent().getStringExtra("id"));
                startActivity(intent);
                finish();
            }
        });


        apiCallBack(getApiCommonController().getTeacherDetails(getIntent().getStringExtra("id")));
    }

    private void setupViewPager(ViewPager viewPager) {
        ShowTeacherDetailsActivity.ViewPagerAdapter adapter = new ShowTeacherDetailsActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TeacherImageFragment(response), "Image");
        adapter.addFragment(new BasicTeacherDetailFragment(response), "Basic");
        adapter.addFragment(new TeacherAddressDetailFragment(response), "Address");
//        adapter.addFragment(new TeacherIdentificationFragment(response), "Identification");
        adapter.addFragment(new BankDetailFragment(response), "Bank");
        adapter.addFragment(new TeacherTimeTableAllocationFragment(), "TimeTable");
//        adapter.addFragment(new TeacherTimeTableAllocationFragment(response.getTeacherResponseList().get(0)), "Timetable");
        viewPager.setAdapter(adapter);
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
    public void getTeacherDetails(GetTeacherDetailResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(0);
            viewPager.setCurrentItem(AppUser.getInstance().getPosition());
        }else {
            dialogError(response.getMessage().getMessage());
        }
    }
}
