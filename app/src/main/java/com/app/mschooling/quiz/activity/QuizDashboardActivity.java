package com.app.mschooling.quiz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizDashboardActivity extends BaseActivity {

    @BindView(R.id.quiz)
    RelativeLayout quiz;

    @BindView(R.id.assignQuiz)
    RelativeLayout assignQuiz;

    @BindView(R.id.assignment)
    RelativeLayout assignment;

    @BindView(R.id.result)
    RelativeLayout result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_dashboard);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.quiz));
        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QuizListActivity.class);
                intent.putExtra("intent","QuizAdminQuestionsListActivity");
                startActivity(intent);
            }
        });
        assignQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AssignQuizActivity.class);
                startActivity(intent);
            }
        }); assignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QuizListActivity.class);
                intent.putExtra("intent", "AssignedQuizActivity");
                startActivity(intent);
            }
        });result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectStudentQuizActivity.class);
                startActivity(intent);
            }
        });
    }
}