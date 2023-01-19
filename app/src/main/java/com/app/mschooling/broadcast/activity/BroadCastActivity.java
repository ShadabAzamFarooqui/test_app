package com.app.mschooling.broadcast.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.mschooling.class_section_subject.activity.ClassSectionMultipleSectionActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.broadcast.AddBroadcastRequest;
import com.mschooling.transaction.response.broadcast.AddBroadcastResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BroadCastActivity extends BaseActivity {

    Spinner spinnerVia;
    Spinner spinnerDept;
    LinearLayout layoutClass;
    LinearLayout classLayout;
    LinearLayout layoutSubject;
    Button submit;
    EditText subject;
    EditText message;
    TextView classSection;
    static final int MAX_VAL = 145;
    List<String> idList;

    //    ClassSectionBroadcastBaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broad_cast);
        toolbarClick(getString(R.string.broad_cast));
        spinnerVia = findViewById(R.id.spinnerVia);
        spinnerDept = findViewById(R.id.spinnerDept);
        layoutClass = findViewById(R.id.layoutClass);
        classLayout = findViewById(R.id.classLayout);
        layoutSubject = findViewById(R.id.layoutSubject);
        submit = findViewById(R.id.submit);
        message = findViewById(R.id.message);
        subject = findViewById(R.id.subject);
        classSection = findViewById(R.id.classSection);


        layoutClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), ClassSectionMultipleSectionActivity.class), 100);
            }
        });
        spinnerVia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    layoutSubject.setVisibility(View.VISIBLE);
                    message.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.MAX_VALUE)});
                    message.setHint(R.string.email_message);
                    message.setSelection(message.getText().length());
                } else {
                    layoutSubject.setVisibility(View.GONE);
                    message.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_VAL)});
                    message.setHint(R.string.broad_cast_message);
                    if (message.getText().toString().length() > MAX_VAL) {
                        message.setText(message.getText().toString().substring(0, MAX_VAL));
                    }
                    message.setSelection(message.getText().length());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    layoutClass.setVisibility(View.VISIBLE);
                } else {
                    layoutClass.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.please_write_your_message));
                    return;
                }
                if (layoutClass.getVisibility() == View.VISIBLE && classSection.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_select_class_section));
                    return;
                }
                AddBroadcastRequest request = new AddBroadcastRequest();
                request.setMode(Common.Mode.SMS);
                if (spinnerDept.getSelectedItemPosition() == 0) {
                    request.setSubject("TEACHERS");
                } else {
                    request.setSubject(classSection.getText().toString());
                }

/*
                if (spinnerDept.getSelectedItemPosition() == 0) {
                    request.setSubject(subject.getText().toString());

                } else {
                    request.setSubject("");
                    request.setMode(Common.Mode.SMS);
                }
*/

               if (spinnerDept.getSelectedItemPosition() == 0) {
                    request.setRole(Common.Role.TEACHER);
                } else {
                    request.setRole(Common.Role.STUDENT);
                }
                request.setRole(Common.Role.getRole(spinnerDept.getSelectedItem().toString()));
                request.setPriority(Common.Priority.HIGH);
                request.setContent(message.getText().toString());
                request.setSectionIds(idList);
                apiCallBack(getApiCommonController().sendBroadCast("add", request));
            }
        });

    }


    @Subscribe
    public void sendBroadCast(AddBroadcastResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            spinnerVia.setSelection(0);
            spinnerDept.setSelection(0);
            subject.setText("");
            message.setText("");
            dialogSuccessFinish(response.getMessage().getMessage());
            //  getDialog("Success", "Your message has been send", false);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 100) {
                    idList = new ArrayList<>();
                    classSection.setText(data.getStringExtra("names"));
                    idList = Arrays.asList(data.getStringExtra("ids").split("\\s*,\\s*"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}