package com.app.mschooling.enrollment.signup.activity;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.enrollment.signup.fragment.StudentSignUpFragment;
import com.app.mschooling.enrollment.signup.fragment.TeacherSignUpFragment;
import com.google.android.material.tabs.TabLayout;


public class SignUpBaseActivity extends BaseActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    LinearLayoutManager mLayoutManager;
    StudentSignUpFragment studentSignUpFragment;
    TeacherSignUpFragment teacherSignUpFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_up_base);
        toolbarClick(getString(R.string.sign_up));
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        if (studentSignUpFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(studentSignUpFragment).commit();
        }
        if (teacherSignUpFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(teacherSignUpFragment).commit();
        }
        studentSignUpFragment = new StudentSignUpFragment();
        teacherSignUpFragment = new TeacherSignUpFragment();


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    public class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:
                    return studentSignUpFragment;
                case 1:
                    return teacherSignUpFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}





