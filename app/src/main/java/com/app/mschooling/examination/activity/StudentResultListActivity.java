package com.app.mschooling.examination.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.examination.adapter.StudentResultListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.examination.GetStudentExaminationClassResultResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentResultListActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noRecord)
    LinearLayout noRecord;
    @BindView(R.id.submit)
    Button submit;

    StudentResultListAdapter adapter;
    GetStudentExaminationClassResultResponse response;

    String examId, classId,sectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_result_list);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.examination));
        submit.setVisibility(View.GONE);
        examId = getIntent("examId");
        classId = getIntent("classId");
        sectionId = getIntent("sectionId");
        apiCallBack(getApiCommonController().getStudentResultList(examId, classId,sectionId));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResultListPrintActivity.class);
                intent.putExtra("examName", getIntent("examName"));
                intent.putExtra("className", getIntent("className"));
                intent.putExtra("sectionName", getIntent("sectionName"));
                intent.putExtra("examId", examId);
                intent.putExtra("classId", classId);
                intent.putExtra("sectionId", sectionId);

                startActivity(intent);
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (adapter != null && response != null) {
                    adapter.filteredList = response.getStudentExaminationClassResultResponses();
                    adapter.responseList = response.getStudentExaminationClassResultResponses();
                    adapter.getFilter().filter(s.toString());
                }
            }
        });


    }


    @Subscribe
    public void getResponse(GetStudentExaminationClassResultResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            adapter = new StudentResultListAdapter(this, examId, response.getStudentExaminationClassResultResponses());
            recyclerView.setAdapter(adapter);

            if (response.getStudentExaminationClassResultResponses().size()>0){
                submit.setVisibility(View.VISIBLE);
                noRecord.setVisibility(View.GONE);
            }else {
                submit.setVisibility(View.GONE);
                noRecord.setVisibility(View.VISIBLE);
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }
}