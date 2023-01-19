package com.app.mschooling.quiz.adapter;

import static com.app.mschooling.quiz.activity.student.QuizStudentQuestionsListActivity.constChangePos;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.quiz.activity.student.QuizStudentQuestionsListActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventClickQuestions;
import com.mschooling.transaction.response.quiz.StudentQuizQuestionResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class StudentQuestionNoAdapter extends RecyclerView.Adapter<StudentQuestionNoAdapter.CustomViewHolder> {
    private QuizStudentQuestionsListActivity context;
    private List<StudentQuizQuestionResponse> data;
    private Boolean bool = false;
    private int constantPos;

    public StudentQuestionNoAdapter(QuizStudentQuestionsListActivity context, List<StudentQuizQuestionResponse> data, int constantPos) {
        this.context=context;
        this.data=data;
        constChangePos=constantPos;
        bool = true;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.student_question_no_adapter, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, @SuppressLint("RecyclerView") int pos) {
        holder.contentName.setText(""+(pos+1));
            if (pos==constChangePos){
                holder.contentName.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
            }else {
                holder.contentName.setTextColor(ContextCompat.getColor(context,R.color.black));
            }
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventClickQuestions(pos));
                holder.contentName.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
                constChangePos = pos;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView contentName;
        private LinearLayout mainLayout;
       // private LinearLayout ll_item;
        public CustomViewHolder(View itemView) {
            super(itemView);
            contentName = (TextView) itemView.findViewById(R.id.contentName);
            mainLayout = (LinearLayout) itemView.findViewById(R.id.mainLayout);
        }
    }
}
