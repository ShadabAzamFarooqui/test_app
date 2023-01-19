package com.app.mschooling.notification.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.attendance.student.adapter.MarkStudentAttendanceAdapter;
import com.app.mschooling.com.R;
import com.app.mschooling.com.databinding.RowAttendanceBinding;
import com.app.mschooling.com.databinding.RowNotificationDateBinding;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.event_handler.GetRoleEvent;
import com.app.mschooling.other.adapter.ImageListAdapter;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.notice.NoticeResponse;
import com.mschooling.transaction.response.notification.NotificationResponseWrapper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class NotificationDateAdapter extends RecyclerView.Adapter<NotificationDateAdapter.ViewHolder>  {

   Activity context;
    List<NotificationResponseWrapper> responseList;
    public NotificationDateAdapter(Activity context, List<NotificationResponseWrapper> responseList) {
        this.context=context;
        this.responseList=responseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {


        RowNotificationDateBinding binding = RowNotificationDateBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
        return new NotificationDateAdapter.ViewHolder(binding.getRoot());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        viewHolder.binding.date.setText(responseList.get(position).getDate());
        viewHolder.binding.recyclerView.setHasFixedSize(true);
        viewHolder.binding.recyclerView.setFocusable(false);
        viewHolder.binding.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        viewHolder.binding.recyclerView.setAdapter(new NotificationContentAdapter(context, responseList.get(position).getNotificationResponses()));


    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        RowNotificationDateBinding binding;
        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.findBinding(itemView.getRootView());
        }
    }




}