package com.app.mschooling.fee.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.fee.adapter.CommonStudentFeeListAdapter;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.fee.GetStudentFeeResponse;
import com.mschooling.transaction.response.fee.StudentFeesResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommonStudentFeeListActivity extends BaseActivity {

    @BindView(R.id.noRecord)
    LinearLayout noRecord;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    GetStudentFeeResponse response;
    CommonStudentFeeListAdapter adapter;

    String classId;
    String sectionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_fee_list);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.fees));
        classId = getIntent().getStringExtra("classId");
        sectionId = getIntent().getStringExtra("sectionId");
        apiCallBack(getApiCommonController().getStudentFeeList(ParameterConstant.getRole(getApplicationContext()), classId, sectionId));

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (adapter != null) {
                    adapter.filteredList = response.getStudentFeesResponses();
                    adapter.responseList = response.getStudentFeesResponses();
                    performFiltering(s.toString());
                }
            }
        });

        if (Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value())) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommonFeeListActivity.class);
                intent.putExtra("classId", classId);
                intent.putExtra("sectionId", sectionId);
                startActivity(intent);
            }
        });
    }


    @Subscribe
    public void getFeeList(GetStudentFeeResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
            adapter = new CommonStudentFeeListAdapter(this, response.getStudentFeesResponses());
            recyclerView.setAdapter(adapter);
            if (response.getStudentFeesResponses().size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
            } else {
                noRecord.setVisibility(View.GONE);
            }
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            apiCallBack(getApiCommonController().getStudentFeeList(ParameterConstant.getRole(getApplicationContext()), classId, sectionId));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    void performFiltering(String value) {
        try {


            if (adapter.filteredList == null) {
                adapter.filteredList = new ArrayList<>();
            }

            if (value.isEmpty()) {
                adapter.filteredList = adapter.responseList;
            } else {
                ArrayList<StudentFeesResponse> filteredList = new ArrayList<>();
                for (StudentFeesResponse studentResponse : adapter.responseList) {
                    if (studentResponse.getStudentBasicResponse().getEnrollmentId().startsWith(value) || studentResponse.getStudentBasicResponse().getEnrollmentId().contains(value)) {
                        filteredList.add(studentResponse);
                    } else if (studentResponse.getStudentBasicResponse().getfName().toLowerCase().startsWith(value) || studentResponse.getStudentBasicResponse().getfName().toLowerCase().contains(value)) {
                        filteredList.add(studentResponse);
                    } else if (studentResponse.getStudentBasicResponse().getlName().toLowerCase().startsWith(value) || studentResponse.getStudentBasicResponse().getlName().toLowerCase().contains(value)) {
                        filteredList.add(studentResponse);
                    } else if (studentResponse.getStudentBasicResponse().getMobile().startsWith(value) || studentResponse.getStudentBasicResponse().getMobile().contains(value)) {
                        filteredList.add(studentResponse);
                    }
                }
                adapter.filteredList = filteredList;
            }
            adapter.responseList = adapter.filteredList;
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}