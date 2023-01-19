package com.app.mschooling.class_section_subject.adapter;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
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

import com.app.mschooling.fee.activity.CommonStudentFeeListActivity;
import com.app.mschooling.timetable.activity.AddTimeTableActivity;
import com.app.mschooling.icard.activity.IdCardListActivity;
import com.app.mschooling.attendance.student.activity.MarkStudentAttendanceActivity;
import com.app.mschooling.students.list.activity.StudentsListActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.IntentWrapper;
import com.google.gson.Gson;
import com.mschooling.transaction.filter.ListCriteria;
import com.mschooling.transaction.response.student.ClassResponse;

import java.util.ArrayList;
import java.util.List;

public class ClassListWithSectionChildAdapter extends RecyclerView.Adapter<ClassListWithSectionChildAdapter.ViewHolder>  {

    private Activity context;
    List<ClassResponse> responseList;
    int groupPosition;
    String intentString;



    public ClassListWithSectionChildAdapter(Activity context,String intentString,int groupPosition, List<ClassResponse> data) {

        this.context = context;
        this.groupPosition = groupPosition;
        this.responseList = data;
        this.intentString = intentString;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_class_section_child, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.name.setText(responseList.get(groupPosition).getSectionResponses().get(position).getName());

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intentString!=null){
                    ListCriteria criteria=new ListCriteria();
                    List<String> classIds=new ArrayList();
                    classIds.add(responseList.get(groupPosition).getId());
                    List<String> sectionIds=new ArrayList();
                    sectionIds.add(responseList.get(groupPosition).getSectionResponses().get(position).getId());
                    criteria.setClassIds(classIds);
                    criteria.setSectionIds(sectionIds);

                    Intent intent=new Intent();
                    if (intentString.equals("StudentsListActivity")){
                        intent=new Intent(context, StudentsListActivity.class);
                    }if (intentString.equals("MarkStudentAttendanceActivity")){
                        intent=new Intent(context, MarkStudentAttendanceActivity.class);
                    }if (intentString.equals("IdCardListActivity")){
                        intent=new Intent(context, IdCardListActivity.class);
                    }if (intentString.equals("ShowStudentAttendanceActivity")){
                        intent=new Intent(context, MarkStudentAttendanceActivity.class);
                    }if (intentString.equals("AddTimeTableActivity")){
                        intent=new Intent(context, AddTimeTableActivity.class);
                    }if (intentString.equals("StudentFeeListActivity")){
                        intent=new Intent(context, CommonStudentFeeListActivity.class);
                    }


                    intent.putExtra("criteria", new Gson().toJson(criteria));
                    intent.putExtra("classId",responseList.get(groupPosition).getId());
                    intent.putExtra("className",responseList.get(groupPosition).getName());
                    intent.putExtra("sectionId",responseList.get(groupPosition).getSectionResponses().get(position).getId());
                    intent.putExtra("sectionName",responseList.get(groupPosition).getSectionResponses().get(position).getName());
                    context.startActivity(intent);
                }else {
                    Intent intent=new Intent();
                    intent.putExtra("classId",responseList.get(groupPosition).getId());
                    intent.putExtra("className",responseList.get(groupPosition).getName());
                    intent.putExtra("sectionId",responseList.get(groupPosition).getSectionResponses().get(position).getId());
                    intent.putExtra("sectionName",responseList.get(groupPosition).getSectionResponses().get(position).getName());
                    context.setResult(RESULT_OK, intent);
                    context.finish();
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        try {
            return responseList.get(groupPosition).getSectionResponses().size();
        }catch (Exception e){
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mainLayout;
        TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            mainLayout=itemView.findViewById(R.id.mainLayout);

        }
    }



}