package com.app.mschooling.class_section_subject.adapter;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
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

import com.app.mschooling.class_section_subject.activity.AddGroupActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.google.gson.Gson;
import com.mschooling.transaction.response.subject.SubjectResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder>  {

    Activity context;
   List<SubjectResponse> responseList;
   String classId;

    public GroupListAdapter(Activity context, List<SubjectResponse> data, String classId) {
        this.context = context;
        this.responseList = data;
        this.classId = classId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_group_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        viewHolder.name.setText(responseList.get(position).getSubjectType().getGroupName().toUpperCase());
//        viewHolder.action.setVisibility(View.GONE);
        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("name", responseList.get(position).getSubjectType().getGroupName().toUpperCase());
                context.setResult(RESULT_OK, intent);
                context.finish();
            }
        });


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
                                Intent intent = new Intent(context, AddGroupActivity.class);
                                intent.putExtra("response", new Gson().toJson(responseList.get(position)));
                                intent.putExtra("classId", classId);
                                context.startActivity(intent);
                                return true;
                            case R.id.delete:
                                EventBus.getDefault().post(new EventDelete(responseList.get(position).getSubjectType().getGroupName(),position));
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
        LinearLayout mainLayout,action;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            action = itemView.findViewById(R.id.action);
        }
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }



}