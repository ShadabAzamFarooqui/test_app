package com.app.mschooling.meeting.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.meeting.adapter.DiscussionListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.discussion.DiscussionResponse;
import com.mschooling.transaction.response.discussion.GetDiscussionResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class DiscussionListActivity extends BaseActivity {
    LinearLayout add;
    LinearLayout noRecord;
    RecyclerView recyclerView;
    DiscussionListAdapter adapter;
    List<DiscussionResponse> responseList;

    int pastVisibleItems, visibleItemCount, totalItemCount;
    int pageNo;
    LinearLayoutManager layoutManager;
    private boolean loading = true;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        toolbarClick(getString(R.string.discussion));
        add = findViewById(R.id.add);
        recyclerView = findViewById(R.id.recyclerView);
        noRecord = findViewById(R.id.noRecord);
        noRecord.setVisibility(View.GONE);
        layoutManager = new LinearLayoutManager(DiscussionListActivity.this);

        if (Preferences.getInstance(DiscussionListActivity.this).getROLE().equals(Common.Role.STUDENT.value())) {
            add.setVisibility(View.GONE);
        } else {
            add.setVisibility(View.VISIBLE);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateDiscussionActivity.class));
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
                    DiscussionListAdapter.filteredList = responseList;
                    DiscussionListAdapter.responseList = responseList;
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
                    pastVisibleItems = layoutManager.findFirstCompletelyVisibleItemPosition();
                    pageNo++;
                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
//                            ApiCallService.action(DiscussionListActivity.this,""+pageNo,ApiCallService.Action.ACTION_GET_APPLICATION);
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
        apiCallBack(getApiCommonController().getDiscussion(pageNo));
    }

    @Subscribe
    public void getApplicationList(GetDiscussionResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            if (pageNo == 0) {
                if (response.getDiscussionResponses().size() > 0) {
                    noRecord.setVisibility(View.GONE);
                } else {
                    noRecord.setVisibility(View.VISIBLE);
                }
                responseList = response.getDiscussionResponses();
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new DiscussionListAdapter(DiscussionListActivity.this, responseList);
                recyclerView.setAdapter(adapter);
            } else {
                responseList.addAll(response.getDiscussionResponses());
                adapter.notifyDataSetChanged();
                if (response.getDiscussionResponses().size() > 0) {
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
