package com.app.mschooling.class_section_subject.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.timetable.adapter.AddTimeTableAdapter;
import com.app.mschooling.timetable.activity.AddTimeTableActivity;
import com.app.mschooling.teachers.list.adapter.TeacherListDialogAdapter;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.Allocation;
import com.mschooling.transaction.common.TimeTable;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.subject.SubjectResponse;

import java.util.List;

public class SubjectListDialogAdapter extends RecyclerView.Adapter<SubjectListDialogAdapter.ViewHolder>{

    private Activity context;
    List<SubjectResponse> responseList;
    Dialog dialog;
    int groupPosition;
    int childPosition;


    public SubjectListDialogAdapter(Activity context,List<SubjectResponse> data,int groupPosition,int childPosition, Dialog dialog) {
        this.context = context;
        this.responseList = data;
        this.dialog=dialog;
        this.groupPosition=groupPosition;
        this.childPosition=childPosition;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_admin_class_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.name.setText(responseList.get(position).getName());
        viewHolder.description.setText(responseList.get(position).getDescription());

        viewHolder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeTable timeTable=new TimeTable();
                timeTable.setSubjectName(responseList.get(position).getName());
                timeTable.setSubjectId(responseList.get(position).getId());
                timeTable.setDay(Common.Day.valueOf(Helper.getDay(childPosition)));
                try {
                    TimeTable request= AddTimeTableAdapter.timeTableList.get(groupPosition).getTimeTables().get(childPosition);
                    timeTable.setId(request.getId());
                    timeTable.setTeacherId(request.getTeacherId());
                    timeTable.setTeacherName(request.getTeacherName());
                }catch (Exception e){

                }

                List<TimeTable> timeTableList= AddTimeTableAdapter.timeTableList.get(groupPosition).getTimeTables();
                timeTableList.set(childPosition,timeTable);
                AddTimeTableActivity.adapter.notifyDataSetChanged();
                dialog.dismiss();
                updateTeacherList(timeTable.getSubjectName(),timeTable.getId(),timeTable.getDay().value());
            }
        });


    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout main_layout;
        TextView name;
        TextView description;
        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            description=itemView.findViewById(R.id.description);
            main_layout=itemView.findViewById(R.id.main_layout);

        }
    }



    void updateTeacherList(String subjectName,String subjectId, String day) {
        try {
            for (int i = 0; i < TeacherListDialogAdapter.responseList.size(); i++) {
                List<Allocation> allocationList=TeacherListDialogAdapter.responseList.get(i).getAllocations();
                for (int j = 0; j < allocationList.size(); j++) {
                    if (allocationList.get(j).getDay().value().equals(day)
                            && allocationList.get(j).getTimeslot().getStart().
                            equals(AddTimeTableAdapter.timeTableList.get(groupPosition).getTimeslot().getStart())
                            && allocationList.get(j).getTimeslot().getEnd().
                            equals(AddTimeTableAdapter.timeTableList.get(groupPosition).getTimeslot().getEnd())) {
                        Allocation allocation=allocationList.get(j);
                        allocation.setSubjectName(subjectName);
                        allocation.setSubjectId(subjectId);
                    }
                }
            }
        }catch (Exception e){

        }

    }

}