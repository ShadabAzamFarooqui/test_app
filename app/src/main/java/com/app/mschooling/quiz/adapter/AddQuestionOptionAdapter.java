package com.app.mschooling.quiz.adapter;

import android.app.Activity;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.request.quiz.ChoiceRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddQuestionOptionAdapter extends RecyclerView.Adapter<AddQuestionOptionAdapter.ViewHolder> {
    Activity context;
    List<ChoiceRequest> responseList;
    Map<Integer, ChoiceRequest> map,mapRadioButton;
    boolean b;
    int pos = -1;

    public AddQuestionOptionAdapter(Activity context, List<ChoiceRequest> responseList) {
        this.context = context;
        this.responseList = responseList;
        map = new HashMap<>();
        mapRadioButton = new HashMap<>();
        b = true;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_add_question_option, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        if (b) {
            map.put(position, responseList.get(position));
        }

        if (position == 0) {
            viewHolder.delete.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.delete.setVisibility(View.VISIBLE);
        }

        try {
            viewHolder.name.setText(map.get(position).getName());
            if (map.get(position).getYesNo().equals(Common.YesNo.NO)){
                viewHolder.radioButtonCorrect.setChecked(false);
            }else {
                viewHolder.radioButtonCorrect.setChecked(true);
                ChoiceRequest request = new ChoiceRequest();
                request = responseList.get(position);
                request.setYesNo(Common.YesNo.YES);
                responseList.set(position,request);
            }
        } catch (Exception e) {
            viewHolder.name.setText("");
        }


//        viewHolder.add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                b = false;
//                ChoiceRequest choiceRequest = new ChoiceRequest();
//                choiceRequest.setName("");
//                responseList.add(position + 1, choiceRequest);
//                putInMap(responseList);
//                notifyDataSetChanged();
//            }
//        });

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b = false;
                responseList.remove(position);
                putInMap(responseList);
                notifyDataSetChanged();
            }
        });

        viewHolder.radioButtonCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b = false;
                ChoiceRequest demo = new ChoiceRequest();
                demo.setName(viewHolder.name.getText().toString());
                for (int i=0;i<responseList.size();i++){
                    responseList.get(i).setYesNo(Common.YesNo.NO);
                }
                demo.setYesNo(Common.YesNo.YES);
                responseList.set(position, demo);
                putInMap(responseList);
                notifyDataSetChanged();
            }
        });
        viewHolder.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ChoiceRequest demo = new ChoiceRequest();
                demo.setName(viewHolder.name.getText().toString());
                if (viewHolder.radioButtonCorrect.isSelected()){
                    demo.setYesNo(Common.YesNo.YES);
                }else {
                    demo.setYesNo(Common.YesNo.NO);
                }
                responseList.set(position, demo);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }

    void putInMap(List<ChoiceRequest> list) {
        for (int i = 0; i < list.size(); i++) {
            map.put(i, list.get(i));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout add;
        LinearLayout delete;
        EditText name;
        RadioButton radioButtonCorrect;
        public ViewHolder(View itemView) {
            super(itemView);
            add = itemView.findViewById(R.id.add);
            delete = itemView.findViewById(R.id.delete);
            name = itemView.findViewById(R.id.name);
            radioButtonCorrect = itemView.findViewById(R.id.radioButtonCorrect);
        }
    }

}
