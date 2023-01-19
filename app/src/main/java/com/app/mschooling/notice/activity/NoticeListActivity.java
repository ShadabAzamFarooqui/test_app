package com.app.mschooling.notice.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.notice.adapter.NoticeListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.notice.DeleteNoticeResponse;
import com.mschooling.transaction.response.notice.GetNoticeResponse;
import com.mschooling.transaction.response.notice.NoticeResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;


public class NoticeListActivity extends BaseActivity {
    LinearLayout add;
    LinearLayout noRecord;
    RecyclerView recyclerView;
    NoticeListAdapter adapter;
    List<NoticeResponse> responseList;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    int pageNo;
    LinearLayoutManager layoutManager;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_list_fragment);
        toolbarClick(getString(R.string.tool_notices));
        add = findViewById(R.id.add);
        recyclerView = findViewById(R.id.recyclerView);
        noRecord = findViewById(R.id.noRecord);
        noRecord.setVisibility(View.GONE);
        layoutManager = new LinearLayoutManager(NoticeListActivity.this);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), AddNoticeActivity.class), REQUEST_CREATE);
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
                    NoticeListAdapter.filteredList = responseList;
                    NoticeListAdapter.responseList = responseList;
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

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            pageNo++;
                            apiCallBack(getApiCommonController().getNotice("" + pageNo));

                        }

                    }
                }
            }
        });

        if (Preferences.getInstance(NoticeListActivity.this).getROLE().equals(Common.Role.ADMIN.value())) {
            add.setVisibility(View.VISIBLE);
        } else {
            add.setVisibility(View.GONE);
        }


        pageNo = 0;
        loading = true;
        apiCallBack(getApiCommonController().getNotice("" + pageNo));
    }


    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    public void get(GetNoticeResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            if (pageNo == 0) {
                if (response.getNoticeResponses().size() > 0) {
                    noRecord.setVisibility(View.GONE);
                } else {
                    noRecord.setVisibility(View.VISIBLE);
                }
                responseList = response.getNoticeResponses();
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new NoticeListAdapter(NoticeListActivity.this, responseList);
                recyclerView.setAdapter(adapter);
            } else {
                responseList.addAll(response.getNoticeResponses());
                adapter.notifyDataSetChanged();
                if (response.getNoticeResponses().size() > 0) {
                    loading = true;
                } else {
                    loading = false;
                }
            }
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    EventDelete event;

    @Subscribe
    public void deleteEvent(EventDelete event) {
        this.event = event;
        dialogDelete(getApiCommonController().deleteNotice(event.getId()));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    public void delete(DeleteNoticeResponse response) {
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

    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_CREATE) {
                    add.setVisibility(View.VISIBLE);
                    pageNo = 0;
                    loading = true;
                    apiCallBack(getApiCommonController().getNotice("" + pageNo));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
