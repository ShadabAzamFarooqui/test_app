package com.app.mschooling.examination.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mschooling.class_section_subject.activity.ClassSectionSelectionActivity;
import com.app.mschooling.class_section_subject.activity.SubjectListActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectExaminationActivity extends BaseActivity {


    @BindView(R.id.clazz)
    TextView clazz;
    @BindView(R.id.exam)
    TextView exam;
    @BindView(R.id.classLayout)
    LinearLayout classLayout;
    @BindView(R.id.subject)
    TextView subject;
    @BindView(R.id.subjectLayout)
    LinearLayout subjectLayout;
    @BindView(R.id.examLayout)
    LinearLayout examLayout;
    @BindView(R.id.section)
    TextView section;
    @BindView(R.id.sectionLayout)
    LinearLayout sectionLayout;

    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.totalMark)
    EditText totalMark;

    String examId;
    String classId;
    String sectionId;
    String subjectId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exam);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.examination));
        sectionLayout.setVisibility(View.GONE);

        examId = getIntent().getStringExtra("id");
        exam.setText(getIntent().getStringExtra("name"));
        examLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ExaminationListActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        classLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), ClassSectionSelectionActivity.class), 200);
            }
        });

        subjectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (classId == null) {
                    dialogError(getResources().getString(R.string.please_select_class));
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), SubjectListActivity.class);
                intent.putExtra("classId", classId);
                intent.putExtra("sectionId", sectionId);
                startActivityForResult(intent, 300);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exam.getText().toString().trim().isEmpty()) {
                    dialogError(getResources().getString(R.string.please_enter_exam));
                    return;
                }
                if (clazz.getText().toString().trim().isEmpty()) {
                    dialogError(getResources().getString(R.string.please_select_class));
                    return;
                }
                if (subject.getText().toString().trim().isEmpty()) {
                    dialogError(getResources().getString(R.string.please_select_subject));
                    return;
                }
                if (subject.getText().toString().trim().isEmpty()) {
                    dialogError(getResources().getString(R.string.please_select_subject));
                    return;
                } if (totalMark.getText().toString().trim().isEmpty()) {
                    dialogError(getResources().getString(R.string.please_enter_tatal_mark));
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), AddStudentMarksActivity.class);
                intent.putExtra("examId", examId);
                intent.putExtra("examName", exam.getText().toString());
                intent.putExtra("classId", classId);
                intent.putExtra("className", clazz.getText().toString());
                intent.putExtra("sectionId", sectionId);
                intent.putExtra("sectionName", section.getText().toString());
                intent.putExtra("subjectId", subjectId);
                intent.putExtra("subjectName", subject.getText().toString());
                intent.putExtra("totalMark", totalMark.getText().toString());
                startActivity(intent);
            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {

                if (requestCode == 100) {
                    examId = data.getStringExtra("id");
                    exam.setText(data.getStringExtra("name"));
                }
                if (requestCode == 200) {
                    classId = data.getStringExtra("classId");
                    clazz.setText(data.getStringExtra("className"));
                    sectionId = data.getStringExtra("sectionId");
                    section.setText(data.getStringExtra("sectionName"));
                    sectionLayout.setVisibility(View.VISIBLE);

                }
                if (requestCode == 300) {
                    subjectId = data.getStringExtra("id");
                    subject.setText(data.getStringExtra("name"));
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
