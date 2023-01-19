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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.homework.activity.HomeWorkListActivity;
import com.app.mschooling.quiz.activity.student.StudentQuizListActivity;
import com.app.mschooling.attendance.student.activity.StudentAttendanceDetailActivity;
import com.app.mschooling.syllabus.activity.SyllabusListActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.user.StudentSetup;
import com.mschooling.transaction.response.subject.StandaloneSubject;
import com.mschooling.transaction.response.subject.StandaloneSubjectResponse;

import java.util.List;

public class SubjectListAdapter extends RecyclerView.Adapter<SubjectListAdapter.ViewHolder> {

    private Activity context;
    List<StandaloneSubjectResponse> responseList;
    String intentString;


    public SubjectListAdapter(Activity context, String intentString, List<StandaloneSubjectResponse> data) {

        this.context = context;
        this.responseList = data;
        this.intentString = intentString;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_subject_list_new, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.name.setText(responseList.get(position).getName());

        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setFocusable(false);
        viewHolder.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        viewHolder.recyclerView.setAdapter(new SubjectListInnerAdapter(context, intentString, responseList.get(position).getStandaloneSubjects()));
    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            recyclerView = itemView.findViewById(R.id.recyclerView);

        }
    }


    public class SubjectListInnerAdapter extends RecyclerView.Adapter<SubjectListInnerAdapter.ViewHolder> {

        private Activity context;
        List<StandaloneSubject> mDetailList;
        String intentString;


        public SubjectListInnerAdapter(Activity context, String intentString, List<StandaloneSubject> data) {

            this.context = context;
            this.mDetailList = data;
            this.intentString = intentString;
        }

        @Override
        public SubjectListInnerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_subject_inner_list_new, viewGroup, false);
            return new SubjectListInnerAdapter.ViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(SubjectListInnerAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
            viewHolder.name.setText(mDetailList.get(position).getName());
            viewHolder.description.setText(mDetailList.get(position).getDescription());

            if (mDetailList.get(position).getSubjectType().getType().value().equals(Common.Type.MANDATORY.value())) {
                viewHolder.star.setVisibility(View.VISIBLE);
            } else {
                viewHolder.star.setVisibility(View.GONE);
            }


            viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (intentString != null) {
                        Intent intent = new Intent();
                        if (intentString.equals("HomeWorkListActivity")) {
                            intent = new Intent(context, HomeWorkListActivity.class);
                        } else if (intentString.equals("SyllabusListActivity")) {
                            intent = new Intent(context, SyllabusListActivity.class);
                        } else if (intentString.equals("StudentAttendanceDetailActivity")) {
                            intent = new Intent(context, StudentAttendanceDetailActivity.class);
                            intent.putExtra("subjectId", mDetailList.get(position).getId());
                        } else if (intentString.equals("StudentQuizListActivity")) {
                            intent.putExtra("subjectId", mDetailList.get(position).getId());
                            intent = new Intent(context, StudentQuizListActivity.class);
                        }
                        if (Preferences.getInstance(context).getROLE().equals(Common.Role.STUDENT.value())) {
                            StudentSetup studentSetup = Preferences.getInstance(context).getConfiguration().getStudentSetup();
                            intent.putExtra("classId", studentSetup.getClassId());
                            intent.putExtra("className", studentSetup.getClassName());
                            intent.putExtra("sectionId", studentSetup.getSectionId());
                            intent.putExtra("sectionName", studentSetup.getSectionName());
                            intent.putExtra("subjectId", mDetailList.get(position).getId());
                            intent.putExtra("subjectName", mDetailList.get(position).getName());
                            context.startActivity(intent);
                        } else {
                            intent.putExtra("classId", context.getIntent().getStringExtra("classId"));
                            intent.putExtra("className", context.getIntent().getStringExtra("className"));
                            intent.putExtra("sectionId", context.getIntent().getStringExtra("sectionId"));
                            intent.putExtra("sectionName", context.getIntent().getStringExtra("sectionName"));
                            intent.putExtra("subjectId", mDetailList.get(position).getId());
                            intent.putExtra("subjectName", mDetailList.get(position).getName());
                            context.startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("id", mDetailList.get(position).getId());
                        intent.putExtra("name", mDetailList.get(position).getName());
                        context.setResult(RESULT_OK, intent);
                        context.finish();
                    }

                }
            });


        }


        @Override
        public int getItemCount() {
            return mDetailList.size();
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


    }


}