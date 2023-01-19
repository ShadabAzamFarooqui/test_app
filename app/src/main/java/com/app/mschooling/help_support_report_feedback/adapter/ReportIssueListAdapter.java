package com.app.mschooling.help_support_report_feedback.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.app.mschooling.network.pojo.ReportIssueModel;
import com.app.mschooling.other.ReportIssueInterface;

import java.util.ArrayList;
import java.util.List;

public class ReportIssueListAdapter extends RecyclerView.Adapter<ReportIssueListAdapter.ViewHolder> {

    private final List<ReportIssueModel> data;
    private final ReportIssueInterface issueReportInterface;
    private final ArrayList<String> reportIssueList;
    Activity context;

    public ReportIssueListAdapter(Activity context, List<ReportIssueModel> data,ReportIssueInterface issueReportInterface) {
        this.context = context;
        this.data = data;
        this.issueReportInterface = issueReportInterface;
        reportIssueList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_report_issue_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.name.setText(data.get(position).getName());
        if (position==data.size()-1){
            viewHolder.divider.setVisibility(View.GONE);
        }else {
            viewHolder.divider.setVisibility(View.VISIBLE);
        }
        viewHolder.main_layout.setOnClickListener(v -> {
            if (viewHolder.checkbox.isChecked()) {
                viewHolder.checkbox.setChecked(false);
                reportIssueList.add(data.get(position).getName());
            }
            else{
                viewHolder.checkbox.setChecked(true);
                reportIssueList.remove(data.get(position).getName());
            }
            issueReportInterface.selectIssue(reportIssueList);
        });

        viewHolder.checkbox.setOnClickListener(v -> {
            if (((CheckBox) v).isChecked()) {
                reportIssueList.add(data.get(position).getName());
            }
            else{
                reportIssueList.remove(data.get(position).getName());
            }
            issueReportInterface.selectIssue(reportIssueList);
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout main_layout;
        CheckBox checkbox;
        TextView name;
        View divider;
        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            checkbox=itemView.findViewById(R.id.checkbox);
            main_layout=itemView.findViewById(R.id.main_layout);
            divider=itemView.findViewById(R.id.divider);

        }
    }
}