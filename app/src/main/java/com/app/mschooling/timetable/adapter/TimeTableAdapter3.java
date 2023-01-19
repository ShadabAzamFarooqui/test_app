package com.app.mschooling.timetable.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.common.TimeTable;

import java.util.List;


public class TimeTableAdapter3 extends RecyclerView.Adapter<TimeTableAdapter3.ViewHolder> {

    Activity context;
    List<TimeTable> timeTableList;



    public TimeTableAdapter3(Activity context, List<TimeTable> timeTableList) {
        this.context = context;
        this.timeTableList = timeTableList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.get_timetable_row3, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.subject.setText(timeTableList.get(position).getSubjectName());
        viewHolder.teacher.setText(timeTableList.get(position).getTeacherName());


    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return timeTableList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView subject;
        TextView teacher;
        public ViewHolder(View itemView) {
            super(itemView);
            subject=itemView.findViewById(R.id.subject);
            teacher=itemView.findViewById(R.id.teacher);

        }
    }



}