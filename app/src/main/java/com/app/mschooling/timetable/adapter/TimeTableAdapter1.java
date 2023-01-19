package com.app.mschooling.timetable.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.timetable.activity.AddTimeTableActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.response.timetable.GetTimeTableResponse;
import com.mschooling.transaction.response.timetable.GetTimeTableResponseList;


public class TimeTableAdapter1 extends RecyclerView.Adapter<TimeTableAdapter1.ViewHolder> {

    Activity context;
   GetTimeTableResponseList getTimeTableResponse;



    public TimeTableAdapter1(Activity context, GetTimeTableResponseList getTimeTableResponse) {
        this.context = context;
        this.getTimeTableResponse = getTimeTableResponse;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.get_timetable_row1, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        GetTimeTableResponse tableResponse=getTimeTableResponse.getGetTimeTableResponses().get(position);
        viewHolder.clazz.setText(tableResponse.getClassName()+" ("+tableResponse.getSectionName()+")");

        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, AddTimeTableActivity.class);
                intent.putExtra("edit",true);
                intent.putExtra("classId",tableResponse.getClassId());
                intent.putExtra("className",tableResponse.getClassName());
                intent.putExtra("sectionName",tableResponse.getSectionName());
                intent.putExtra("sectionId",tableResponse.getSectionId());
                context.startActivity(intent);
            }
        });

        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setFocusable(false);
        viewHolder.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        viewHolder.recyclerView.setAdapter(new TimeTableAdapter2(context, getTimeTableResponse.getGetTimeTableResponses().get(position).getTimeTableRows()));

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return getTimeTableResponse.getGetTimeTableResponses().size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        LinearLayout edit;
        TextView clazz;
        RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            clazz = itemView.findViewById(R.id.clazz);
            edit = itemView.findViewById(R.id.edit);
        }
    }



}