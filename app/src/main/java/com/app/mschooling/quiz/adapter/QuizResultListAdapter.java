package com.app.mschooling.quiz.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.response.quiz.SubmitQuiz;
import com.mschooling.transaction.response.quiz.SubmitQuizResponse;

import java.util.List;

public class QuizResultListAdapter extends RecyclerView.Adapter<QuizResultListAdapter.ViewHolder> {

    Activity context;
    List<SubmitQuizResponse> responseList;

    public QuizResultListAdapter(Activity context, List<SubmitQuizResponse> data) {
        this.context = context;
        this.responseList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_quiz_result_base, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.name.setText(responseList.get(position).getName());
        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setFocusable(false);
        viewHolder.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        viewHolder.recyclerView.setAdapter(new QuizInnerResultListAdapter(context, responseList.get(position).getSubmitQuizzes()));

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


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    static class QuizInnerResultListAdapter extends RecyclerView.Adapter<QuizInnerResultListAdapter.ViewHolder> {

        Activity context;
        List<SubmitQuiz> responseList;

        public QuizInnerResultListAdapter(Activity context, List<SubmitQuiz> data) {
            this.context = context;
            this.responseList = data;
        }

        @Override
        public QuizInnerResultListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_quiz_result, viewGroup, false);
            return new QuizInnerResultListAdapter.ViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(QuizInnerResultListAdapter.ViewHolder viewHolder, int position) {
//           viewHolder.name.setText(responseList.get(position).getName());
//            viewHolder.level.setText("" + responseList.get(position).getLevel());
            viewHolder.totalTime.setText("" + responseList.get(position).getTime());
            viewHolder.totalAttempts.setText("" + responseList.get(position).getTotalNumberOfQuestionAttempted());
            viewHolder.obtainedMarks.setText("" + responseList.get(position).getMarksObtained());
            viewHolder.noOfAttempts.setText("" + responseList.get(position).getTotalNumberOfQuestionAttempted());
            viewHolder.correctAttempts.setText("" + responseList.get(position).getCorrectNumberOfQuestionAttempt());
            viewHolder.totalQuestion.setText("" + responseList.get(position).getTotalNumberOfQuestion());


        }


        @Override
        public int getItemCount() {
            return responseList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView totalQuestion, correctAttempts, level, totalAttempts, totalTime, obtainedMarks, noOfAttempts, time;
            LinearLayout mainLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                totalQuestion = itemView.findViewById(R.id.totalQuestion);
                correctAttempts = itemView.findViewById(R.id.correctAttempts);
                level = itemView.findViewById(R.id.level);
                totalAttempts = itemView.findViewById(R.id.totalAttempts);
                totalTime = itemView.findViewById(R.id.totalTime);
                obtainedMarks = itemView.findViewById(R.id.obtainedMarks);
                noOfAttempts = itemView.findViewById(R.id.noOfAttempts);
                time = itemView.findViewById(R.id.time);
            }
        }


        @Override
        public int getItemViewType(int position) {
            return position;
        }


    }
}