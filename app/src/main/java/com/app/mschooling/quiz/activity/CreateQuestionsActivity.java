package com.app.mschooling.quiz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.quiz.adapter.AddQuestionOptionAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.google.gson.Gson;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.quiz.AddQuizQuestionRequest;
import com.mschooling.transaction.request.quiz.ChoiceRequest;
import com.mschooling.transaction.response.quiz.AddQuizQuestionResponse;
import com.mschooling.transaction.response.quiz.QuizQuestionResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class CreateQuestionsActivity extends BaseActivity {
    AddQuestionOptionAdapter addQuestionOptionAdapter;
    RecyclerView sectionRecyclerView;
    EditText etQuestionContent;
    Button submit;
    RelativeLayout addOptionLayout;
    List<ChoiceRequest> optionList;
    String quizId = "",whereFrom="";
    QuizQuestionResponse quizQuestionResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);
        Intent intent = getIntent();
        quizId = intent.getStringExtra("quizId");
        whereFrom = intent.getStringExtra("whereFrom");
        initVIew();
        setListeners();
        optionList = new ArrayList();
        if (whereFrom.equals("add")){
            toolbarClick(getString(R.string.create_questions));
            submit.setText(getString(R.string.submit));
            ChoiceRequest request = new ChoiceRequest();
            request.setName("");
            request.setYesNo(Common.YesNo.NO);
            optionList.add(request);
        }else {
            toolbarClick(getString(R.string.update_questions));
            submit.setText(getString(R.string.update));
            Gson gson = new Gson();
            String jsonString = intent.getStringExtra("data");
            quizQuestionResponse = gson.fromJson(jsonString, QuizQuestionResponse.class);
            etQuestionContent.setText(quizQuestionResponse.getName());
            for (int i=0;i<quizQuestionResponse.getChoiceResponses().size();i++){
                ChoiceRequest request = new ChoiceRequest();
                request.setName(quizQuestionResponse.getChoiceResponses().get(i).getName());
                request.setYesNo(quizQuestionResponse.getChoiceResponses().get(i).getYesNo());
                optionList.add(request);
            }
        }
        optionViewAction(optionList);

        addOptionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CreateQuestionsActivity.this.optionList.size() == 6) {
                    dialogError(getString(R.string.cant_create_more_options));
                    return;
                }
                ChoiceRequest request = new ChoiceRequest();
                request.setName("");
                request.setYesNo(Common.YesNo.NO);
                CreateQuestionsActivity.this.optionList.add(request);
                addQuestionOptionAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setListeners() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etQuestionContent.getText().toString().isEmpty()){
                    dialogError(getString(R.string.enter_question));
                    return;
                }
                AddQuizQuestionRequest request = new  AddQuizQuestionRequest();
                request.setId(quizId);
                if (whereFrom.equals("add")){
                    request.setQuestionId(null);
                }else {
                    request.setQuestionId(quizQuestionResponse.getId());
                }
                request.setName(etQuestionContent.getText().toString());
                Boolean bool = false;
                for (int i = 0; i < optionList.size(); i++) {
                    if (optionList.get(i).getName().trim().isEmpty()) {
                        dialogError(getString(R.string.enter_option)+ (i+1));
                        return;
                    }
                    if (optionList.get(i).getYesNo().equals(Common.YesNo.YES)){
                        bool = true;
                    }
                }
                if (!bool){
                    dialogError(getString(R.string.select_right_answer));
                    return;
                }
                request.setChoiceRequests(optionList);
                apiCallBack(getApiCommonController().addQuestion(request));
            }
        });
    }

    private void initVIew() {
        sectionRecyclerView = findViewById(R.id.sectionRecyclerView);
        etQuestionContent = findViewById(R.id.content);
        submit = findViewById(R.id.submit);
        addOptionLayout = findViewById(R.id.addOptionLayout);
    }

    private void optionViewAction(List<ChoiceRequest> optionList) {
        sectionRecyclerView.setHasFixedSize(true);
        sectionRecyclerView.setFocusable(false);
        sectionRecyclerView.setNestedScrollingEnabled(false);
        sectionRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        addQuestionOptionAdapter = new AddQuestionOptionAdapter(this, optionList);
        sectionRecyclerView.setAdapter(addQuestionOptionAdapter);
    }

    @Subscribe
    public void addQuestion(AddQuizQuestionResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            if (whereFrom.equals("add")){
                dialogSuccess(response.getMessage().getMessage());
                etQuestionContent.setText("");
                optionList = new ArrayList();
                ChoiceRequest request = new ChoiceRequest();
                request.setName("");
                request.setYesNo(Common.YesNo.NO);
                optionList.add(request);
                addQuestionOptionAdapter = new AddQuestionOptionAdapter(this, optionList);
                sectionRecyclerView.setAdapter(addQuestionOptionAdapter);
            }else {
                finish();
            }
        } else {
            dialogFailed( response.getMessage().getMessage());
        }
    }















}