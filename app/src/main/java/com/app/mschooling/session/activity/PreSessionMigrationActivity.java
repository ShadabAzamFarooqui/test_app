package com.app.mschooling.session.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.enrollment.LandingPageActivity;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.CreateSessionRequest;
import com.mschooling.transaction.response.session.CreateSessionResponse;

import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

public class PreSessionMigrationActivity extends BaseActivity {

    ViewPager viewPager;
    RadioGroup mRadioGroup;
    MyPagerAdapter adapter;
    int currentPage = 0;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_session_migration);
        toolbarClick(getString(R.string.session_migration));

        viewPager = findViewById(R.id.viewpager);
        mRadioGroup = findViewById(R.id.radioGroup);
        next = findViewById(R.id.next);

        next.setVisibility(View.VISIBLE);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (R.id.radioButton1 == checkedId) {
                    currentPage = 0;
                } else if (R.id.radioButton2 == checkedId) {
                    currentPage = 1;
                } else if (R.id.radioButton3 == checkedId) {
                    currentPage = 2;
                } else if (R.id.radioButton4 == checkedId) {
                    currentPage = 3;
                } else if (R.id.radioButton5 == checkedId) {
                    currentPage = 4;
                } else if (R.id.radioButton6 == checkedId) {
                    currentPage = 5;
                } else if (R.id.radioButton7 == checkedId) {
                    currentPage = 6;
                } else if (R.id.radioButton8 == checkedId) {
                    currentPage = 7;
                }
                viewPager.setCurrentItem(currentPage, true);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage == 7) {
                    startActivity(new Intent(getApplicationContext(), SessionMigrationActivity.class));
                    finish();
                } else {
                    currentPage = currentPage + 1;
                    viewPager.setCurrentItem(currentPage, true);
                }
            }
        });

        adapter = new MyPagerAdapter();
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mRadioGroup.check(R.id.radioButton1);
                        break;
                    case 1:
                        mRadioGroup.check(R.id.radioButton2);
                        break;
                    case 2:
                        mRadioGroup.check(R.id.radioButton3);
                        break;
                    case 3:
                        mRadioGroup.check(R.id.radioButton4);
                        break;
                    case 4:
                        mRadioGroup.check(R.id.radioButton5);
                        break;
                    case 5:
                        mRadioGroup.check(R.id.radioButton6);
                        break;
                    case 6:
                        mRadioGroup.check(R.id.radioButton7);
                        break;
                    case 7:
                        mRadioGroup.check(R.id.radioButton8);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(adapter);


    }


    @Override
    public void onStop() {
        super.onStop();


    }


    class MyPagerAdapter extends PagerAdapter {


        public int getCount() {
            return 8;
        }

        public Object instantiateItem(View collection, int position) {

            LayoutInflater inflater = (LayoutInflater) collection.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.layout.migrate1;
                    break;
                case 1:

                    resId = R.layout.migrate2;
                    break;
                case 2:

                    resId = R.layout.migrate3;
                    break;
                case 3:

                    resId = R.layout.migrate4;
                    break;
                case 4:

                    resId = R.layout.migrate5;
                    break;
                case 5:

                    resId = R.layout.migrate6;
                    break;
                case 6:

                    resId = R.layout.migrate7;
                    break;
                case 7:

                    resId = R.layout.migrate8;
                    break;
            }

            View view = inflater.inflate(resId, null);

            ((ViewPager) collection).addView(view, 0);

            return view;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);

        }


        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }

}