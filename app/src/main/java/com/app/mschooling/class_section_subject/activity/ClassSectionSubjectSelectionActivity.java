package com.app.mschooling.class_section_subject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.app.mschooling.com.databinding.ActivityClassSectionSubjectSelectionBinding;
import com.app.mschooling.homework.activity.HomeWorkListActivity;
import com.app.mschooling.syllabus.activity.SyllabusListActivity;
import com.app.mschooling.attendance.student.activity.MarkStudentAttendanceActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassSectionSubjectSelectionActivity extends BaseActivity {


    String classId, sectionId;
    String subjectId;

    ActivityClassSectionSubjectSelectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_class_section_subject_selection);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.select));
        binding.sectionLayout.setVisibility(View.GONE);


        binding.classLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), ClassSectionSelectionActivity.class), 200);
            }
        });

        binding.subjectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (classId == null) {
                    dialogError(getResources().getString(R.string.please_select_class));
                    setError(binding.classLayout, binding.clazz, getString(R.string.please_select_class));
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), SubjectListActivity.class);
                intent.putExtra("classId", classId);
                intent.putExtra("sectionId", sectionId);
                startActivityForResult(intent, 300);
            }
        });


        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValid = true;
                if (binding.clazz.getText().toString().isEmpty()) {
                    isValid = setError(binding.classLayout, binding.clazz, getString(R.string.please_select_class));
                }
                if (binding.subject.getText().toString().trim().isEmpty()) {
                    isValid = setError(binding.subjectLayout, binding.subject, getString(R.string.please_select_subject));
                }
                if (binding.subject.getText().toString().trim().isEmpty()) {
                    isValid = setError(binding.subjectLayout, binding.subject, getString(R.string.please_select_subject));
                }
                if (isValid) {
                    Intent intent = new Intent();
                    if (getIntent().getStringExtra("intent").equalsIgnoreCase("ShowAttendanceActivity")) {
                        intent = new Intent(getApplicationContext(), MarkStudentAttendanceActivity.class);
                    }
                    if (getIntent().getStringExtra("intent").equalsIgnoreCase("MarkStudentAttendanceActivity")) {
                        intent = new Intent(getApplicationContext(), MarkStudentAttendanceActivity.class);
                    }
                    if (getIntent().getStringExtra("intent").equalsIgnoreCase("HomeWorkListActivity")) {
                        intent = new Intent(getApplicationContext(), HomeWorkListActivity.class);
                    }
                    if (getIntent().getStringExtra("intent").equalsIgnoreCase("SyllabusListActivity")) {
                        intent = new Intent(getApplicationContext(), SyllabusListActivity.class);
                    }
                    intent.putExtra("classId", classId);
                    intent.putExtra("className", binding.clazz.getText().toString());
                    intent.putExtra("sectionId", sectionId);
                    intent.putExtra("sectionName", binding.section.getText().toString());
                    intent.putExtra("subjectId", subjectId);
                    intent.putExtra("subjectName", binding.subject.getText().toString());
                    startActivity(intent);
                } else {
                    dialogError(getString(R.string.please_check_form));
                }

            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                if (!data.getStringExtra("classId").equals(classId)) {
                    binding.subject.setText("");
                    binding.subject.setHint(getString(R.string.select_subject));
                }
                classId = data.getStringExtra("classId");
                binding.clazz.setText(data.getStringExtra("className"));
                sectionId = data.getStringExtra("sectionId");
                binding.section.setText(data.getStringExtra("sectionName"));
                binding.sectionLayout.setVisibility(View.VISIBLE);
            }
            if (requestCode == 300) {
                subjectId = data.getStringExtra("id");
                binding.subject.setText(data.getStringExtra("name"));
            }

        }


    }

}
