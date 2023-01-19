package com.app.mschooling.application.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.application.adapter.ApplicationListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.application.ApplicationResponse;
import com.mschooling.transaction.response.application.GetApplicationResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class ApplicationListActivity extends BaseActivity {


    LinearLayout add;
    LinearLayout noRecord;
    RecyclerView recyclerView;
    ApplicationListAdapter adapter;
    List<ApplicationResponse> responseList;


    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    int pageNo;
    LinearLayoutManager layoutManager;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_list_activity);
        toolbarClick(getString(R.string.applications));
        add=findViewById(R.id.add);
        recyclerView=findViewById(R.id.recyclerView);
        noRecord=findViewById(R.id.noRecord);
        noRecord.setVisibility(View.GONE);
        layoutManager=new LinearLayoutManager(ApplicationListActivity.this);
        pageNo=0;
        loading=true;


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Preferences.getInstance(ApplicationListActivity.this).getROLE().equals(Common.Role.ADMIN.value())){
                    startActivity(new Intent(getApplicationContext(), ApplicationReasonListActivity.class));
                }else {
                    startActivity(new Intent(getApplicationContext(), AddApplicationActivity.class));
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
                    ApplicationListAdapter.filteredList =responseList;
                    ApplicationListAdapter.responseList =responseList;
                    adapter.getFilter().filter(s.toString());
                }catch (Exception e){

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
                            apiCallBack(getApiCommonController().getApplication(""+pageNo));                        }

                    }
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().getApplication(""+pageNo));
    }

    @Subscribe
    public void getApplicationList(GetApplicationResponse response){
        if (response.getStatus().value()==Status.SUCCESS.value()) {
            if (pageNo==0) {
                if (response.getApplicationResponses().size()>0){
                    noRecord.setVisibility(View.GONE);
                }else {
                    noRecord.setVisibility(View.VISIBLE);
                }
                responseList = response.getApplicationResponses();
                recyclerView.setHasFixedSize(true);
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new ApplicationListAdapter(ApplicationListActivity.this,  responseList);
                recyclerView.setAdapter(adapter);
            }else {
                responseList.addAll(response.getApplicationResponses());
                adapter.notifyDataSetChanged();
                if (response.getApplicationResponses().size()>0){
                    loading=true;
                }else {
                    loading=false;
                }
            }
        }else {
            dialogFailed(response.getMessage().getMessage());
        }
    }
}
