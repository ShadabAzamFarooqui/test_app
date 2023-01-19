package com.app.mschooling.quiz.adapter;

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

import com.app.mschooling.quiz.activity.student.QuizStudentQuestionsListActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.response.quiz.StudentQuizResponse;

import java.util.List;

public class StudentQuizListAdapter extends RecyclerView.Adapter<StudentQuizListAdapter.ViewHolder> {

    Activity context;
    List<StudentQuizResponse> mDetailList;

    String subjectId;

    public StudentQuizListAdapter(Activity context, List<StudentQuizResponse> data, String subjectId) {
        this.context = context;
        this.mDetailList = data;
        this.subjectId = subjectId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_student_quiz_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.name.setText(mDetailList.get(position).getName());
        viewHolder.level.setText("" + mDetailList.get(position).getLevel());
        viewHolder.totalTime.setText("" + mDetailList.get(position).getTime());
        viewHolder.totalAttempts.setText("" + mDetailList.get(position).getAttempt());
        viewHolder.startTime.setText("" + mDetailList.get(position).getStartTime());
        viewHolder.endTime.setText("" + mDetailList.get(position).getEndTime());

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(context, StudentQuizDashboardActivity.class);
                Intent intent=new Intent(context, QuizStudentQuestionsListActivity.class);
                intent.putExtra("quizId",mDetailList.get(position).getId());
                intent.putExtra("subjectId",subjectId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDetailList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, section, subject, level, totalAttempts, totalTime, startTime, endTime;
        LinearLayout mainLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            section = itemView.findViewById(R.id.section);
            subject = itemView.findViewById(R.id.subject);
            level = itemView.findViewById(R.id.level);
            totalAttempts = itemView.findViewById(R.id.totalAttempts);
            totalTime = itemView.findViewById(R.id.totalTime);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.endTime);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

}