package com.app.mschooling.study_material.adapter;

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

import com.app.mschooling.study_material.activity.UploadStudyMaterialActivity;
import com.app.mschooling.syllabus.activity.PdfDocumentActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.study.GetStudyResponse;

import org.greenrobot.eventbus.EventBus;

public class StudyMaterialListAdapter extends RecyclerView.Adapter<StudyMaterialListAdapter.ViewHolder> {

    Activity context;
    public GetStudyResponse response;
    String categoryId;

    public StudyMaterialListAdapter(Activity context, GetStudyResponse data,String categoryId) {
        this.context = context;
        this.response = data;
        this.categoryId = categoryId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_video_category, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.name.setText(response.getStudyResponses().get(position).getName());
        viewHolder.description.setText(response.getStudyResponses().get(position).getName());


        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfDocumentActivity.class);
                intent.putExtra("url", response.getStudyResponses().get(position).getUrl());
                intent.putExtra("name", response.getStudyResponses().get(position).getName());
                context.startActivity(intent);
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
                                Intent intent = new Intent(context, UploadStudyMaterialActivity.class);
                                intent.putExtra("action", "update");
                                intent.putExtra("categoryId", categoryId);
                                intent.putExtra("id", response.getStudyResponses().get(position).getId());
                                intent.putExtra("url", response.getStudyResponses().get(position).getUrl());
                                intent.putExtra("name", response.getStudyResponses().get(position).getName());
                                context.startActivityForResult(intent,2);
                                return true;
                            case R.id.delete:
                                EventBus.getDefault().post(new EventDelete(response.getStudyResponses().get(position).getId(), position));
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
        return response.getStudyResponses().size();
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