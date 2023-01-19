package com.app.mschooling.quiz.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.quiz.adapter.QuizListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.quiz.DeleteQuizResponse;
import com.mschooling.transaction.response.quiz.GetQuizResponse;
import com.mschooling.transaction.response.quiz.QuizResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class QuizListActivity extends BaseActivity {
    LinearLayout noRecord;
    RecyclerView recyclerView;
    QuizListAdapter adapter;
    LinearLayout add;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    int pageNo;
    LinearLayoutManager layoutManager;
    List<QuizResponse> responseList;
    String intent;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);
        toolbarClick(getString(R.string.quiz));
        recyclerView = findViewById(R.id.recyclerView);
        noRecord = findViewById(R.id.noRecord);
        add = findViewById(R.id.add);
        intent=getIntent().getStringExtra("intent");

        if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.STUDENT.value())) {
            add.setVisibility(View.GONE);
        } else {
            add.setVisibility(View.VISIBLE);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizListActivity.this, CreateQuizActivity.class);
                startActivity(intent);
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstCompletelyVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            pageNo++;
                            apiCallBack(getApiCommonController().getQuizList("" + pageNo));
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().getQuizList("0"));
    }

    @Subscribe
    public void getQuizList(GetQuizResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {

            if (pageNo == 0) {
                responseList = response.getQuizResponses();
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                layoutManager=new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new QuizListAdapter(this, response.getQuizResponses(), intent);
                recyclerView.setAdapter(adapter);

                if (response.getQuizResponses().size() == 0) {
                    noRecord.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    noRecord.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                responseList.addAll(response.getQuizResponses());
                adapter.notifyDataSetChanged();
                if (response.getQuizResponses().size() > 0) {
                    loading = true;
                } else {
                    loading = false;
                }
            }

        } else {
            dialogFailed(response.getMessage().getMessage());
        }
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
                apiCallBack(getApiCommonController().deleteQuiz(event.getId()));
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
    public void delete(DeleteQuizResponse response) {
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
}