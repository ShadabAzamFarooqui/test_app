package com.app.mschooling.timetable.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.timetable.adapter.TeacherClassAllocationAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.Allocation;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.teacher.GetAllocationResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeacherTimeTableAllocationActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noRecord)
    LinearLayout noRecord;
    @BindView(R.id.days)
    Spinner days;


    List<Allocation> allocations=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wallet2);
        ButterKnife.bind(this);
        toolbarClick( getString(R.string.tool_timetable));

        days.setSelection(Helper.getDay());


        apiCallBack( getApiCommonController().getAllocation(getIntent().getStringExtra("enrollmentId")));

        days.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<Allocation> al=new ArrayList<>();
                if (days.getSelectedItem().toString().equalsIgnoreCase("All")){
                    setAdapter(allocations);
                }else {
                    for (int i=0;i<allocations.size();i++){
                        if (days.getSelectedItem().toString().equals(allocations.get(i).getDay().value())){
                            al.add(allocations.get(i));
                        }
                    }
                    setAdapter(al);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }




    @Subscribe
    public void getTeacherList(GetAllocationResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            allocations=response.getAllocationResponses().get(0).getAllocations();
            List<Allocation> al=new ArrayList<>();
            for (int i=0;i<allocations.size();i++){
                if (days.getSelectedItem().toString().equals(allocations.get(i).getDay().value())){
                    al.add(allocations.get(i));
                }
            }
            setAdapter(al);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }



    void setAdapter(List<Allocation> allocations){
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new TeacherClassAllocationAdapter(this, allocations));

        if (allocations.size()==0){
            noRecord.setVisibility(View.VISIBLE);
        }else {
            noRecord.setVisibility(View.GONE);
        }
    }


}

