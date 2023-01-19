package com.app.mschooling.other.activity;

import android.os.Bundle;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;

public class SalaryListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary_list);
        toolbarClick(getString(R.string.tool_salary));
    }
}