package com.app.mschooling.quiz.adapter;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.quiz.activity.AssignedQuizActivity;
import com.app.mschooling.quiz.activity.CreateQuizActivity;
import com.app.mschooling.quiz.activity.QuizAdminQuestionsListActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.mschooling.transaction.response.quiz.QuizResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.ViewHolder>{

    Activity context;
    List<QuizResponse> mDetailList;

    String intent;

    public QuizListAdapter(Activity context, List<QuizResponse> data, String intent) {
        this.context = context;
        this.mDetailList = data;
        this.intent = intent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_quiz_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.quizName.setText(mDetailList.get(position).getName());
        viewHolder.questionNo.setText(""+mDetailList.get(position).getSize());
        viewHolder.level.setText(""+mDetailList.get(position).getLevel().value());

        if (mDetailList.get(position).isCreator()){
            viewHolder.action.setVisibility(View.VISIBLE);
        }else {
            viewHolder.action.setVisibility(View.INVISIBLE);
        }

        viewHolder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent==null) {
                    Intent intent = new Intent();
                    intent.putExtra("id", mDetailList.get(position).getId());
                    intent.putExtra("name", mDetailList.get(position).getName());
                    context.setResult(RESULT_OK, intent);
                    context.finish();
                }else if (intent.equals("QuizAdminQuestionsListActivity")) {
                    Intent intent = new Intent(context, QuizAdminQuestionsListActivity.class);
                    intent.putExtra("id", mDetailList.get(position).getId());
                    intent.putExtra("name", mDetailList.get(position).getName());
                    intent.putExtra("isCreator",""+ mDetailList.get(position).isCreator());
                    context.startActivity(intent);

                }else if ("AssignedQuizActivity".equals(intent)){
                    Intent intent = new Intent(context, AssignedQuizActivity.class);
                    intent.putExtra("id", mDetailList.get(position).getId());
                    intent.putExtra("name", mDetailList.get(position).getName());
                    intent.putExtra("level", mDetailList.get(position).getLevel());
                    intent.putExtra("question", ""+mDetailList.get(position).getSize());
                    context.startActivity(intent);
                }
            }
        });


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
                                Intent intent = new Intent(context, CreateQuizActivity.class);
                                intent.putExtra("id", mDetailList.get(position).getId());
                                intent.putExtra("name", mDetailList.get(position).getName());
                                intent.putExtra("type", mDetailList.get(position).getLevel().value());
                                context.startActivity(intent);
                                return true;
                            case R.id.delete:
                                EventBus.getDefault().post(new EventDelete(mDetailList.get(position).getId(), position));
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
        return mDetailList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout main_layout,action;
        TextView quizName,questionNo,level;
        public ViewHolder(View itemView) {
            super(itemView);
            quizName=itemView.findViewById(R.id.quizName);
            questionNo=itemView.findViewById(R.id.questionNo);
            level=itemView.findViewById(R.id.level);
            main_layout=itemView.findViewById(R.id.main_layout);
            action=itemView.findViewById(R.id.action);
        }
    }

}