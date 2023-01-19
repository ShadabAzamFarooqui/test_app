package com.app.mschooling.quiz.activity.student;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.mschooling.com.R;
import com.app.mschooling.other.QuestionOptionInterface;


@SuppressLint("ValidFragment")
public class StudentQuizProceedFragment extends Fragment {
    QuizStudentQuestionsListActivity studentActivity;
    private View view;
    private TextView tvProceed,tvCancel;
    private QuestionOptionInterface questionOptionInterface;

    @SuppressLint("ValidFragment")
    public StudentQuizProceedFragment(QuizStudentQuestionsListActivity studentActivity,QuestionOptionInterface questionOptionInterface) {
        this.studentActivity = studentActivity;
        this.questionOptionInterface = questionOptionInterface;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null) {
            view=inflater.inflate(R.layout.student_quiz_proceed_fragment, container, false);
            initView(view);
            setListeners();
        }
        return view;
    }

    private void setListeners() {
        tvProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionOptionInterface.selectIssue(0,"proceed",null);
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionOptionInterface.selectIssue(1,"cancel",null);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void initView(View view) {
        tvProceed = (TextView) view.findViewById(R.id.tvProceed);
        tvCancel = (TextView) view.findViewById(R.id.tvCancel);


    }
}
