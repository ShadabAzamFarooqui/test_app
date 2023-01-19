package com.app.mschooling.timetable.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.common.TimeTableRow;

import java.util.List;


public class TimeTableAdapter2 extends RecyclerView.Adapter<TimeTableAdapter2.ViewHolder> {

    Activity context;
    List<TimeTableRow> timeTableRows;
    int groupPosition;


    public TimeTableAdapter2(Activity context, List<TimeTableRow> timeTableRows) {
        this.context = context;
        this.timeTableRows = timeTableRows;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.get_timetable_row2, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {


        viewHolder.from.setText("" + timeTableRows.get(position).getTimeslot().getStart());
        viewHolder.to.setText("" + timeTableRows.get(position).getTimeslot().getEnd());
        viewHolder.srNo.setText("" + (position+1));

        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setFocusable(false);
        viewHolder.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        viewHolder.recyclerView.setAdapter(new TimeTableAdapter3(context, timeTableRows.get(position).getTimeTables()));


    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return timeTableRows.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView srNo;
        TextView from;
        TextView to;
        RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            srNo = itemView.findViewById(R.id.srNo);
            from = itemView.findViewById(R.id.from);
            to = itemView.findViewById(R.id.to);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }


}