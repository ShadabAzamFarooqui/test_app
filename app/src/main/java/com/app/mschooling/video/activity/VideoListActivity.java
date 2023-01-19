package com.app.mschooling.video.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.video.adapter.VideoListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.video.DeleteVideoResponse;
import com.mschooling.transaction.response.video.GetVideoResponse;
import com.mschooling.transaction.response.video.VideoResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class VideoListActivity extends BaseActivity {
    LinearLayout add;
    RecyclerView recyclerView;
    VideoListAdapter adapter;
    List<VideoResponse> responseList;
    LinearLayout noRecord;

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    int pageNo;
    LinearLayoutManager layoutManager;

    String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        toolbarClick(getString(R.string.videos));
        categoryId=getIntent().getStringExtra("categoryId");
        add = findViewById(R.id.add);
        noRecord = findViewById(R.id.noRecord);
        recyclerView = findViewById(R.id.recycler_view);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), AddVideoActivity.class);
                intent.putExtra("categoryId",categoryId);
                startActivity(intent);
            }
        });

        if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.STUDENT.value())) {
            add.setVisibility(View.GONE);
        } else {
            add.setVisibility(View.VISIBLE);
        }

      /*  recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstCompletelyVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            pageNo++;
                            apiCallBack(getApiCommonController().getVideoList("" + pageNo));

                        }

                    }
                }
            }
        });*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        pageNo = 0;
        loading = true;
        apiCallBack(getApiCommonController().getVideoList("" + categoryId));
    }

    @Subscribe
    public void getResponseList(GetVideoResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            if (pageNo == 0) {
                if (response.getVideoResponses().size() > 0) {
                    noRecord.setVisibility(View.GONE);
                } else {
                    noRecord.setVisibility(View.VISIBLE);
                }
                responseList = response.getVideoResponses();
                setAdapter(responseList);
            } else {
                responseList.addAll(response.getVideoResponses());
                adapter.notifyDataSetChanged();
                if (response.getVideoResponses().size() > 0) {
                    loading = true;
                } else {
                    loading = false;
                }
            }

            AppUser.getInstance().setVideoResponse(responseList);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    private void setAdapter(List<VideoResponse> responseList) {
        if (responseList.size() > 0) {
            noRecord.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
            adapter = new VideoListAdapter(this, responseList, categoryId,false);
            recyclerView.setAdapter(adapter);
        } else {
            noRecord.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }



    EventDelete event;

    @Subscribe
    public void delete(EventDelete event) {
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
                apiCallBack(getApiCommonController().deleteVideo(event.getCategoryId(),event.getId()));
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