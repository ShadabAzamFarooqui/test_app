package com.app.mschooling.quiz.activity.student;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.quiz.adapter.StudentMcqOptionAdapter;
import com.app.mschooling.quiz.adapter.StudentCorrectAnswerAdapter;
import com.app.mschooling.com.R;
import com.app.mschooling.other.QuestionOptionInterface;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.quiz.ChoiceResponse;
import com.mschooling.transaction.response.quiz.StudentQuizQuestionResponse;

import java.util.ArrayList;
import java.util.List;


@SuppressLint("ValidFragment")
public class StudentQuestionChangeFragment extends Fragment implements QuestionOptionInterface {
    QuizStudentQuestionsListActivity studentActivity;
    RecyclerView recyclerView,recyclerViewAnswer;
    TextView tvQuestionName;
    StudentQuizQuestionResponse data;
    private View view;
    private int constantPos;
    List<ChoiceResponse> arrOption;
    QuestionOptionInterface questionOptionInterface;
    private ArrayList<ChoiceResponse> arrCorrectOption;
    private ArrayList<StudentQuizQuestionResponse> questionList;
    StudentMcqOptionAdapter adapterMcq;

    @SuppressLint("ValidFragment")
    public StudentQuestionChangeFragment(QuizStudentQuestionsListActivity studentActivity, int constantPos, ArrayList<StudentQuizQuestionResponse> questionList) {
        this.studentActivity = studentActivity;
        this.data = questionList.get(constantPos);
        this.constantPos = constantPos;
        this.questionList = questionList;
        this.questionOptionInterface=this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null) {
            view=inflater.inflate(R.layout.student_question_change_fragment, container, false);
            initView(view);
        }
        return view;
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
        tvQuestionName = (TextView) view.findViewById(R.id.tvQuestionName);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerViewAnswer = (RecyclerView) view.findViewById(R.id.recyclerViewAnswer);
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(studentActivity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        arrOption = new ArrayList<>();
        arrOption.addAll(data.getChoiceResponses());
        tvQuestionName.setText("Q."+(constantPos+1)+" "+data.getName());
        setAdapter(arrOption);

        recyclerViewAnswer.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManagerAnswer = new LinearLayoutManager(studentActivity);
        recyclerViewAnswer.setLayoutManager(mLayoutManagerAnswer);
        recyclerViewAnswer.setItemAnimator(new DefaultItemAnimator());

    }

    void setAdapter(List<ChoiceResponse> list){
        adapterMcq = new StudentMcqOptionAdapter(studentActivity,list,questionOptionInterface);
        recyclerView.setAdapter(adapterMcq);

    }

    @Override
    public void selectIssue(int position, String answer, ChoiceResponse data) {
        arrCorrectOption = new ArrayList<>();
        questionList.get(constantPos).setSelected(true);
        for (int i=0;i<questionList.get(constantPos).getChoiceResponses().size();i++)
            questionList.get(constantPos).getChoiceResponses().get(i).setYesNo(Common.YesNo.NO);
        data.setYesNo(Common.YesNo.YES);
        questionList.get(constantPos).getChoiceResponses().set(position,data);
        adapterMcq.notifyDataSetChanged();
        data.setName(answer);
        arrCorrectOption.add(data);
        StudentCorrectAnswerAdapter adapter = new StudentCorrectAnswerAdapter(studentActivity,arrCorrectOption);
        recyclerViewAnswer.setAdapter(adapter);
    }
}
