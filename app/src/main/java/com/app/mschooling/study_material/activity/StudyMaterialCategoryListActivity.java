package com.app.mschooling.study_material.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.study_material.adapter.StudyMaterialCategoryListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.study.DeleteStudyResponse;
import com.mschooling.transaction.response.study.GetStudyCategoryResponse;
import com.mschooling.transaction.response.study.StudyCategoryResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class StudyMaterialCategoryListActivity extends BaseActivity {
    LinearLayout add;
    RecyclerView recyclerView;
    StudyMaterialCategoryListAdapter adapter;
    List<StudyCategoryResponse> responseList;
    LinearLayout noRecord;

    int pastVisiblesItems, visibleItemCount, totalItemCount;
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
                Intent intent=new Intent(getApplicationContext(), AddStudyMaterialCategoryActivity.class);
                intent.putExtra("classId",getIntent("classId"));
                intent.putExtra("className",getIntent("className"));
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
                    pastVisiblesItems = layoutManager.findFirstCompletelyVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            pageNo++;
                            apiCallBack(getApiCommonController().getStudyMaterialCategoryList("" + pageNo,getIntent("classId")));

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
        apiCallBack(getApiCommonController().getStudyMaterialCategoryList("" + pageNo,getIntent("classId")));
    }

    @Subscribe
    public void getResponseList(GetStudyCategoryResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            if (pageNo == 0) {
                if (response.getStudyCategoryResponses().size() > 0) {
                    noRecord.setVisibility(View.GONE);
                } else {
                    noRecord.setVisibility(View.VISIBLE);
                }
                responseList = response.getStudyCategoryResponses();
                setAdapter(responseList);
            } else {
                responseList.addAll(response.getStudyCategoryResponses());
                adapter.notifyDataSetChanged();
                if (response.getStudyCategoryResponses().size() > 0) {
                    loading = true;
                } else {
                    loading = false;
                }
            }

        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    private void setAdapter(List<StudyCategoryResponse> responseList) {
        if (responseList.size() > 0) {
            noRecord.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            layoutManager=new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new StudyMaterialCategoryListAdapter(StudyMaterialCategoryListActivity.this, responseList, false);
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
                apiCallBack(getApiCommonController().deleteStudyMaterialCategory(event.getId()));
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
    public void deleteNotice(DeleteStudyResponse response) {
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