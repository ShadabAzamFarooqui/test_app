package com.app.mschooling.timetable.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.timetable.adapter.TimeTableAdapter1;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.request.timetable.TimetableCriteria;
import com.mschooling.transaction.response.timetable.GetTimeTableResponseList;

import org.greenrobot.eventbus.Subscribe;

public class ClassTimeTableActivity extends BaseActivity {

    RecyclerView recyclerView;
    LinearLayout noRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_time_table);
        toolbarClick(getString(R.string.view_timetable));
        recyclerView = findViewById(R.id.recyclerView);
        noRecord = findViewById(R.id.noRecord);

    }

    @Override
    protected void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().getTimeTable(new TimetableCriteria()));
    }

    @Subscribe
    public void getTimeTable(GetTimeTableResponseList response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            if (response.getGetTimeTableResponses().size()>0){
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
                recyclerView.setAdapter( new TimeTableAdapter1(ClassTimeTableActivity.this, response));
            }else {
                noRecord.setVisibility(View.VISIBLE);
            }
        }else {
            dialogError(response.getMessage().getMessage());
        }
    }
}
