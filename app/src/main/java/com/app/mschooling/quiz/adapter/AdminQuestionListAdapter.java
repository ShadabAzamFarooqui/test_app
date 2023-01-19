package com.app.mschooling.quiz.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.quiz.activity.CreateQuestionsActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.google.gson.Gson;
import com.mschooling.transaction.response.quiz.ChoiceResponse;
import com.mschooling.transaction.response.quiz.QuizQuestionResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class AdminQuestionListAdapter extends RecyclerView.Adapter<AdminQuestionListAdapter.ViewHolder> {
    private View view;
    Context context;
    LayoutInflater inflater;
    List<QuizQuestionResponse> data;
    private String quizId;
    String isCreator;

    public AdminQuestionListAdapter(Context context, List<QuizQuestionResponse> data, String quizId,String isCreator) {
        this.context = context;
        this.data = data;
        this.quizId = quizId;
        this.isCreator = isCreator;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public AdminQuestionListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        view = inflater.inflate(R.layout.row_admin_question_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminQuestionListAdapter.ViewHolder viewHolder, int position) {
        viewHolder.tvQuestionName.setText(data.get(position).getName());
        setAdapter(viewHolder,data.get(position).getChoiceResponses());
        if (isCreator.equals("true")){
            viewHolder.action.setVisibility(View.VISIBLE);
        }else {
            viewHolder.action.setVisibility(View.INVISIBLE);
        }
        viewHolder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                //inflating menu from xml resource
                popup.inflate(R.menu.edit_delete);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                Intent intent = new Intent(context, CreateQuestionsActivity.class);
                                intent.putExtra("quizId", quizId);
                                intent.putExtra("whereFrom", "update");
                                intent.putExtra("data",new Gson().toJson(data.get(position)));
                                context.startActivity(intent);
                                return true;
                            case R.id.delete:
                                EventBus.getDefault().post(new EventDelete(data.get(position).getId(), position));
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void setAdapter(ViewHolder viewHolder, List<ChoiceResponse> choiceResponses) {
        try {
            viewHolder.mRecyclerView.setHasFixedSize(true);
            viewHolder.mRecyclerView.setFocusable(false);
            viewHolder.mRecyclerView.setNestedScrollingEnabled(false);
            viewHolder.mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            viewHolder.mRecyclerView.setAdapter(new AdminOptionAdapter(context, choiceResponses));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionName;
        RecyclerView mRecyclerView;
        LinearLayout action;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionName = (TextView) itemView.findViewById(R.id.tvQuestionName);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
            action =  itemView.findViewById(R.id.action);
        }
    }
}
