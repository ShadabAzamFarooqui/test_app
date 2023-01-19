package com.app.mschooling.setting.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.setting.activity.AddSettingActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.setting.SettingResponse;

import java.util.ArrayList;
import java.util.List;

public class SettingListAdapter extends RecyclerView.Adapter<SettingListAdapter.ViewHolder> {

    Activity context;
    List<SettingResponse> responseList;

    public SettingListAdapter(Activity context, List<SettingResponse> data) {
        this.context = context;
        this.responseList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_setting, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        if (position == responseList.size() - 1 && position > 2) {
            viewHolder.marginLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.marginLayout.setVisibility(View.GONE);
        }
        viewHolder.marginLayout.setVisibility(View.GONE);
        viewHolder.title.setText(responseList.get(position).getDisplayName());
        viewHolder.value.setText(responseList.get(position).getValue());
        viewHolder.time.setText(responseList.get(position).getTime());
        viewHolder.postedBy.setText(responseList.get(position).getCreator());

        if (responseList.get(position).isEditable()) {
            viewHolder.edit.setVisibility(View.VISIBLE);
        } else {
            viewHolder.edit.setVisibility(View.GONE);
        }

//        viewHolder.edit.setVisibility(View.VISIBLE);

        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.SettingType.DROPDOWN.value().equals(responseList.get(position).getSettingType().value())){
                    Intent intent=new Intent(context, AddSettingActivity.class);
                    intent.putExtra("displayName", responseList.get(position).getDisplayName());
                    intent.putExtra("name", responseList.get(position).getName());
                    intent.putExtra("value", responseList.get(position).getValue());
                    intent.putStringArrayListExtra("data", (ArrayList<String>) responseList.get(position).getDropdowns());
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
        TextView title;
        TextView value;
        TextView time;
        TextView postedBy;
        View marginLayout;
        LinearLayout commonLayout;
        LinearLayout edit;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            value = itemView.findViewById(R.id.value);
            time = itemView.findViewById(R.id.time);
            postedBy = itemView.findViewById(R.id.postedBy);
            marginLayout = itemView.findViewById(R.id.marginLayout);
            commonLayout = itemView.findViewById(R.id.commonLayout);
            edit = itemView.findViewById(R.id.edit);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
}