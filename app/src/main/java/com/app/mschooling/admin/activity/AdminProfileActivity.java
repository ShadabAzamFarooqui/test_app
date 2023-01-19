package com.app.mschooling.admin.activity;

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
import com.app.mschooling.admin.fragment.AdminProfileAddressFragment;
import com.app.mschooling.admin.fragment.AdminProfileBasicDetailFragment;
import com.app.mschooling.admin.fragment.AdminProfileImageFragment;
import com.app.mschooling.utils.AppUser;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.profile.GetAdminProfileResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class AdminProfileActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppBarLayout appBar;
    LinearLayout edit;
    private GetAdminProfileResponse response;
    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);
        toolbarClick(getString(R.string.profile));
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        appBar = findViewById(R.id.appBar);
        edit = findViewById(R.id.edit);

        if (getIntent().getBooleanExtra("update",false)){
            edit.setVisibility(View.GONE);
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setVisibility(View.GONE);
                AppUser.getInstance().setUpdate(true);
                Intent intent=new Intent(getApplicationContext(), AdminProfileActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("update",true);
                startActivity(intent);
                finish();
            }
        });

        apiCallBack(getApiCommonController().getAdminProfile());
    }

    private void setupViewPager(ViewPager viewPager) {
        AdminProfileActivity.ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AdminProfileImageFragment(response,getIntent().getBooleanExtra("update",false)), getString(R.string.profile));
        adapter.addFragment(new AdminProfileBasicDetailFragment(response,getIntent().getBooleanExtra("update",false)), getString(R.string.basic));
        adapter.addFragment(new AdminProfileAddressFragment(response,getIntent().getBooleanExtra("update",false)), getString(R.string.address));
//        adapter.addFragment(new AdminProfileIdentificationFragment(response,getIntent().getBooleanExtra("update",false)), getString(R.string.tool_identification));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(10);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                AdminProfileActivity.this.position=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    static class ViewPagerAdapter extends FragmentPagerAdapter {
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
    public void getStudentDetails(GetAdminProfileResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(0);
            viewPager.setCurrentItem(getIntent().getIntExtra("position",0));
        }
    }
}
