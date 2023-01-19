package com.app.mschooling.quiz.activity.student;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.quiz.adapter.StudentQuestionNoAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventClickQuestions;
import com.app.mschooling.other.QuestionOptionInterface;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.quiz.AddStudentSubmitQuizRequest;
import com.mschooling.transaction.request.quiz.SubmitQuizRequest;
import com.mschooling.transaction.response.quiz.AddStudentSubmitQuizResponse;
import com.mschooling.transaction.response.quiz.ChoiceResponse;
import com.mschooling.transaction.response.quiz.GetStudentQuizQuestionResponse;
import com.mschooling.transaction.response.quiz.StudentQuizQuestionResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.iwgang.countdownview.CountdownView;

public class QuizStudentQuestionsListActivity extends BaseActivity implements QuestionOptionInterface {
    private TextView tvTitle, tvTime, tvEndTest, tvSaveNext;
    private LinearLayout btClearResponse, btSaveNext, back, layoutTime, layoutBottom;
    private RecyclerView recyclerView;
    private ArrayList<StudentQuizQuestionResponse> questionList;
    private StudentQuestionNoAdapter studentQuestionNoAdapter;
    private int constantPos = 0;
    public static int constChangePos = -1;
    public FragmentManager fragmentManager;
    private List<SubmitQuizRequest> submitQuizRequests = new ArrayList();
    private String quizId,subjectId;
    CountdownView countdownView;
    private Map<String, String> requestMap = new HashMap<>();
    QuestionOptionInterface questionOptionInterface;
    Fragment fragment;

    GetStudentQuizQuestionResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_questions_student_list);
        questionOptionInterface = this;
        questionList = new ArrayList<>();
        initView();
        setListeners();
        Intent intent = getIntent();
        quizId = intent.getStringExtra("quizId");
        subjectId = intent.getStringExtra("subjectId");
        toolbarClick(getString(R.string.quiz));
        fragment = new StudentQuizProceedFragment(QuizStudentQuestionsListActivity.this, questionOptionInterface);
        changeFragment(fragment, "proceed_fragment");
    }

    private void initView() {
        tvTitle = findViewById(R.id.tvTitle);
        tvTime = findViewById(R.id.tvTime);
        tvEndTest = findViewById(R.id.tvEndTest);
        btClearResponse = findViewById(R.id.btClearResponse);
        btSaveNext = findViewById(R.id.btSaveNext);
        tvSaveNext = findViewById(R.id.tvSaveNext);
        back = findViewById(R.id.back);
        back.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recyclerView);
        countdownView = findViewById(R.id.countdownView);
        layoutTime = findViewById(R.id.layoutTime);
        layoutBottom = findViewById(R.id.layoutBottom);
    }

    private void setListeners() {
        tvEndTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAlert(getString(R.string.end_this_test), getString(R.string.no), getString(R.string.yes), true, false);
            }
        });

        btSaveNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionList.get(constantPos).isSelected()) {
                    for (int i = 0; i < questionList.get(constantPos).getChoiceResponses().size(); i++) {
                        if (questionList.get(constantPos).getChoiceResponses().get(i).getYesNo() != null && questionList.get(constantPos).getChoiceResponses().get(i).getYesNo().equals(Common.YesNo.YES)) {
                            requestMap.put("" + constantPos, questionList.get(constantPos).getChoiceResponses().get(i).getId());
                            break;
                        }
                    }
                }
                if (constantPos < questionList.size() - 1) {
                    constantPos++;
                    constChangePos = constantPos;
                    changeFragment(new StudentQuestionChangeFragment(QuizStudentQuestionsListActivity.this, constantPos, questionList), "question_fragment");
                    studentQuestionNoAdapter.notifyDataSetChanged();
                }

                if (tvSaveNext.getText().toString().equalsIgnoreCase("Submit")) {
                    Dialog dialog = new Dialog(QuizStudentQuestionsListActivity.this);
                    dialog.getWindow().requestFeature(1);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(false);
                    TextView message = dialog.findViewById(R.id.message);
                    TextView submit = dialog.findViewById(R.id.submit);
                    TextView cancel = dialog.findViewById(R.id.cancel);
                    message.setText(R.string.submit_this_test);
                    submit.setText(getString(R.string.submit));
                    cancel.setText(getString(R.string.cancel));

                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            apiHit();
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
                if (constantPos == questionList.size() - 1) {
                    tvSaveNext.setText(getString(R.string.submit));
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        if (response != null) {
            dialogAlert( getString(R.string.end_this_test), getString(R.string.no), getString(R.string.yes), true, false);
        }else {
            finish();
        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    public void getQuizList(GetStudentQuizQuestionResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            if (response.getStudentQuizQuestionResponses().size() > 0) {
                toolbarClick(getString(R.string.questions));
                layoutBottom.setVisibility(View.VISIBLE);
                layoutTime.setVisibility(View.VISIBLE);

                questionList.addAll(response.getStudentQuizQuestionResponses());
                recyclerView.setLayoutManager(new LinearLayoutManager(QuizStudentQuestionsListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setNestedScrollingEnabled(false);
                studentQuestionNoAdapter = new StudentQuestionNoAdapter(QuizStudentQuestionsListActivity.this, response.getStudentQuizQuestionResponses(), constantPos);
                recyclerView.setAdapter(studentQuestionNoAdapter);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                changeFragment(new StudentQuestionChangeFragment(QuizStudentQuestionsListActivity.this, constantPos, questionList), "question_fragment");
                //Time set
                int t = Integer.parseInt(response.getTime());
                int hour = t / 60;
                int minute = t % 60;
                if (hour != 0 && minute != 0) {
                    tvTime.setText(hour + getString(R.string.hour) + minute + getString(R.string.minute));
                } else {
                    if (hour == 0) {
                        tvTime.setText("" + minute + getString(R.string.minute));
                    } else {
                        tvTime.setText(hour + getString(R.string.hour));
                    }
                }
                countdownView.setTag("test22");
                long time22 = (long) 60 * t * 1000;
                countdownView.start(time22);
                countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                    @Override
                    public void onEnd(CountdownView cv) {
                        Object tag = cv.getTag();

                        Dialog dialog = new Dialog(QuizStudentQuestionsListActivity.this);
                        dialog.getWindow().requestFeature(1);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        dialog.setContentView(R.layout.dialog_alert);
                        dialog.setCancelable(false);
                        TextView message = dialog.findViewById(R.id.message);
                        TextView submit = dialog.findViewById(R.id.submit);
                        TextView cancel = dialog.findViewById(R.id.cancel);
                        message.setText(R.string.test_time_up);
                        submit.setText(getString(R.string.submit));
                        cancel.setVisibility(View.GONE);

                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                apiHit();
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
                });
                if (questionList.size() == 1) {
                    tvSaveNext.setText(getString(R.string.submit));
                }
            }
        } else {
            dialogFailedFinish(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void response(EventClickQuestions response) {
        if (response != null) {
            constantPos = response.getPosition();
            changeFragment(new StudentQuestionChangeFragment(QuizStudentQuestionsListActivity.this, constantPos, questionList), "question_fragment");
            if (constantPos == questionList.size() - 1) {
                tvSaveNext.setText(getString(R.string.submit));
            } else {
                tvSaveNext.setText(getString(R.string.save_amp_next));
            }
        }
    }

    public void changeFragment(Fragment fragment, String title) {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(title).replace(R.id.fragment_change_container, fragment).commit();
    }

    @Subscribe
    public void submitQuiz(AddStudentSubmitQuizResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            // dialogSuccess(response.getMessage().getMessage());
            Intent intent = new Intent(QuizStudentQuestionsListActivity.this, StudentQuizResultListActivity.class);
            intent.putExtra("quizId", quizId);
            startActivity(intent);
            finish();
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    void apiHit() {
        AddStudentSubmitQuizRequest mainRequest = new AddStudentSubmitQuizRequest();
        mainRequest.setReportId(response.getReportId());
        mainRequest.setId(quizId);
        mainRequest.setSubjectId(subjectId);
        mainRequest.setSubmitQuizRequests(funSubmitQuizRequests());
        apiCallBack(getApiCommonController().submitQuiz(mainRequest));
    }

    List<SubmitQuizRequest> funSubmitQuizRequests() {
        List<SubmitQuizRequest> submitQuizRequests = new ArrayList<>();
        for (Map.Entry<String, String> entry : requestMap.entrySet()) {
            SubmitQuizRequest request = new SubmitQuizRequest();
            request.setQuestionId(questionList.get(Integer.parseInt(entry.getKey())).getId());
            request.setChoiceId(entry.getValue());
            submitQuizRequests.add(request);
        }
        return submitQuizRequests;
    }

    @Override
    public void selectIssue(int position, String answer, ChoiceResponse data) {
        if (answer.equalsIgnoreCase("proceed")) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction trans = manager.beginTransaction();
            trans.remove(fragment);
            trans.commit();
            manager.popBackStack();
            apiCallBack(getApiCommonController().getStudentQuizQuestionList(quizId,subjectId));
        } else {
            finish();
        }
    }
}
