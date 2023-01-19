package com.app.mschooling.examination.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.examination.adapter.AddStudentMarksAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.BuzzTextView;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.StudentExamination;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.examination.AddStudentExaminationRequest;
import com.mschooling.transaction.response.examination.AddExaminationResponse;
import com.mschooling.transaction.response.examination.DeleteExaminationResponse;
import com.mschooling.transaction.response.examination.GetStudentExaminationResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddStudentMarksActivity extends BaseActivity {


    @BindView(R.id.noRecord)
    LinearLayout noRecord;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    List<StudentExamination> responseList;
    AddStudentMarksAdapter adapter;
    @BindView(R.id.clazz)
    BuzzTextView clazz;
    @BindView(R.id.section)
    BuzzTextView section;
    @BindView(R.id.subject)
    BuzzTextView subject;
    @BindView(R.id.totalMark)
    BuzzTextView totalMark;
    @BindView(R.id.submit)
    Button submit;

    String examId;
    String classId;
    String sectionId;
    String subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student_marks);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.mark));
        toolbarClick(getIntent().getStringExtra("examName"));

        clazz.setText(getIntent().getStringExtra("className"));
        section.setText(getIntent().getStringExtra("sectionName"));
        subject.setText(getIntent().getStringExtra("subjectName"));
        totalMark.setText(getIntent().getStringExtra("totalMark"));


        examId=getIntent().getStringExtra("examId");
        classId=getIntent().getStringExtra("classId");
        sectionId=getIntent().getStringExtra("sectionId");
        subjectId=getIntent().getStringExtra("subjectId");
        apiCallBack(getApiCommonController().getMarksExaminationList(
                ParameterConstant.getRole(getApplicationContext()),
                examId,classId,sectionId,subjectId));


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddStudentExaminationRequest request=new AddStudentExaminationRequest();
                request.setId(examId);
                request.setClassId(classId);
                request.setSectionId(sectionId);
                request.setSubjectId(subjectId);
                request.setStudentExaminations(responseList);
                apiCallBack(getApiCommonController().addMarksExaminationList(ParameterConstant.getRole(getApplicationContext()),request));
            }
        });
    }


    @Subscribe
    public void getExamination(GetStudentExaminationResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            responseList = response.getStudentExaminations();
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            Map<Integer,Double> theory=new HashMap();
            Map<Integer,Double> practical=new HashMap();
            for(int i=0;i<responseList.size();i++){
                theory.put(i,responseList.get(i).getObtainTheoryMarks());
                practical.put(i,responseList.get(i).getObtainPracticalMarks());
            }
            adapter = new AddStudentMarksAdapter(this,
                    getIntent().getStringExtra("totalMark"),
                    responseList,recyclerView);
            recyclerView.setAdapter(adapter);
            if (response.getStudentExaminations().size() == 0) {
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


    EventDelete event;

    @Subscribe
    public void deleteEvent(EventDelete event) {
        this.event = event;
        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_delete_notice);
        dialog.setCancelable(false);
        TextView delete = dialog.findViewById(R.id.delete);
        TextView cancel = dialog.findViewById(R.id.cancel);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                apiCallBack(getApiCommonController().deleteExam(ParameterConstant.getRole(getApplicationContext()),event.getId()));
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

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    public void delete(DeleteExaminationResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            responseList.remove(event.getPosition());
            adapter.notifyDataSetChanged();
            if (responseList.size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
            } else {
                noRecord.setVisibility(View.GONE);
            }
            dialogSuccess(response.getMessage().getMessage());
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


    @Subscribe
    public void add(AddExaminationResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            dialogSuccess(response.getMessage().getMessage());
            apiCallBack(getApiCommonController().getMarksExaminationList(
                    ParameterConstant.getRole(getApplicationContext()),
                    examId,classId,sectionId,subjectId));
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }
}
