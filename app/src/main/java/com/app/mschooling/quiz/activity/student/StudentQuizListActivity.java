package com.app.mschooling.quiz.activity.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.quiz.adapter.StudentQuizListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.quiz.GetStudentQuizResponse;
import com.mschooling.transaction.response.quiz.StudentQuizResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentQuizListActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noRecord)
    LinearLayout noRecord;
 @BindView(R.id.add)
    LinearLayout add;

    List<StudentQuizResponse> responseList;
    StudentQuizListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_quiz_list);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.quiz));
        apiCallBack(getApiCommonController().getQuizListStudent(getIntent("subjectId")));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),StudentQuizResultListActivity.class);
                intent.putExtra("subjectId",getIntent("subjectId"));
                startActivity(intent);
            }
        });
    }


    @Subscribe
    public void get(GetStudentQuizResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            responseList = response.getStudentQuizResponses();
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new StudentQuizListAdapter(this, responseList, getIntent().getStringExtra("subjectId"));
            recyclerView.setAdapter(adapter);
            if (responseList.size()==0){
                noRecord.setVisibility(View.VISIBLE);
            }else {
                noRecord.setVisibility(View.GONE);
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }
}