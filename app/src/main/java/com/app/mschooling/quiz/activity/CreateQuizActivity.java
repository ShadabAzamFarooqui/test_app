package com.app.mschooling.quiz.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.quiz.AddQuizRequest;
import com.mschooling.transaction.response.quiz.AddQuizResponse;

import org.greenrobot.eventbus.Subscribe;

public class CreateQuizActivity extends BaseActivity {
    private EditText tvQuizName;
    private Spinner spQuizType;
    private Button submit;
    AddQuizRequest request = new AddQuizRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        initView();

        if (getIntent().getStringExtra("id")==null) {
            toolbarClick(getString(R.string.create_quiz));
        }else {
            toolbarClick(getString(R.string.update_quiz));
            tvQuizName.setText(getIntent().getStringExtra("name"));
            if (getIntent().getStringExtra("type").equalsIgnoreCase(getString(R.string.low))){
                spQuizType.setSelection(1);
            }else if (getIntent().getStringExtra("type").equalsIgnoreCase(getString(R.string.medium))){
                spQuizType.setSelection(2);
            }else if (getIntent().getStringExtra("type").equalsIgnoreCase(getString(R.string.high))){
                spQuizType.setSelection(3);
            }
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvQuizName.getText().toString().isEmpty()) {
                    dialogError(getString(R.string.please_enter_quiz_name));
                    return;
                }
                if (spQuizType.getSelectedItemPosition()==0) {
                    dialogError(getString(R.string.please_select_quiz_type));
                    return;
                }
                request.setName(tvQuizName.getText().toString());
                if (spQuizType.getSelectedItemPosition()==1){
                    request.setType(Common.Level.LOW.value());
                    request.setLevel(Common.Level.LOW);
                }
                if (spQuizType.getSelectedItemPosition()==2){
                    request.setType(Common.Level.MEDIUM.value());
                    request.setLevel(Common.Level.MEDIUM);
                }
                if (spQuizType.getSelectedItemPosition()==3){
                    request.setType(Common.Level.HIGH.value());
                    request.setLevel(Common.Level.HIGH);
                }
                request.setId(getIntent().getStringExtra("id"));
                apiCallBack(getApiCommonController().addQuiz(request));
            }
        });

    }

    private void initView() {
        tvQuizName = findViewById(R.id.tvQuizName);
        spQuizType = findViewById(R.id.spQuizType);
        submit = findViewById(R.id.submit);
    }

    @Subscribe
    public void addQuiz(AddQuizResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())){
            dialogSuccessFinish(response.getMessage().getMessage());
            tvQuizName.setText("");
            spQuizType.setSelection(0);
        }else {
            dialogError(response.getMessage().getMessage());
        }
    }
}