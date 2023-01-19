package com.app.mschooling.quiz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.mschooling.class_section_subject.activity.ClassListActivity;
import com.app.mschooling.students.list.activity.StudentsListActivity;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.google.gson.Gson;
import com.mschooling.transaction.filter.ListCriteria;
import com.mschooling.transaction.response.student.SectionResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectStudentQuizActivity extends BaseActivity {


    @BindView(R.id.quiz)
    TextView quiz;

    @BindView(R.id.quizLayout)
    LinearLayout quizLayout;

    @BindView(R.id.classLayout)
    LinearLayout classLayout;

     @BindView(R.id.sectionLayout)
    LinearLayout sectionLayout;

    @BindView(R.id.section)
    Spinner section;

    @BindView(R.id.clazz)
    TextView clazz;

    @BindView(R.id.studentLayout)
    LinearLayout studentLayout;

    @BindView(R.id.student)
    TextView student;

    @BindView(R.id.submit)
    Button submit;


    String quizId;
    String enrollmentId;
    String classId;

    List<SectionResponse> sectionResponses;
    List<String> listSection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_quiz);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.quiz));

        sectionLayout.setVisibility(View.GONE);

        quizLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), QuizListActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        classLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(new Intent(getApplicationContext(), ClassListActivity.class), 1);
            }
        });


        studentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clazz.getText().toString().isEmpty()){
                    dialogError(getString(R.string.please_select_class));
                    return;
                }
                ListCriteria criteria=new ListCriteria();
                criteria.setClassIds(getList(classId));
                criteria.setSectionIds(getList(sectionResponses.get(section.getSelectedItemPosition()).getId()));

                Intent intent = new Intent(getApplicationContext(), StudentsListActivity.class);
                intent.putExtra("criteria", new Gson().toJson(criteria));
                intent.putExtra("intent", "StudentsListActivity");
                startActivityForResult(intent, 200);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quiz.getText().toString().trim().isEmpty()) {
                    dialogError(getResources().getString(R.string.please_enter_exam));
                    return;
                }
                if (student.getText().toString().trim().isEmpty()) {
                    dialogError(getResources().getString(R.string.please_select_student));
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), AdminQuizResultListActivity.class);
                intent.putExtra("id", quizId);
                intent.putExtra("enrollmentId", enrollmentId);
                startActivity(intent);


            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    sectionLayout.setVisibility(View.VISIBLE);
                    classId = data.getStringExtra("id");
                    clazz.setText(data.getStringExtra("name"));
                    listSection = new ArrayList<>();
                    sectionResponses = AppUser.getInstance().getSectionResponse();
                    for (int i = 0; i < sectionResponses.size(); i++) {
                        listSection.add(sectionResponses.get(i).getName());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listSection);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    section.setAdapter(spinnerAdapter);
                }
                if (requestCode == 100) {
                    quizId = data.getStringExtra("id");
                    quiz.setText(data.getStringExtra("name"));
                }
                if (requestCode == 200) {
                    enrollmentId = data.getStringExtra("id");
                    student.setText(data.getStringExtra("name"));
                }


            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    List<String> getList(String val){
        List<String> list=new ArrayList<>();
        list.add(val);
        return list;
    }

}
