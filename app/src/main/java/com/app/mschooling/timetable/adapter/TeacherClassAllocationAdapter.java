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
import com.mschooling.transaction.common.Allocation;

import java.util.HashMap;
import java.util.List;

public class TeacherClassAllocationAdapter extends RecyclerView.Adapter<TeacherClassAllocationAdapter.ViewHolder> {

    private Activity context;
    List<Allocation> allocationList;
    HashMap<Integer, Boolean> map;


    public TeacherClassAllocationAdapter(Activity context, List<Allocation> allocationList) {

        this.context = context;
        this.allocationList = allocationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_teacher_timetable_perticular, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Allocation allocation = allocationList.get(position);
        if (allocation.getSectionName() == null) {
            viewHolder.clazz.setText(allocation.getClassName());
        } else {
            viewHolder.clazz.setText(allocation.getClassName() + " (" + allocation.getSectionName() + ")");
        }
        viewHolder.subject.setText(allocation.getSubjectName());
        try {
            viewHolder.day.setText(allocation.getDay().value());
            viewHolder.time.setText(allocation.getTimeslot().getStart() + "-" + allocation.getTimeslot().getEnd());

        } catch (Exception e) {

        }
    }


    @Override
    public int getItemCount() {
        return allocationList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView clazz;
        TextView subject;
        TextView time;
        TextView day;

        public ViewHolder(View itemView) {
            super(itemView);

            clazz = itemView.findViewById(R.id.clazz);
            subject = itemView.findViewById(R.id.subject);
            time = itemView.findViewById(R.id.time);
            day = itemView.findViewById(R.id.day);

        }
    }


}