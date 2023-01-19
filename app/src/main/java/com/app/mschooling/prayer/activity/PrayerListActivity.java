package com.app.mschooling.prayer.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.prayer.adapter.PrayerListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.prayer.DeletePrayerResponse;
import com.mschooling.transaction.response.prayer.GetPrayerResponse;
import com.mschooling.transaction.response.prayer.PrayerResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrayerListActivity extends BaseActivity {

    @BindView(R.id.noRecord)
    LinearLayout noRecord;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.add)
    LinearLayout add;

    List<PrayerResponse> responseList;
    PrayerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_list);
        ButterKnife.bind(this);
        toolbarClick(getResources().getString(R.string.prayer));

        if (Preferences.getInstance(getApplicationContext()).getROLE().equals(Common.Role.ADMIN.value())){
            add.setVisibility(View.VISIBLE);
        }else {
            add.setVisibility(View.GONE);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddPrayerActivity.class));
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().getPrayer());
    }

    @Subscribe
    public void getStudentList(GetPrayerResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            responseList=response.getPrayerResponses();
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter=new PrayerListAdapter(this,responseList);
            recyclerView.setAdapter(adapter);
            if (response.getPrayerResponses().size() == 0) {
                noRecord.setVisibility(View.VISIBLE);
            } else {
                noRecord.setVisibility(View.GONE);
            }
        } else {
            dialogError(response.getMessage().getMessage());
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
                apiCallBack(getApiCommonController().deletePrayer(event.getId()));
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
    public void deleteNotice(DeletePrayerResponse response) {
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