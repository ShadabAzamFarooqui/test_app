package com.app.mschooling.quiz.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.quiz.activity.student.QuizStudentQuestionsListActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.other.QuestionOptionInterface;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.quiz.ChoiceResponse;

import java.util.List;

public class StudentMcqOptionAdapter extends RecyclerView.Adapter<StudentMcqOptionAdapter.CustomViewHolder> {
    private QuizStudentQuestionsListActivity context;
    private List<ChoiceResponse> data;
    private String[] str = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
    private QuestionOptionInterface questionOptionInterface;
    public StudentMcqOptionAdapter(QuizStudentQuestionsListActivity context, List<ChoiceResponse> data, QuestionOptionInterface questionOptionInterface) {
        this.context=context;
        this.data = data;
        this.questionOptionInterface = questionOptionInterface;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_mcq_option_adapter, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int pos) {
        holder.tvName.setText(data.get(pos).getName());

        if (data.get(pos).getYesNo()!=null && data.get(pos).getYesNo().equals(Common.YesNo.YES)){
            holder.radioButton.setChecked(true);
            holder.mainLayout.setBackground(ContextCompat.getDrawable(context,R.drawable.rectangular_solid_border));
        }else {
            holder.radioButton.setChecked(false);
            holder.mainLayout.setBackground(ContextCompat.getDrawable(context,R.drawable.rectangular_border));
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionOptionInterface.selectIssue(pos,holder.tvName.getText().toString(),data.get(pos));
               // EventBus.getDefault().post(new EventClickCorrectOption(pos,holder.tvName.getText().toString(),data.get(pos)));
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private RadioButton radioButton;
        private LinearLayout mainLayout;
        public CustomViewHolder(View itemView) {
            super(itemView);
            mainLayout = (LinearLayout) itemView.findViewById(R.id.mainLayout);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            radioButton = (RadioButton) itemView.findViewById(R.id.radioButton);
        }
    }
}
