package com.app.mschooling.complaint.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.complaint.adapter.ComplaintReasonListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDeleteBase;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.complain.ComplainReasonResponse;
import com.mschooling.transaction.response.complain.DeleteComplainReasonResponse;
import com.mschooling.transaction.response.complain.GetComplainReasonResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class ComplaintReasonListActivity extends BaseActivity {


    LinearLayout add;
    RecyclerView recyclerView;
    ComplaintReasonListAdapter adapter;
    List<ComplainReasonResponse> response;
    LinearLayout noRecord;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_list);
        toolbarClick(getString(R.string.tool_complaint_reason));
        noRecord=findViewById(R.id.noRecord);
        add=findViewById(R.id.add);
        recyclerView=findViewById(R.id.recycler_view);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getApplicationContext(), AddComplaintReasonActivity.class));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().complainReasonList());

    }

    @Subscribe
    public void getReason(GetComplainReasonResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            this.response=response.getComplainReasonResponses();
            if (this.response.size()==0){
                noRecord.setVisibility(View.VISIBLE);
            }else {
                noRecord.setVisibility(View.GONE);
            }

            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
            adapter = new ComplaintReasonListAdapter(ComplaintReasonListActivity.this, this.response);
            recyclerView.setAdapter(adapter);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    EventDeleteBase event;
    @Subscribe
    public void deleteEvent(EventDeleteBase event){
       this.event=event;
       apiCallBack(getApiCommonController().deleteComplainReason(response.get(event.getPosition()).getId()));
    }


    @Subscribe
    public void deleteResponse(DeleteComplainReasonResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
           this.response.remove(event.getPosition());
           adapter.notifyDataSetChanged();
            if (this.response.size()==0){
                noRecord.setVisibility(View.VISIBLE);
            }else {
                noRecord.setVisibility(View.GONE);
            }
        } else {
           dialogFailed(response.getMessage().getMessage());
        }
    }


}

