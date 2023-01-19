package com.app.mschooling.birthday.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityBirthdayBinding;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.user.StudentSetup;
import com.mschooling.transaction.common.user.UserSetup;

public class BirthdayActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBirthdayBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_birthday);
        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        UserSetup userSetup = Preferences.getInstance(getApplicationContext()).getConfiguration().getUserSetup();
        binding.name.setText(userSetup.getName());
        binding.dob.setText(Helper.getCurrentDayMonth());

        if (Preferences.isStudent(this)) {
            StudentSetup studentSetup = Preferences.getInstance(getApplicationContext()).getConfiguration().getStudentSetup();
            binding.classSection.setVisibility(View.VISIBLE);
            binding.classSection.setText(studentSetup.getClassName() + " (" + studentSetup.getSectionName() + ")");
        } else {
            binding.classSection.setVisibility(View.GONE);
        }
    }
}