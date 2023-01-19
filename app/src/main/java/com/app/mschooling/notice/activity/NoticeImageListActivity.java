package com.app.mschooling.notice.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.notice.adapter.NoticeImageListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.google.gson.Gson;
import com.mschooling.transaction.common.Firebase;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.notice.NoticeResponse;

import java.util.List;

public class NoticeImageListActivity extends BaseActivity {
    public List<Firebase> responseList;
    private NoticeResponse response;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_image_list_fragment);
        toolbarClick(getString(R.string.attachment));
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        Gson gson = new Gson();
        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("data");
        response = gson.fromJson(jsonString, NoticeResponse.class);
        responseList = response.getFirebase();

        for (int i=0;i<responseList.size();i++){
            if (responseList.get(i).getFirebaseType()!=null){
                if (responseList.get(i).getFirebaseType().equals(Common.FirebaseType.PDF)){
                    responseList.remove(i);
                }
            }
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new NoticeImageListAdapter(this, responseList));
    }
}