package com.app.mschooling.notification.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.databinding.RowNotificationContentBinding;
import com.app.mschooling.com.databinding.RowNotificationDateBinding;
import com.app.mschooling.homework.activity.HomeWorkListActivity;
import com.app.mschooling.notice.activity.NoticeListActivity;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.notification.NotificationResponse;
import com.mschooling.transaction.response.notification.NotificationResponseWrapper;

import java.util.List;

public class NotificationContentAdapter extends RecyclerView.Adapter<NotificationContentAdapter.ViewHolder> {

    Activity context;
    List<NotificationResponse> responseList;

    public NotificationContentAdapter(Activity context, List<NotificationResponse> responseList) {
        this.context = context;
        this.responseList = responseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RowNotificationContentBinding binding = RowNotificationContentBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
        return new NotificationContentAdapter.ViewHolder(binding.getRoot());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.binding.tittle.setText(responseList.get(position).getTitle());
        viewHolder.binding.time.setText(responseList.get(position).getTime());

//        viewHolder.binding.mainLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (responseList.get(position).getNotificationType() == Common.NotificationType.NOTICE) {
//                    context.startActivity(new Intent(context, NoticeListActivity.class));
//                } else if (responseList.get(position).getNotificationType() == Common.NotificationType.DISCUSSION) {
//                    context.startActivity(new Intent(context, NoticeListActivity.class));
//                } else if (responseList.get(position).getNotificationType() == Common.NotificationType.HOMEWORK) {
//                    context.startActivity(new Intent(context, HomeWorkListActivity.class));
//                } else if (responseList.get(position).getNotificationType() == Common.NotificationType.TIMETABLE) {
////                    context.startActivity(new Intent(context, NoticeListActivity.class));
//                } else if (responseList.get(position).getNotificationType() == Common.NotificationType.SYLLABUS) {
//
//                } else if (responseList.get(position).getNotificationType() == Common.NotificationType.ATTENDANCE) {
//
//                } else if (responseList.get(position).getNotificationType() == Common.NotificationType.PENDING_TASK) {
//
//                } else if (responseList.get(position).getNotificationType() == Common.NotificationType.VIDEO) {
//
//                } else if (responseList.get(position).getNotificationType() == Common.NotificationType.LIVE) {
//
//                } else if (responseList.get(position).getNotificationType() == Common.NotificationType.QUIZ) {
//
//                } else if (responseList.get(position).getNotificationType() == Common.NotificationType.SUPPORT) {
//
//                }
//            }
//        });

    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        RowNotificationContentBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.findBinding(itemView.getRootView());
        }
    }


}