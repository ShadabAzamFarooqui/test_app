package com.app.mschooling.video.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.video.adapter.VideoCategoryListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.video.DeleteVideoResponse;
import com.mschooling.transaction.response.video.GetVideoCategoryResponse;
import com.mschooling.transaction.response.video.VideoCategoryResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class VideoCategoryListActivity extends BaseActivity {
    LinearLayout add;
    RecyclerView recyclerView;
    VideoCategoryListAdapter adapter;
    List<VideoCategoryResponse> responseList;
    LinearLayout noRecord;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    int pageNo;
    LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        toolbarClick(getString(R.string.categories));

        add = findViewById(R.id.add);
        noRecord = findViewById(R.id.noRecord);
        recyclerView = findViewById(R.id.recycler_view);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), AddVideoCategoryActivity.class);
                intent.putExtra("classId",getIntent("classId"));
                intent.putExtra("className",getIntent("className"));;
                startActivity(intent);
            }
        });

        if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.STUDENT.value())) {
            add.setVisibility(View.GONE);
        } else {
            add.setVisibility(View.VISIBLE);
        }

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
                            apiCallBack(getApiCommonController().getVideoCategoryList("" + pageNo,getIntent("classId")));

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
        apiCallBack(getApiCommonController().getVideoCategoryList("" + pageNo,getIntent("classId")));
    }

    @Subscribe
    public void getResponseList(GetVideoCategoryResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            if (pageNo == 0) {
                if (response.getVideoCategoryResponses().size() > 0) {
                    noRecord.setVisibility(View.GONE);
                } else {
                    noRecord.setVisibility(View.VISIBLE);
                }
                responseList = response.getVideoCategoryResponses();
                setAdapter(responseList);
            } else {
                responseList.addAll(response.getVideoCategoryResponses());
                adapter.notifyDataSetChanged();
                if (response.getVideoCategoryResponses().size() > 0) {
                    loading = true;
                } else {
                    loading = false;
                }
            }

        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    private void setAdapter(List<VideoCategoryResponse> responseList) {
        if (responseList.size() > 0) {
            noRecord.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            layoutManager=new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new VideoCategoryListAdapter(VideoCategoryListActivity.this, responseList, getIntent().getStringExtra("intent"));
            recyclerView.setAdapter(adapter);
        } else {
            noRecord.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }



    EventDelete event;

    @Subscribe
    public void deleteNoticeEvent(EventDelete event) {
        this.event = event;

        Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_delete_notice);
        dialog.setCancelable(false);
        TextView delete =  dialog.findViewById(R.id.delete);
        TextView cancel =  dialog.findViewById(R.id.cancel);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                apiCallBack(getApiCommonController().deleteVideoCategory(event.getId()));
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

    @Subscribe
    public void deleteNotice(DeleteVideoResponse response) {
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

}