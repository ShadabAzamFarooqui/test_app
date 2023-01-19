package com.app.mschooling.syllabus.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.class_section_subject.activity.ClassListActivity;
import com.app.mschooling.class_section_subject.activity.SubjectListActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivitySelectClassSubjectBinding;

public class SelectClassSubjectActivity extends BaseActivity {

    String classId, subjectId;

    ActivitySelectClassSubjectBinding binding;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_class_subject);
        toolbarClick(getResources().getString(R.string.syllabus));


        binding.classLayout.setOnClickListener(view -> startActivityForResult(new Intent(getApplicationContext(), ClassListActivity.class), 1));

        binding.subjectLayout.setOnClickListener(view -> {
            if (binding.clazz.getText().toString().isEmpty()) {
                dialogError(getString(R.string.please_select_class));
                setError(binding.classLayout, binding.clazz, getString(R.string.please_select_class));
                return;
            }
            Intent intent = new Intent(getApplicationContext(), SubjectListActivity.class);
            intent.putExtra("classId", classId);
            startActivityForResult(intent, 2);
        });

        binding.submit.setOnClickListener(v -> {

            boolean isValid = true;

            if (binding.clazz.getText().toString().isEmpty()) {

                isValid = setError(binding.classLayout, binding.clazz, getString(R.string.please_select_class));
            }
            if (binding.subject.getText().toString().isEmpty()) {

                isValid = setError(binding.subjectLayout, binding.subject, getString(R.string.please_select_subject));

            }
            if (isValid) {
                Intent intent = new Intent(getApplicationContext(), SyllabusListActivity.class);
                intent.putExtra("classId", classId);
                intent.putExtra("className", binding.clazz.getText().toString());
                intent.putExtra("subjectId", subjectId);
                intent.putExtra("subjectName", binding.subject.getText().toString());
                startActivity(intent);

            } else {
                dialogError(getString(R.string.please_check_form));
            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (!data.getStringExtra("id").equals(classId)) {
                    binding.subject.setText("");
                }
                classId = data.getStringExtra("id");
                binding.clazz.setText(data.getStringExtra("name"));
            }
            if (requestCode == 2) {
                subjectId = data.getStringExtra("id");
                binding.subject.setText(data.getStringExtra("name"));
            }
        }

    }
}
