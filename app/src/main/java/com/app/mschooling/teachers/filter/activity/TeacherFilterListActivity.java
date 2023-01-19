package com.app.mschooling.teachers.filter.activity;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.mschooling.students.list.activity.StudentsListActivity;
import com.app.mschooling.teachers.list.activity.TeacherListActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.google.gson.Gson;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.filter.ListCriteria;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TeacherFilterListActivity extends BaseActivity {

    EditText search;
    Button submit;
    LinearLayout noFilter;
    CheckBox nameWise;
    CheckBox enrollWise;
    CheckBox admissionDateWise;
    CheckBox male;
    CheckBox female;
    CheckBox active;
    CheckBox disabled;
    CheckBox deleted;
    EditText name;
    EditText enrolmentId;

    public Dialog dialog;
    private DatePickerDialog DatePickerDialog;
    private SimpleDateFormat dateFormatter;
    String startDate;
    String endDate;
    ListCriteria criteria=new ListCriteria();


    @Nullable
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_teacher_filter_list);
        toolbarClick( getString(R.string.tool_filter));
        init();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameWise.isChecked()) {
                    criteria.setName(name.getText().toString());
                }
                if (enrollWise.isChecked()) {
                    criteria.setEnrollmentId(enrolmentId.getText().toString());
                }
                if (admissionDateWise.isChecked()) {
                    criteria.setJoiningStartDate(startDate);
                    criteria.setJoiningEndDate(endDate);
                }
                if (male.isChecked()) {
                    criteria.setGender(Common.Gender.Male);
                }
                if (female.isChecked()) {
                    criteria.setGender(Common.Gender.Female);
                }
                if (active.isChecked()){
                    criteria.setState(Common.State.ACTIVATED);
                }
                if (disabled.isChecked()){
                    criteria.setState(Common.State.DISABLED);
                }
                if (deleted.isChecked()){
                    criteria.setState(Common.State.DELETED);
                }

                Intent intent = new Intent(getApplicationContext(), TeacherListActivity.class);
                intent.putExtra("criteria", new Gson().toJson(criteria));
                startActivity(intent);
            }
        });

        noFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                criteria.setState(Common.State.ACTIVATED);
                Intent intent=new Intent(getApplicationContext(),TeacherListActivity.class);
                intent.putExtra("criteria", new Gson().toJson(criteria));
                startActivity(intent);
            }
        });

        nameWise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    name.setVisibility(View.VISIBLE);
                } else {
                    name.setVisibility(View.GONE);
                    name.setText("");
                }
            }
        });


        enrollWise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enrolmentId.setVisibility(View.VISIBLE);
                } else {
                    enrolmentId.setVisibility(View.GONE);
                    enrolmentId.setText("");
                }
            }
        });

        admissionDateWise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showDateDialog(admissionDateWise);
                }
            }
        });

        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    female.setChecked(false);
                }
            }
        });

        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    male.setChecked(false);
                }
            }
        });

        active.setChecked(true);
        active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    disabled.setChecked(false);
                    deleted.setChecked(false);
                } else {
                    if (!disabled.isChecked()&&!deleted.isChecked()){
                        active.setChecked(true);
                    }
                }
            }
        });

        disabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    active.setChecked(false);
                    deleted.setChecked(false);
                } else {
                    if (!disabled.isChecked()&&!deleted.isChecked()){
                        active.setChecked(true);
                    }
                }
            }
        });


        deleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    disabled.setChecked(false);
                    active.setChecked(false);
                } else {
                    if (!disabled.isChecked()&&!deleted.isChecked()){
                        active.setChecked(true);
                    }
                }
            }
        });


    }

    void init() {
        submit = findViewById(R.id.submit);
        search = findViewById(R.id.search);
        noFilter = findViewById(R.id.noFilter);
        nameWise = findViewById(R.id.nameWise);
        enrollWise = findViewById(R.id.enrollWise);
        admissionDateWise = findViewById(R.id.admissionDateWise);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        name = findViewById(R.id.name);
        enrolmentId = findViewById(R.id.enrolmentId);

        active = findViewById(R.id.active);
        disabled = findViewById(R.id.disabled);
        deleted = findViewById(R.id.deleted);
        name.setVisibility(View.GONE);
        enrolmentId.setVisibility(View.GONE);
    }





    public void showDateDialog(CheckBox checkBox) {
        dateFormatter = new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.US);
        Dialog dialog = new Dialog(TeacherFilterListActivity.this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.date_pick_dialog);
        dialog.setCancelable(false);
        TextView date1 = (TextView) dialog.findViewById(R.id.date1);
        TextView date2 = (TextView) dialog.findViewById(R.id.date2);
        Button submit = (Button) dialog.findViewById(R.id.submit);
        LinearLayout close =  dialog.findViewById(R.id.close);
        date1.setText(dateFormatter.format(System.currentTimeMillis()));
        date2.setText(dateFormatter.format(System.currentTimeMillis()));

        dateDialog(date1, Calendar.getInstance());
        dateDialog(date2, Calendar.getInstance());

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBox.setChecked(false);
                dialog.dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String start = ((TextView) dialog.findViewById(R.id.date1)).getText().toString();
                String end = ((TextView) dialog.findViewById(R.id.date2)).getText().toString();
                if (Helper.dateValidation(start, end) == -1) {
                    dialogError( getString(R.string.end_date_should_be_greater));
                } else {
                    dialog.dismiss();
                    startDate = date1.getText().toString();
                    endDate = date2.getText().toString();
                }
            }
        });

        dialog.show();
    }

    void dateDialog(TextView textView, Calendar calendar) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog = new DatePickerDialog(TeacherFilterListActivity.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        String date = dateFormatter.format(calendar.getTime());
                        textView.setText(date);
                    }

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                DatePickerDialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        criteria = new ListCriteria();
    }

    @Override
    public void onStop() {
        super.onStop();
        nameWise.setChecked(false);
        enrollWise.setChecked(false);
        admissionDateWise.setChecked(false);
        male.setChecked(false);
        female.setChecked(false);
        name.setVisibility(View.GONE);
        enrolmentId.setVisibility(View.GONE);
        name.setText("");
        enrolmentId.setText("");
        active.setChecked(true);
    }
}





