package com.app.mschooling.attendance.student.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.response.attendance.StudentAttendanceDetailResponse;

public class StudentAttendanceDetailAdapter extends RecyclerView.Adapter<StudentAttendanceDetailAdapter.ViewHolder> {

    private final Activity context;
    public StudentAttendanceDetailResponse responseList;

    public StudentAttendanceDetailAdapter(Activity context, StudentAttendanceDetailResponse responseList) {

        this.context = context;
        this.responseList = responseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_student_attendance, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.date.setText(responseList.getAttendanceResponses().get(position).getDate());
        try {
            viewHolder.attendance.setText(responseList.getAttendanceResponses().get(position).getAttendance().value());
            if (responseList.getAttendanceResponses().get(position).getAttendance().value().equals("A")){
                viewHolder.attendance.setTextColor(context.getResources().getColor(R.color.red));
            }else if (responseList.getAttendanceResponses().get(position).getAttendance().value().equals("P")){
                viewHolder.attendance.setTextColor(context.getResources().getColor(R.color.green));
            }else {
                viewHolder.attendance.setTextColor(context.getResources().getColor(R.color.black));
            }
        }catch (Exception e){
            viewHolder.attendance.setText("");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return responseList.getAttendanceResponses().size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView attendance;



        public ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            attendance = view.findViewById(R.id.attendance);
        }
    }


}