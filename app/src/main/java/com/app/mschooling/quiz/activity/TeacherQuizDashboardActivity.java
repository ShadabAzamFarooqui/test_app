package com.app.mschooling.quiz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;

public class TeacherQuizDashboardActivity extends BaseActivity {

    LinearLayout quiz,result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_quiz_dashboard);
        toolbarClick(getString(R.string.quiz));
        quiz=findViewById(R.id.quiz);
        result=findViewById(R.id.result);

        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), QuizListActivity.class);
                intent.putExtra("intent","QuizAdminQuestionsListActivity");
                startActivity(intent);
            }
        });


        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectStudentQuizActivity.class);
                startActivity(intent);
            }
        });


    }
}