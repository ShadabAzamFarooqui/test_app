package com.app.mschooling.teachers.list.adapter;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.timetable.adapter.AddTimeTableAdapter;
import com.app.mschooling.timetable.adapter.TeacherClassAllocationAdapter;
import com.app.mschooling.timetable.activity.AddTimeTableActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.Allocation;
import com.mschooling.transaction.common.TimeTable;
import com.mschooling.transaction.common.Timeslot;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.teacher.AllocationResponse;

import java.util.List;

public class TeacherListDialogPlusAdapter extends BaseAdapter {

    Activity context;
    public static List<AllocationResponse> responseList;
    int groupPosition;
    int childPosition;


    public TeacherListDialogPlusAdapter(Activity context, List<AllocationResponse> data, int groupPosition, int childPosition) {

        this.context = context;
        this.responseList = data;
        this.groupPosition = groupPosition;
        this.childPosition = childPosition;
    }





    @Override
    public int getCount() {
        return responseList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).
                        inflate(R.layout.row_admin_class_list2, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            AllocationResponse teacherResponse = responseList.get(position);
            viewHolder.name.setText(teacherResponse.getfName() + " " + teacherResponse.getlName());
            viewHolder.id.setText(teacherResponse.getEnrollmentId());

            viewHolder.recyclerView.setHasFixedSize(true);
            viewHolder.recyclerView.setFocusable(false);
            viewHolder.recyclerView.setNestedScrollingEnabled(false);
            viewHolder.recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            viewHolder.recyclerView.setAdapter(new TeacherClassAllocationAdapter(context, responseList.get(position).getAllocations()));


            viewHolder.main_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TimeTable timeTable = new TimeTable();
                    timeTable.setTeacherName(responseList.get(position).getfName() + " " + responseList.get(position).getlName());
                    timeTable.setTeacherId(responseList.get(position).getEnrollmentId());
                    timeTable.setDay(Common.Day.valueOf(Helper.getDay(childPosition)));
                    try {
                        TimeTable request = AddTimeTableAdapter.timeTableList.get(groupPosition).getTimeTables().get(childPosition);
                        timeTable.setId(request.getId());
                        timeTable.setSubjectId(request.getSubjectId());
                        timeTable.setSubjectName(request.getSubjectName());
                    } catch (Exception e) {

                    }

                    List<TimeTable> timeTableList = AddTimeTableAdapter.timeTableList.get(groupPosition).getTimeTables();
                    timeTableList.set(childPosition, timeTable);
                    AddTimeTableActivity.adapter.notifyDataSetChanged();
                    removeAndAdd(timeTable,Helper.getDay(childPosition),position);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AddTimeTableActivity.dialogPlus.dismiss();
                        }
                    },100);


                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout main_layout;
        TextView id;
        TextView name;
        RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.id);
            name = itemView.findViewById(R.id.name);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            main_layout = itemView.findViewById(R.id.main_layout);

        }
    }


    void removeAndAdd(TimeTable timeTable, String day, int position) {

        for (int i = 0; i < responseList.size(); i++) {
            List<Allocation> allocationList=responseList.get(i).getAllocations();
            for (int j = 0; j < allocationList.size(); j++) {
                if (allocationList.get(j).getDay().value().equals(day)
                        && allocationList.get(j).getTimeslot().getStart().
                        equals(AddTimeTableAdapter.timeTableList.get(groupPosition).getTimeslot().getStart())
                        && allocationList.get(j).getTimeslot().getEnd().
                        equals(AddTimeTableAdapter.timeTableList.get(groupPosition).getTimeslot().getEnd())) {
                    allocationList.remove(j);
                }
            }
        }

        List<Allocation> allocationList = responseList.get(position).getAllocations();
        Allocation allocation = new Allocation();
        allocation.setClassId(AddTimeTableActivity.classId);
        allocation.setClassName(AddTimeTableActivity.className);
        allocation.setSectionId(AddTimeTableActivity.sectionId);
        allocation.setSectionName(AddTimeTableActivity.sectionName);
        allocation.setSubjectName(timeTable.getSubjectName());
        allocation.setSubjectId(timeTable.getSubjectId());
        allocation.setDay(Common.Day.valueOf(day));
        Timeslot timeslot = new Timeslot();
        timeslot.setStart(AddTimeTableAdapter.timeTableList.get(groupPosition).getTimeslot().getStart());
        timeslot.setEnd(AddTimeTableAdapter.timeTableList.get(groupPosition).getTimeslot().getEnd());
        allocation.setTimeslot(timeslot);
        allocationList.add(allocation);
    }


}