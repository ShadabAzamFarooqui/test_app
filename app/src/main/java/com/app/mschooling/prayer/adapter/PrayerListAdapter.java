package com.app.mschooling.prayer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.prayer.activity.AddPrayerActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.prayer.PrayerResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class PrayerListAdapter extends RecyclerView.Adapter<PrayerListAdapter.ViewHolder> {

    Activity context;
    public static List<PrayerResponse> responseList;
    public static List<PrayerResponse> filteredList;

    public PrayerListAdapter(Activity context, List<PrayerResponse> data) {
        this.context = context;
        this.responseList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_prayer, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.title.setText(responseList.get(position).getTitle());
        viewHolder.application.setText(responseList.get(position).getContent());
        if (position==responseList.size()-1&&position>2){
            viewHolder.marginLayout.setVisibility(View.VISIBLE);
        }else {
            viewHolder.marginLayout.setVisibility(View.GONE);
        }

        if (Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value())){
            viewHolder.action.setVisibility(View.VISIBLE);
        }else {
            viewHolder.action.setVisibility(View.GONE);
        }

        viewHolder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                //inflating menu from xml resource
                popup.inflate(R.menu.edit_delete);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                Intent intent = new Intent(context, AddPrayerActivity.class);
                                intent.putExtra("id", responseList.get(position).getId());
                                intent.putExtra("title", responseList.get(position).getTitle());
                                intent.putExtra("content", responseList.get(position).getContent());
                                context.startActivity(intent);
                                return true;
                            case R.id.delete:
                                EventBus.getDefault().post(new EventDelete(responseList.get(position).getId(), position));

                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView application;
//        TextView time;
//        TextView postedBy;
        LinearLayout action;

        View marginLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            application = itemView.findViewById(R.id.notice);
//            time = itemView.findViewById(R.id.time);
//            postedBy = itemView.findViewById(R.id.postedBy);
            action = itemView.findViewById(R.id.action);
            marginLayout = itemView.findViewById(R.id.marginLayout);
        }
    }




    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
}