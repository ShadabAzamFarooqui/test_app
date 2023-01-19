package com.app.mschooling.notification.activity;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.BuildConfig;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.ActivityNotificationListBinding;
import com.app.mschooling.com.databinding.NoRecord2Binding;
import com.app.mschooling.com.databinding.NoRecordBinding;
import com.app.mschooling.fee.adapter.FeeHistoryListAdapter;
import com.app.mschooling.notification.adapter.NotificationDateAdapter;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.notification.GetNotificationResponse;
import com.mschooling.transaction.response.notification.NotificationResponse;
import com.mschooling.transaction.response.notification.NotificationResponseWrapper;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class NotificationListActivity extends BaseActivity {

    ActivityNotificationListBinding binding;
    NoRecord2Binding noRecordBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_notification_list);
        noRecordBinding = binding.noRecord;
        toolbarClick(getString(R.string.notification));
        apiCallBack(getApiCommonController().getNotification(ParameterConstant.getRole(this)));
    }

    @Subscribe
    public void getNotification(GetNotificationResponse response){
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {

//           if(BuildConfig.DEBUG){
//               response.setNotificationResponseWrappers(new ArrayList<>());
//               NotificationResponseWrapper notificationResponseWrapper=new NotificationResponseWrapper();
//               notificationResponseWrapper.setNotificationResponses(new ArrayList<>());
//               notificationResponseWrapper.setDate(Helper.getCurrentDate());
//               NotificationResponse notificationResponse=new NotificationResponse();
//               notificationResponse.setDate(Helper.getCurrentDate());
//               notificationResponse.setNotificationType(Common.NotificationType.NOTICE);
//               notificationResponse.setTime(Helper.getCurrentTime());
//               notificationResponse.setTitle("One notice added");
//               notificationResponseWrapper.getNotificationResponses().add(notificationResponse);
//               notificationResponseWrapper.getNotificationResponses().add(notificationResponse);
//               notificationResponseWrapper.getNotificationResponses().add(notificationResponse);
//               notificationResponseWrapper.getNotificationResponses().add(notificationResponse);
//               notificationResponseWrapper.getNotificationResponses().add(notificationResponse);
//               notificationResponseWrapper.getNotificationResponses().add(notificationResponse);
//               notificationResponseWrapper.getNotificationResponses().add(notificationResponse);
//               response.getNotificationResponseWrappers().add(notificationResponseWrapper);
//               response.getNotificationResponseWrappers().add(notificationResponseWrapper);
//           }
            binding.recyclerView.setHasFixedSize(true);
            binding.recyclerView.setFocusable(false);
            binding.recyclerView.setNestedScrollingEnabled(false);
            binding.recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
            binding.recyclerView.setAdapter(new NotificationDateAdapter(this,response.getNotificationResponseWrappers()));
            if (response.getNotificationResponseWrappers().size()==0){
                noRecordBinding.noRecord.setVisibility(View.VISIBLE);
            }else {
                noRecordBinding.noRecord.setVisibility(View.GONE);
            }
        } else {
            dialogError(response.getMessage().getMessage());
        }
    }
}