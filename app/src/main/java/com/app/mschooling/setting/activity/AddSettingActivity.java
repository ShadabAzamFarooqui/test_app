package com.app.mschooling.setting.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.setting.AddSettingRequest;
import com.mschooling.transaction.response.setting.AddSettingResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class AddSettingActivity extends BaseActivity {

    Spinner spinner;
    Button submit;
    TextView note;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_setting);
//        toolbarClick(getString(R.string.update_Setup));
        toolbarClick(getIntent().getStringExtra("displayName"));
        spinner = findViewById(R.id.spinner);
        note = findViewById(R.id.note);
        submit = findViewById(R.id.submit);

        if (getIntent().getStringExtra("name").equals("ATTENDANCE_MODE")){
            note.setVisibility(View.VISIBLE);
        }else {
            note.setVisibility(View.GONE);
        }

        ArrayList<String> data = getIntent().getStringArrayListExtra("data");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, data);
        spinner.setAdapter(spinnerArrayAdapter);
        int position=data.indexOf(getIntent().getStringExtra("value"));
        if (position>-1){
            spinner.setSelection(position);
        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddSettingRequest request = new AddSettingRequest();
                request.setName(getIntent().getStringExtra("name"));
                request.setValue(spinner.getSelectedItem().toString());
                apiCallBack(getApiCommonController().updateSetting(request));
            }
        });

    }


    @Subscribe
    public void updateSetting(AddSettingResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            Preferences.getInstance(getApplicationContext()).setAttendanceMode(spinner.getSelectedItem().toString());
            dialogSuccessFinish(response.getMessage().getMessage());
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }


}

