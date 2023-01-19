package com.app.mschooling.quiz.activity.student;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.quiz.adapter.QuizResultListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.quiz.GetStudentSubmitQuizResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentQuizResultListActivity extends BaseActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noRecord)
    LinearLayout noRecord;

    QuizResultListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_quiz_result_java);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.scorecard));
        apiCallBack(getApiCommonController().getStudentQuizResult());
    }


    @Subscribe
    public void getQuizResult(GetStudentSubmitQuizResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new QuizResultListAdapter(this, response.getSubmitQuizResponses());
            recyclerView.setAdapter(adapter);
            if (response.getSubmitQuizResponses().size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
            } else {
                noRecord.setVisibility(View.GONE);
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }
}