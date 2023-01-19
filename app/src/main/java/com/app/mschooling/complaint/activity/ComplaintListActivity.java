package com.app.mschooling.complaint.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.complaint.adapter.ComplainListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.complain.ComplainResponse;
import com.mschooling.transaction.response.complain.GetComplainResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class ComplaintListActivity extends BaseActivity {

    LinearLayout add;
    LinearLayout noRecord;
    RecyclerView recyclerView;
    ComplainListAdapter adapter;
    List<ComplainResponse> responseList;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    int pageNo;
    LinearLayoutManager layoutManager;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_list);
        toolbarClick(getString(R.string.tool_complaints));
        add = findViewById(R.id.add);
        recyclerView = findViewById(R.id.recyclerView);
        noRecord = findViewById(R.id.noRecord);
        noRecord.setVisibility(View.GONE);
        layoutManager = new LinearLayoutManager(ComplaintListActivity.this);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())) {
                    startActivity(new Intent(getApplicationContext(), ComplaintReasonListActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), AddComplaintActivity.class));
                }

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
                try {
                    ComplainListAdapter.filteredList = responseList;
                    ComplainListAdapter.responseList = responseList;
                    adapter.getFilter().filter(s.toString());
                } catch (Exception e) {

                }

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
                    pageNo++;
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            apiCallBack(getApiCommonController().getComplain("" + pageNo));
                        }

                    }
                }
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        pageNo = 0;
        loading = true;
        apiCallBack(getApiCommonController().getComplain("0"));
    }

    @Subscribe
    public void getComplainList(GetComplainResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            if (pageNo == 0) {
                if (response.getComplainResponses().size() > 0) {
                    noRecord.setVisibility(View.GONE);
                } else {
                    noRecord.setVisibility(View.VISIBLE);
                }
                responseList = response.getComplainResponses();
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new ComplainListAdapter(this, responseList);
                recyclerView.setAdapter(adapter);
            } else {
                responseList.addAll(response.getComplainResponses());
                adapter.notifyDataSetChanged();
                if (response.getComplainResponses().size() > 0) {
                    loading = true;
                } else {
                    loading = false;
                }
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


}
