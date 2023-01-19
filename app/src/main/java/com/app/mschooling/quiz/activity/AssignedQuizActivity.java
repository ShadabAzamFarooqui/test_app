package com.app.mschooling.quiz.activity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.quiz.adapter.AssignedClassListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.UnAssignedQuizEvent;
import com.app.mschooling.utils.SnapHelper;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.quiz.AssignQuizClassResponse;
import com.mschooling.transaction.response.quiz.DeleteAssignQuizResponse;
import com.mschooling.transaction.response.quiz.GetAssignQuizResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssignedQuizActivity extends BaseActivity {

    @BindView(R.id.quizName)
    TextView quizName;
    @BindView(R.id.questionNo)
    TextView questionNo;
    @BindView(R.id.level)
    TextView level;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noRecord)
    LinearLayout noRecord;

    List<AssignQuizClassResponse> responseList;
    AssignedClassListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_quiz);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.assigned_quiz));
        quizName.setText(getIntent("name"));
        level.setText(getIntent("level"));
        questionNo.setText(getIntent("question"));
        apiCallBack(getApiCommonController().getAssignedQuiz(getIntent().getStringExtra("id")));
    }


    @Subscribe
    public void delete(DeleteAssignQuizResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            Toast.makeText(this, response.getMessage().getMessage(), Toast.LENGTH_SHORT).show();
            apiCallBack(getApiCommonController().getAssignedQuiz(getIntent().getStringExtra("id")));
            adapter.notifyDataSetChanged();
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void get(GetAssignQuizResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            responseList = new ArrayList<>();
            responseList.addAll(response.getAssignQuizResponse().getAssignQuizClassResponses());
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(true);
//            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            adapter = new AssignedClassListAdapter(this, responseList, null);
            recyclerView.setAdapter(adapter);
            LinearSnapHelper linearSnapHelper = new SnapHelper();
            linearSnapHelper.attachToRecyclerView(recyclerView);
            if (response.getAssignQuizResponse().getAssignQuizClassResponses().size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                noRecord.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    UnAssignedQuizEvent event;

    @Subscribe
    public void unAssigned(UnAssignedQuizEvent event) {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_unassigned);
        dialog.setCancelable(false);
        TextView yes = dialog.findViewById(R.id.yes);
        TextView cancel = dialog.findViewById(R.id.cancel);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                apiCallBack(getApiCommonController().unassignedQuiz(getIntent("id"), event.getId()));
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
}