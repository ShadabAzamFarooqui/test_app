package com.app.mschooling.quiz.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.mschooling.class_section_subject.activity.ClassSectionSelectionActivity;
import com.app.mschooling.class_section_subject.activity.SubjectListActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.quiz.AddAssignQuizRequest;
import com.mschooling.transaction.response.quiz.AddQuizMappingResponse;

import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AssignQuizActivity extends BaseActivity {

    LinearLayout classLayout, subjectLayout, sectionLayout,quizLayout;
    TextView clazz, subject, quiz;
    String classId,sectionId, subjectId;
    TextView section;
    Button submit;
    TextView totalMarks, totalAttempts, totalTime;
    String quizId;
    TextView startTime, endTime;

    Date start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_assign);
        Intent intent = getIntent();
        toolbarClick(getString(R.string.assign_quiz));

        clazz = findViewById(R.id.clazz);
        classLayout = findViewById(R.id.classLayout);
        subjectLayout = findViewById(R.id.subjectLayout);
        sectionLayout = findViewById(R.id.sectionLayout);
        quizLayout = findViewById(R.id.quizLayout);
        subject = findViewById(R.id.subject);
        quiz = findViewById(R.id.quiz);
        totalMarks = findViewById(R.id.totalMarks);
        totalAttempts = findViewById(R.id.totalAttempts);
        totalTime = findViewById(R.id.totalTime);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        section = findViewById(R.id.section);
        submit = findViewById(R.id.submit);

        startTime.setText(Helper.getCurrentDateTime());
        endTime.setText(Helper.getTomorrowDateTime());

        sectionLayout.setVisibility(View.GONE);

        classLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), ClassSectionSelectionActivity.class), 1);
            }
        });
        quizLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), QuizListActivity.class), 3);
            }
        });


        subjectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clazz.getText().toString().isEmpty()) {
                    Toast.makeText(AssignQuizActivity.this, getString(R.string.please_select_class), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), SubjectListActivity.class);
                intent.putExtra("classId", classId);
                startActivityForResult(intent, 2);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (start!=null&&end!=null){
                    if (start.compareTo(end)==-1) {
                        dialogError(getString(R.string.start_time_cant_less));
                        return;
                    }
                }*/


                if (quiz.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_select_quiz));
                    return;
                }
                if (clazz.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_select_class));
                    return;
                }
                if (subject.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_select_subject));
                    return;
                }
                if (totalMarks.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_enter_marks));
                    return;
                }
                if (totalAttempts.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_enter_no_of_attempts));
                    return;
                }
                if (totalTime.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_enter_time));
                    return;
                }

                AddAssignQuizRequest request = new AddAssignQuizRequest();
                request.setId(quizId);
                request.setClassId(classId);
                request.setSectionId(sectionId);
                request.setSubjectId(subjectId);
                request.setTotalMark(Integer.parseInt(totalMarks.getText().toString()));
                request.setTotalAttempts(Integer.parseInt(totalAttempts.getText().toString()));
                request.setTotalTime(Integer.parseInt(totalTime.getText().toString()));
                request.setStartTime(startTime.getText().toString());
                request.setEndTime(endTime.getText().toString());
                apiCallBack(getApiCommonController().quizMapping(request));
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    classId = data.getStringExtra("classId");
                    clazz.setText(data.getStringExtra("className"));
                    sectionId = data.getStringExtra("sectionId");
                    section.setText(data.getStringExtra("sectionName"));
                    sectionLayout.setVisibility(View.VISIBLE);
                } else if (requestCode == 2) {
                    subjectId = data.getStringExtra("id");
                    subject.setText(data.getStringExtra("name"));
                } else if (requestCode == 3) {
                    quizId = data.getStringExtra("id");
                    quiz.setText(data.getStringExtra("name"));
                }
            }
        } catch (Exception ex) {
        }
    }


    @Subscribe
    public void quizMapping(AddQuizMappingResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    Calendar calendar;

    public void showDateTimePicker(TextView textView, String type) {
        final Calendar currentDate = Calendar.getInstance();
        calendar = Calendar.getInstance();
        new DatePickerDialog(AssignQuizActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(AssignQuizActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        textView.setText(dateFormat.format(calendar.getTime()));
                        if (type.equals("start")) {
                            start = calendar.getTime();
                        } else {
                            end = calendar.getTime();
                        }
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }


    public void startTime(View view) {
        showDateTimePicker(startTime, "start");
    }

    public void endTime(View view) {
        showDateTimePicker(endTime, "end");
    }
}
