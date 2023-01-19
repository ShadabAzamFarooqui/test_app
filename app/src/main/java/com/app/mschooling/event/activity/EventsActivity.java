package com.app.mschooling.event.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event.fragment.AllEventFragment;
import com.app.mschooling.event.fragment.CurrentEventFragment;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.Preferences;
import com.app.mschooling.utils.calenderview.CustomCalendarView;
import com.google.android.material.tabs.TabLayout;
import com.mschooling.transaction.common.api.Common;

import org.greenrobot.eventbus.Subscribe;

public class EventsActivity extends BaseActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    LinearLayoutManager mLayoutManager;
    CurrentEventFragment currentEventFragment;
    AllEventFragment allEventFragment;
    LinearLayout add;



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        toolbarClick(getString(R.string.tool_events));
        if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())){
            CustomCalendarView.enableMark = true;
        }else {
            CustomCalendarView.enableMark = false;
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        add =  findViewById(R.id.add);
        viewPager.setSaveFromParentEnabled(false);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        if (Preferences.getInstance(this).getROLE().equals(Common.Role.ADMIN.value())) {
            add.setVisibility(View.VISIBLE);
        } else {
            add.setVisibility(View.GONE);
        }

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

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(EventsActivity.this, AddEventActivity.class));

                Intent intent=new Intent(getApplicationContext(), AddEventFinalActivity.class);
                intent.putExtra("date",CustomCalendarView.selectedDate);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(0);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        System.out.println("check back stack " + fragmentManager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        currentEventFragment = new CurrentEventFragment();
        allEventFragment = new AllEventFragment();

        CustomCalendarView.selectedDate = Helper.getCurrentDate();

    }

    public class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:
                    return currentEventFragment;
                case 1:
                    return allEventFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

    }


    @Subscribe
    public void timeout(String msg) {

    }
}
