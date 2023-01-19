package com.app.mschooling.quiz.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.class_section_subject.adapter.OptionListAdapter;
import com.app.mschooling.quiz.adapter.AdminQuestionListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.quiz.DeleteQuizQuestionResponse;
import com.mschooling.transaction.response.quiz.GetQuizQuestionResponse;
import com.mschooling.transaction.response.quiz.QuizQuestionResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;

public class QuizAdminQuestionsListActivity extends BaseActivity implements OptionListAdapter.question_answer {
    LinearLayout noRecord, layout1;
    TextView tvQuesId;
    TextView heading;
    RecyclerView mRecyclerView;
    OptionListAdapter.question_answer question_answer_interface;
    public HashMap<Integer, String> map_question = null;
    public HashMap<Integer, String> map_option = null;
    Button submit;
    LinearLayout add;
    String quizId = "";

    AdminQuestionListAdapter adapter;
    List<QuizQuestionResponse> responseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_questions_admin_list);
        question_answer_interface = this;
        mRecyclerView = findViewById(R.id.recycler_view);
        heading = findViewById(R.id.heading);
        layout1 = findViewById(R.id.layout1);
        tvQuesId = findViewById(R.id.tvQuesId);
        noRecord = findViewById(R.id.noRecord);
        submit = findViewById(R.id.submit);
        add = findViewById(R.id.add);
        setListeners();
        Intent intent = getIntent();
        heading.setText(getIntent().getStringExtra("name"));
        quizId = intent.getStringExtra("id");
        toolbarClick(getString(R.string.questions));
    }

    private void setListeners() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(QuizAdminQuestionsListActivity.this, CreateQuestionsActivity.class);
                    intent.putExtra("quizId", quizId);
                    intent.putExtra("whereFrom", "add");
                    startActivity(intent);
                } catch (Exception e) {

                }
            }
        });
    }

    @Override
    protected void onResume() {
        apiCallBack(getApiCommonController().getAdminQuizQuestionList(quizId));
        super.onResume();
    }

    @Subscribe
    public void getQuestionList(GetQuizQuestionResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            responseList=response.getQuizQuestionResponses();
            layout1.setVisibility(View.VISIBLE);
            tvQuesId.setText("" + response.getQuizQuestionResponses().size());
            setAdapter(response.getQuizQuestionResponses());
            if (response.getQuizQuestionResponses().size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            } else {
                noRecord.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    private void setAdapter(List<QuizQuestionResponse> dataArrays) {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(false);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter=new AdminQuestionListAdapter(QuizAdminQuestionsListActivity.this, dataArrays,quizId,getIntent("isCreator"));
        mRecyclerView.setAdapter(adapter);
    }




    EventDelete event;

    @Subscribe
    public void deleteEvent(EventDelete event) {
        this.event = event;

        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_delete_notice);
        dialog.setCancelable(false);
        TextView delete =  dialog.findViewById(R.id.delete);
        TextView cancel =  dialog.findViewById(R.id.cancel);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                apiCallBack(getApiCommonController().deleteQuestion(quizId,event.getId()));
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();

    }

    @Subscribe
    public void deleteNotice(DeleteQuizQuestionResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            responseList.remove(event.getPosition());
            adapter.notifyDataSetChanged();
            if (responseList.size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
            } else {
                noRecord.setVisibility(View.GONE);
            }
            Toast.makeText(this, ""+response.getMessage().getMessage(), Toast.LENGTH_SHORT).show();
//            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    @Override
    public void selectedResult(HashMap<Integer, String> map_question1, HashMap<Integer, String> map_option1) {
        map_question = map_question1;
        map_option = map_option1;
    }
}
