package com.app.mschooling.class_section_subject.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.timetable.adapter.AddTimeTableAdapter;
import com.app.mschooling.timetable.activity.AddTimeTableActivity;
import com.app.mschooling.teachers.list.adapter.TeacherListDialogAdapter;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.mschooling.transaction.common.Allocation;
import com.mschooling.transaction.common.TimeTable;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.subject.StandaloneSubject;
import com.mschooling.transaction.response.subject.StandaloneSubjectResponse;

import java.util.List;

/**
 * Created by Shadab Azam Farooqui on 09/04/19.
 */

public class SubjectListDialogPlusAdapter extends BaseAdapter {
    private Activity context;
    List<StandaloneSubjectResponse> responseList;
    int groupPosition;
    int childPosition;

    public SubjectListDialogPlusAdapter(Activity context, List<StandaloneSubjectResponse> data, int groupPosition, int childPosition) {
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
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {// inflate the layout for each list row
        ViewHolder viewHolder;
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).
                        inflate(R.layout.row_subject_list_new, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // get the TextView for item name and item description

            viewHolder.name.setText(responseList.get(position).getName());


            viewHolder.recyclerView.setHasFixedSize(true);
            viewHolder.recyclerView.setFocusable(false);
            viewHolder.recyclerView.setNestedScrollingEnabled(false);
            viewHolder.recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
            viewHolder.recyclerView.setAdapter(new SubjectListDialogPlusInnerAdapter(context, responseList.get(position).getStandaloneSubjects(), groupPosition, childPosition));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        RecyclerView recyclerView;
        TextView name;

        public ViewHolder(View itemView) {
            name = itemView.findViewById(R.id.name);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }


    public class SubjectListDialogPlusInnerAdapter extends RecyclerView.Adapter<SubjectListDialogPlusInnerAdapter.ViewHolder> {

        private final int groupPosition;
        private final int childPosition;
        List<StandaloneSubject> responseList;
        Activity context;


        public SubjectListDialogPlusInnerAdapter(Activity context, List<StandaloneSubject> data, int groupPosition, int childPosition) {
            this.context = context;
            this.responseList = data;
            this.groupPosition = groupPosition;
            this.childPosition = childPosition;
        }

        @Override
        public SubjectListDialogPlusInnerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_subject_inner_list_new, viewGroup, false);
            return new SubjectListDialogPlusInnerAdapter.ViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(SubjectListDialogPlusInnerAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
            viewHolder.name.setText(responseList.get(position).getName());
            viewHolder.description.setText(responseList.get(position).getDescription());

            if (responseList.get(position).getSubjectType().getType().value().equals(Common.Type.MANDATORY.value())) {
                viewHolder.star.setVisibility(View.VISIBLE);
            } else {
                viewHolder.star.setVisibility(View.GONE);
            }


            viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimeTable timeTable = new TimeTable();
                    timeTable.setSubjectName(responseList.get(position).getName());
                    timeTable.setSubjectId(responseList.get(position).getId());
                    timeTable.setDay(Common.Day.valueOf(Helper.getDay(childPosition)));
                    try {
                        TimeTable request = AddTimeTableAdapter.timeTableList.get(groupPosition).getTimeTables().get(childPosition);
                        timeTable.setId(request.getId());
                        timeTable.setTeacherId(request.getTeacherId());
                        timeTable.setTeacherName(request.getTeacherName());
                    } catch (Exception e) {

                    }

                    List<TimeTable> timeTableList = AddTimeTableAdapter.timeTableList.get(groupPosition).getTimeTables();
                    timeTableList.set(childPosition, timeTable);
                    AddTimeTableActivity.adapter.notifyDataSetChanged();
                    updateTeacherList(timeTable.getSubjectName(), timeTable.getId(), timeTable.getDay().value());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AddTimeTableActivity.dialogPlus.dismiss();
                        }
                    }, 80);
                }
            });


        }


        @Override
        public int getItemCount() {
            return responseList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout mainLayout;
            TextView name;
            TextView description;
            TextView star;

            public ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                description = itemView.findViewById(R.id.description);
                star = itemView.findViewById(R.id.star);
                mainLayout = itemView.findViewById(R.id.mainLayout);

            }
        }


        void updateTeacherList(String subjectName, String subjectId, String day) {
            try {
                for (int i = 0; i < TeacherListDialogAdapter.responseList.size(); i++) {
                    List<Allocation> allocationList = TeacherListDialogAdapter.responseList.get(i).getAllocations();
                    for (int j = 0; j < allocationList.size(); j++) {
                        if (allocationList.get(j).getDay().value().equals(day)
                                && allocationList.get(j).getTimeslot().getStart().
                                equals(AddTimeTableAdapter.timeTableList.get(groupPosition).getTimeslot().getStart())
                                && allocationList.get(j).getTimeslot().getEnd().
                                equals(AddTimeTableAdapter.timeTableList.get(groupPosition).getTimeslot().getEnd())) {
                            Allocation allocation = allocationList.get(j);
                            allocation.setSubjectName(subjectName);
                            allocation.setSubjectId(subjectId);
                        }
                    }
                }
            } catch (Exception e) {

            }

        }


    }


}
