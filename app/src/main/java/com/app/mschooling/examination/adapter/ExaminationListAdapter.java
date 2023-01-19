package com.app.mschooling.examination.adapter;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
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

import com.app.mschooling.examination.activity.CreateExaminationActivity;
import com.app.mschooling.examination.activity.ResultActivityActivity;
import com.app.mschooling.examination.activity.StudentResultListActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.event_handler.EventPublish;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.examination.ExaminationResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ExaminationListAdapter extends RecyclerView.Adapter<ExaminationListAdapter.ViewHolder> {

    Activity context;
    List<ExaminationResponse> responseList;
    String intentString;

    public ExaminationListAdapter(Activity context, List<ExaminationResponse> data, String intentString) {
        this.context = context;
        this.responseList = data;
        this.intentString = intentString;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_examination_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.name.setText(responseList.get(position).getName());
        if (Preferences.getInstance(context).getROLE().equals(Common.Role.STUDENT.value())) {
            viewHolder.action.setVisibility(View.GONE);
        } else {
            viewHolder.action.setVisibility(View.VISIBLE);
        }
        if (Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value())) {
            if ("exam".equals(intentString)) {
                viewHolder.publishLayout.setVisibility(View.GONE);
                viewHolder.action.setVisibility(View.VISIBLE);
            } else if ("publish".equals(intentString)) {
                viewHolder.publishLayout.setVisibility(View.VISIBLE);
                viewHolder.action.setVisibility(View.GONE);
            } else {
                viewHolder.publishLayout.setVisibility(View.GONE);
                viewHolder.action.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.publishLayout.setVisibility(View.GONE);
            viewHolder.action.setVisibility(View.GONE);
        }

        if (responseList.get(position).isPublish()) {
            viewHolder.tick.setImageDrawable(context.getResources().getDrawable(R.drawable.tick_location));
            viewHolder.publish.setVisibility(View.GONE);
            viewHolder.published.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tick.setImageDrawable(context.getResources().getDrawable(R.drawable.untick_location));
            viewHolder.publish.setVisibility(View.VISIBLE);
            viewHolder.published.setVisibility(View.GONE);
        }

        viewHolder.publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                publishExam
                EventBus.getDefault().post(new EventPublish(responseList.get(position).getId(), position,"PUBLISH"));
            }
        });

        viewHolder.published.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                publishExam
                EventBus.getDefault().post(new EventPublish(responseList.get(position).getId(), position,"UNPUBLISH"));
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
                                Intent intent = new Intent(context, CreateExaminationActivity.class);
                                intent.putExtra("id", responseList.get(position).getId());
                                intent.putExtra("name", responseList.get(position).getName());
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


        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intentString == null) {
                    if (Preferences.getInstance(context).getROLE().equals(Common.Role.STUDENT.value())) {
                        Intent intent = new Intent(context, ResultActivityActivity.class);
                        intent.putExtra("id", responseList.get(position).getId());
                        intent.putExtra("name", responseList.get(position).getName());
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("id", responseList.get(position).getId());
                        intent.putExtra("name", responseList.get(position).getName());
                        context.setResult(RESULT_OK, intent);
                        context.finish();
                    }
                } else if ("StudentResultListActivity".equals(intentString)) {
                    Intent intent = new Intent(context, StudentResultListActivity.class);
                    intent.putExtra("id", responseList.get(position).getId());
                    intent.putExtra("name", responseList.get(position).getName());
                    context.startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        LinearLayout mainLayout;
        LinearLayout action, publish, published, publishLayout;
        ImageView tick;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            action = itemView.findViewById(R.id.action);
            publish = itemView.findViewById(R.id.publish);
            published = itemView.findViewById(R.id.published);
            publishLayout = itemView.findViewById(R.id.publishLayout);
            tick = itemView.findViewById(R.id.tick);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


}