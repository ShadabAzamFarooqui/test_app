package com.app.mschooling.broadcast.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.broadcast.adapter.BroadcastListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.broadcast.GetBroadcastResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;



public class BroadCastListActivity extends BaseActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.add)
    LinearLayout add;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.noRecord)
    LinearLayout noRecord;
    GetBroadcastResponse response;
    boolean loading=true;
    int pageNo;
    BroadcastListAdapter adapter;
    LinearLayoutManager layoutManager;
    int pastVisiblesItems, visibleItemCount, totalItemCount;



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broad_list);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.broad_cast));
        if (Preferences.getInstance(this).getROLE().equals(Common.Role.ADMIN.value()) ){
            add.setVisibility(View.VISIBLE);
        }else {
            add.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.GONE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BroadCastActivity.class));
            }
        });


       /* recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    if (loading) {
                        progressBar.setVisibility(View.VISIBLE);
                        pageNo++;
                        apiCallBack(getApi().broadcastList(pageNo));
                    }
                }
            }
        });*/


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
                            apiCallBack(getApiCommonController().broadcastList(pageNo));
                        }

                    }
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().broadcastList(pageNo));
    }

    @Subscribe
    public void getResponse(GetBroadcastResponse response) {
        progressBar.setVisibility(View.GONE);
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            this.response = response;
            if (pageNo == 0) {
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                layoutManager=new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                adapter = new BroadcastListAdapter(BroadCastListActivity.this,  response.getBroadcastResponses());
                recyclerView.setAdapter(adapter);
                if (response.getBroadcastResponses().size()==0){
                    noRecord.setVisibility(View.VISIBLE);
                }else {
                    noRecord.setVisibility(View.GONE);
                }
            } else {
                response.getBroadcastResponses().addAll(response.getBroadcastResponses());
                adapter.notifyDataSetChanged();
                if (response.getBroadcastResponses().size() == 0) {
                    loading = false;
                }
            }
        }else {
            dialogFailed(response.getMessage().getMessage());
        }
    }





        }