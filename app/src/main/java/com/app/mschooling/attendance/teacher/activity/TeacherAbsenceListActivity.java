package com.app.mschooling.attendance.teacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.attendance.teacher.adapter.TeacherAbsenceListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.attendance.GetTeacherAttendanceResponse;
import com.mschooling.transaction.response.attendance.TeacherAttendanceResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class TeacherAbsenceListActivity extends BaseActivity {

    LinearLayout noRecord;
    EditText search;
    RecyclerView recycler_view;
    TeacherAbsenceListAdapter adapter;
    List<TeacherAttendanceResponse> responseList;
    Button submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_list);
        Intent intent = getIntent();
        search = findViewById(R.id.search);
        recycler_view = findViewById(R.id.recycler_view);
        noRecord = findViewById(R.id.noRecord);
        submit = findViewById(R.id.submit);
        submit.setVisibility(View.GONE);
        toolbarClick(getString(R.string.tool_absence));


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    TeacherAbsenceListAdapter.responseList = responseList;
                    TeacherAbsenceListAdapter.filteredList = responseList;
                    adapter.getFilter().filter(s.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        apiCallBack(getApiCommonController().getAbsence(Helper.getCurrentDate(), Common.Attendance.A));

    }


    @Subscribe
    public void getClassList(GetTeacherAttendanceResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            try {
                responseList = response.getTeacherAttendanceWrapperResponse().getTeacherAttendanceResponses();
                recycler_view.setHasFixedSize(true);
                recycler_view.setFocusable(false);
                recycler_view.setNestedScrollingEnabled(false);
                recycler_view.setLayoutManager(new GridLayoutManager(this, 2));
                adapter = new TeacherAbsenceListAdapter(this, responseList);
                recycler_view.setAdapter(adapter);

                if (responseList.size() == 0) {
                    noRecord.setVisibility(View.VISIBLE);
                    recycler_view.setVisibility(View.GONE);
                } else {
                    noRecord.setVisibility(View.GONE);
                    recycler_view.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                noRecord.setVisibility(View.VISIBLE);
                recycler_view.setVisibility(View.GONE);
            }

        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


}





