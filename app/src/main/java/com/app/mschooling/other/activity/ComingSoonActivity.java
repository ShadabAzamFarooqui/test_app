package com.app.mschooling.other.activity;


import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;

import org.greenrobot.eventbus.Subscribe;

public class ComingSoonActivity extends BaseActivity {
    private ImageView image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coming_soon);
        toolbarClick(getIntent().getStringExtra("toolbar"));
    }

    @Subscribe
    public void timeout(String msg) {
    }

}