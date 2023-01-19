package com.app.mschooling.meeting.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;

import com.app.mschooling.students.list.activity.StudentsListActivity;
import com.app.mschooling.teachers.list.activity.TeacherListActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.google.gson.Gson;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.filter.ListCriteria;
import com.mschooling.transaction.request.discussion.AddDiscussionRequest;
import com.mschooling.transaction.response.discussion.AddDiscussionResponse;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateDiscussionActivity extends BaseActivity {
    Spinner spinnerRole;
    EditText subject;
    TextView student, teacher, date;
    LinearLayout teacherLayout, studentLayout;
    RelativeLayout dateLayout;
    Button submit;
    String id;
    ListCriteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_create);
        toolbarClick(getString(R.string.discussion));
        findIds();
        setListener();
        criteria = new ListCriteria();
        criteria.setState(Common.State.ACTIVATED);
    }

    private void findIds() {
        spinnerRole = findViewById(R.id.spinnerRole);
        subject = findViewById(R.id.subject);
        teacherLayout = findViewById(R.id.teacherLayout);
        teacher = findViewById(R.id.teacher);
        student = findViewById(R.id.student);
        studentLayout = findViewById(R.id.studentLayout);
        dateLayout = findViewById(R.id.dateLayout);
        date = findViewById(R.id.date);
        submit = findViewById(R.id.submit);

    }

    private void setListener() {
        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerRole.getSelectedItemPosition() == 0) {
                    teacherLayout.setVisibility(View.GONE);
                    studentLayout.setVisibility(View.GONE);
                }
                if (spinnerRole.getSelectedItemPosition() == 1) {
                    teacherLayout.setVisibility(View.VISIBLE);
                    studentLayout.setVisibility(View.GONE);
                }
                if (spinnerRole.getSelectedItemPosition() == 2) {
                    teacherLayout.setVisibility(View.GONE);
                    studentLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        studentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), StudentsListActivity.class);
                intent.putExtra("intent", "StudentsListActivity");
                intent.putExtra("criteria", new Gson().toJson(criteria));
                startActivityForResult(intent, 100);
            }
        });
        teacherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TeacherListActivity.class);
                intent.putExtra("whereFrom", "LiveMeeting");
                intent.putExtra("criteria", new Gson().toJson(criteria));
                startActivityForResult(intent, 101);
            }
        });

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Integer.valueOf(Helper.getCurrentYear()) - 2, Integer.valueOf(Helper.getCurrentMonth()) - 1, Integer.valueOf(Helper.getCurrentDay()));

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateDiscussionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf1 = new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.getDefault());
//                        date.setText(sdf1.format(myCalendar.getTime()));
                        timePicker(sdf1.format(myCalendar.getTime()));
                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subject.getText().toString().trim().isEmpty()) {
                    dialogError(getString(R.string.enter_meeting_subject));
                    return;
                }
                if (subject.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.enter_meeting_subject));
                    return;
                }
                if (spinnerRole.getSelectedItemPosition() == 0) {
                    dialogError(getString(R.string.select_role));
                    return;
                }

                if (spinnerRole.getSelectedItemPosition() == 1) {
                    if (teacher.getText().toString().isEmpty()) {
                        dialogError(getString(R.string.select_teacher));
                        return;
                    }
                }
                if (spinnerRole.getSelectedItemPosition() == 2) {
                    if (student.getText().toString().isEmpty()) {
                        dialogError(getString(R.string.select_student));
                        return;
                    }
                }
                if (date.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.select_date_w_star));
                    return;
                }
                AddDiscussionRequest request = new AddDiscussionRequest();
                request.setDateTime(date.getText().toString());
                request.setEnrollmentId(id);
                request.setSubject(subject.getText().toString());
                apiCallBack(getApiCommonController().addDiscussion(request));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                id = data.getStringExtra("id");
                if (requestCode == 100) {
                    student.setText(data.getStringExtra("name"));
                }
                if (requestCode == 101) {
                    teacher.setText(data.getStringExtra("name"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    void timePicker(String d) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String h = "" + hourOfDay;
                        String m = "" + minute;
                        if (hourOfDay < 10) {
                            h = "0" + hourOfDay;
                        }
                        if (minute < 10) {
                            m = "0" + minute;
                        }
                        String dd = d.concat(" " + h + ":" + m);
                        date.setText(dd);
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    @Subscribe
    public void notice(AddDiscussionResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            dialogSuccessFinish(response.getMessage().getMessage());
            finish();
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }
}
