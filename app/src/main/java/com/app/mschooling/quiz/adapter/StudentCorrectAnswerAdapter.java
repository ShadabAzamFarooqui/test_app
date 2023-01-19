package com.app.mschooling.quiz.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.quiz.activity.student.QuizStudentQuestionsListActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.response.quiz.ChoiceResponse;

import java.util.ArrayList;

public class StudentCorrectAnswerAdapter extends RecyclerView.Adapter<StudentCorrectAnswerAdapter.CustomViewHolder> {
    private QuizStudentQuestionsListActivity context;
    private ArrayList<ChoiceResponse> correctData;

    public StudentCorrectAnswerAdapter(QuizStudentQuestionsListActivity context, ArrayList<ChoiceResponse> correctData) {
        this.context=context;
        this.correctData = correctData;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.correct_option_adapter, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int pos) {
            holder.tvName.setText("Ans: "+correctData.get(pos).getName());
    }

    @Override
    public int getItemCount() {
        return correctData.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        public CustomViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }
    }
}
