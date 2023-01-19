package com.app.mschooling.video.adapter;

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

import com.app.mschooling.video.activity.AddVideoCategoryActivity;
import com.app.mschooling.video.activity.VideoListActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.video.VideoCategoryResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class VideoCategoryListAdapter extends RecyclerView.Adapter<VideoCategoryListAdapter.ViewHolder> {

    Activity context;
    public List<VideoCategoryResponse> responseList;
    private String intent="";

    public VideoCategoryListAdapter(Activity context, List<VideoCategoryResponse> data, String intent) {
        this.context = context;
        this.responseList = data;
        this.intent = intent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_video_category, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.name.setText(responseList.get(position).getName());
        viewHolder.description.setText(responseList.get(position).getName());


        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent!=null ){
                    Intent intent = new Intent();
                    intent.putExtra("id", responseList.get(position).getId());
                    intent.putExtra("name", responseList.get(position).getName());
                    context.setResult(Activity.RESULT_OK,intent);
                    context.finish();
                }else {
                    Intent intent = new Intent(context, VideoListActivity.class);
                    intent.putExtra("name", responseList.get(position).getName());
                    intent.putExtra("categoryId", responseList.get(position).getId());
                    context.startActivity(intent);
                }
            }
        });

        if (Preferences.getInstance(context).getROLE().equals(Common.Role.STUDENT.value())) {
            viewHolder.action.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.action.setVisibility(View.VISIBLE);
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
                                Intent intent = new Intent(context, AddVideoCategoryActivity.class);
                                intent.putExtra("id", responseList.get(position).getId());
                                intent.putExtra("name", responseList.get(position).getName());
                                intent.putExtra("classId", responseList.get(position).getClassId());
                                intent.putExtra("className", responseList.get(position).getClassName());
                                intent.putExtra("priority", ""+responseList.get(position).getPriority());
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
        TextView name;
        TextView description;
        LinearLayout mainLayout;
        LinearLayout action;


        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);

            mainLayout = itemView.findViewById(R.id.mainLayout);
            action = itemView.findViewById(R.id.action);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}