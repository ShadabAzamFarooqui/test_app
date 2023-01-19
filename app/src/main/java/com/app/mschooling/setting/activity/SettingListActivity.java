package com.app.mschooling.setting.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.setting.adapter.SettingListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.setting.GetSettingResponse;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingListActivity extends BaseActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        ButterKnife.bind(this);
        toolbarClick(getString(R.string.setup2));
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().getSetting());
    }

    @Subscribe
    public void getSetting(GetSettingResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerView.setAdapter(new SettingListAdapter(this, response.getSettingResponses()));
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }


}
