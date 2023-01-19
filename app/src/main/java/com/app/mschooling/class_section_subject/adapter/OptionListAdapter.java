package com.app.mschooling.class_section_subject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.response.quiz.ChoiceResponse;

import java.util.HashMap;
import java.util.List;

public class OptionListAdapter extends RecyclerView.Adapter<OptionListAdapter.ViewHolder> {
    private View view;
    Context context;
    LayoutInflater inflater;
    List<ChoiceResponse> myList;
    RadioButton selectedRadioButton;
    //interface
    question_answer question_answer_interface;
    HashMap<Integer, String> map_question;
    HashMap<Integer, String> map_option;
    String question_id;

    public OptionListAdapter(Context context,HashMap<Integer, String> map_question, List<ChoiceResponse> items, HashMap<Integer, String> map_option,
                             question_answer question_answer_interface, String question_id) {
        this.context = context;
        myList = items;
        this.map_question = map_question;
        this.map_option = map_option;
        this.question_answer_interface = question_answer_interface;
        this.question_id = question_id;
        inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public OptionListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        view = inflater.inflate(R.layout.row_options_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull OptionListAdapter.ViewHolder viewHolder, int position) {
        viewHolder.rb_option.setText(myList.get(position).getName());

        viewHolder.rb_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                for (QuizOption model : myList)
//                    model.setSelect(false);
//                // Set "checked" the model associated to the clicked radio button
//                myList.get(position).setSelect(true);

                // If current view (RadioButton) differs from previous selected radio button, then uncheck selectedRadioButton
                if (null != selectedRadioButton && !v.equals(selectedRadioButton))
                    selectedRadioButton.setChecked(false);

                // Replace the previous selected radio button with the current (clicked) one, and "check" it
                selectedRadioButton = (RadioButton) v;
                selectedRadioButton.setChecked(true);

             //   String answer_map_id= myList.get(position).getOption();
             //   map_option.put(Integer.parseInt(question_id), answer_map_id);

                question_answer_interface.selectedResult(map_question,map_option);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton rb_option;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rb_option = (RadioButton)itemView.findViewById(R.id.rb_option);
        }
    }

    public interface question_answer{
        void selectedResult(HashMap<Integer, String> map_question, HashMap<Integer, String> map_option);
    }
}
