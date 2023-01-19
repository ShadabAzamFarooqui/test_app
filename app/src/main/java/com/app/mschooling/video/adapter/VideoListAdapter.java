package com.app.mschooling.video.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.video.activity.AddVideoActivity;
import com.app.mschooling.video.activity.WatchVideoActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.Preferences;
import com.bumptech.glide.Glide;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.video.VideoResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    Activity context;
    boolean isFinish;
    String categoryId;
    public List<VideoResponse> responseList;

    public VideoListAdapter(Activity context, List<VideoResponse> data, String categoryId,boolean isFinish) {
        this.context = context;
        this.isFinish = isFinish;
        this.responseList = data;
        this.categoryId = categoryId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_video_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.tvName.setText(responseList.get(position).getName());
//        String ID = Helper.extractYTId(responseList.get(position));
        String ID = responseList.get(position).getUrl().replace("https://www.youtube.com/watch?v=", "");
         ID = ID.replace("https://youtu.be/", "");
        if (ID != null && !ID.equals("")) {
            String url = "https://img.youtube.com/vi/" + ID + "/mqdefault.jpg";
            Glide.with(context).load(url).into(viewHolder.youtubeThumb);
        }



        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WatchVideoActivity.class);
                intent.putExtra("filename", responseList.get(position).getUrl());
                intent.putExtra("name", responseList.get(position).getName());
                intent.putExtra("id", responseList.get(position).getId());
                intent.putExtra("categoryId", categoryId);
                context.startActivity(intent);
                if (isFinish) {
                    context.finish();
                }
            }
        });

        if (Preferences.getInstance(context).getROLE().equals(Common.Role.STUDENT.value())) {
            viewHolder.action.setVisibility(View.INVISIBLE);
        } else {
            if (isFinish){
                viewHolder.action.setVisibility(View.INVISIBLE);
            }else {
                viewHolder.action.setVisibility(View.VISIBLE);
            }
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
                                Intent intent = new Intent(context, AddVideoActivity.class);
                                intent.putExtra("id", responseList.get(position).getId());
                                intent.putExtra("name", responseList.get(position).getName());
                                intent.putExtra("url", responseList.get(position).getUrl());
                                intent.putExtra("categoryId", categoryId);
                                context.startActivity(intent);
                                return true;
                            case R.id.delete:
                                EventBus.getDefault().post(new EventDelete(categoryId,responseList.get(position).getId(), position));
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
        TextView tvName;
        LinearLayout mainLayout;
        LinearLayout action;
        ImageView youtubeThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            youtubeThumb = itemView.findViewById(R.id.youtubeThumb);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            action = itemView.findViewById(R.id.action);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}