package com.app.mschooling.quiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.class_section_subject.adapter.OptionListAdapter;
import com.app.mschooling.com.R;
import com.mschooling.transaction.response.quiz.QuizQuestionResponse;

import java.util.HashMap;
import java.util.List;

public class StudentQuestionListAdapter extends RecyclerView.Adapter<StudentQuestionListAdapter.ViewHolder> {
    private View view;
    Context context;
    LayoutInflater inflater;
    List<QuizQuestionResponse> myList;
    //    options list
    private RecyclerView rv_option_list;
    private OptionListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    OptionListAdapter.question_answer question_answer_interface;
    HashMap<Integer, String> map_question = new HashMap<Integer, String>();
    HashMap<Integer, String> map_option = new HashMap<Integer, String>();

    public StudentQuestionListAdapter(Context context, List<QuizQuestionResponse> items,
                                      OptionListAdapter.question_answer question_answer_interface) {
        this.context = context;
        myList = items;
        this.question_answer_interface = question_answer_interface;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public StudentQuestionListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        view = inflater.inflate(R.layout.row_student_question_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentQuestionListAdapter.ViewHolder viewHolder, int position) {
        try {
            viewHolder.tv_question_no.setText(""+(position+1));
            viewHolder.tv_question.setText(myList.get(position).getName());

            String question_id = myList.get(position).getId();

            map_question.put(Integer.parseInt(myList.get(position).getId()), question_id);
            mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            mAdapter = new OptionListAdapter(context, map_question,myList.get(position).getChoiceResponses(),map_option,question_answer_interface,question_id);
            viewHolder.rv_option_list.setAdapter(mAdapter);
            viewHolder.rv_option_list.setLayoutManager(mLayoutManager);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rv_option_list;
        TextView tv_question_no,tv_question;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_option_list = (RecyclerView) itemView.findViewById(R.id.rv_option_list);
            tv_question_no = (TextView) itemView.findViewById(R.id.tv_question_no);
            tv_question = (TextView) itemView.findViewById(R.id.tv_question);
        }
    }
}
