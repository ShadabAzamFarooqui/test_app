package com.app.mschooling.quiz.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.UnAssignedQuizEvent;
import com.mschooling.transaction.response.quiz.AssignQuizClassResponse;
import com.mschooling.transaction.response.quiz.AssignQuizSectionResponse;
import com.mschooling.transaction.response.quiz.AssignQuizSubjectResponse;
import com.mschooling.transaction.response.quiz.AssignResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssignedClassListAdapter extends RecyclerView.Adapter<AssignedClassListAdapter.ViewHolder> {

    Activity context;
    List<AssignQuizClassResponse> responseList;

    String intent;
    int classPosition;
    int sectionPosition;
    int subjectPosition;

    public AssignedClassListAdapter(Activity context, List<AssignQuizClassResponse> responseList, String intent) {
        this.context = context;
        this.responseList = responseList;
        this.intent = intent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_assigned_class, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        classPosition=position;
        viewHolder.name.setText(responseList.get(position).getName());
        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setFocusable(false);
        viewHolder.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        List list = new ArrayList();
        list.addAll(responseList.get(position).getAssignQuizSectionResponses());
        viewHolder.recyclerView.setAdapter(new AssignedSectionListAdapter(context, list, null));
        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List list = new ArrayList();
                list.addAll(responseList.get(position).getAssignQuizSectionResponses());
                viewHolder.recyclerView.setAdapter(new AssignedSectionListAdapter(context, list, null));
            }
        });

    }

    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recyclerView)
        RecyclerView recyclerView;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.mainLayout)
        LinearLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }


//    =============================================Adapter 2==================================================


    public class AssignedSectionListAdapter extends RecyclerView.Adapter<AssignedSectionListAdapter.ViewHolder> {

        Activity context;
        List<AssignQuizSectionResponse> responseList;
        String intent;

        public AssignedSectionListAdapter(Activity context, List<AssignQuizSectionResponse> data, String intent) {
            this.context = context;
            this.responseList = data;
            this.intent = intent;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_assigned_section, viewGroup, false);
            return new ViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(AssignedSectionListAdapter.ViewHolder viewHolder, int position) {
            sectionPosition=position;
            viewHolder.name.setText(responseList.get(position).getName());
            viewHolder.recyclerView.setHasFixedSize(true);
            viewHolder.recyclerView.setFocusable(false);
            viewHolder.recyclerView.setNestedScrollingEnabled(false);
            viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context));

            List list = new ArrayList();
            list.addAll(responseList.get(position).getAssignQuizSubjectResponses());
            viewHolder.recyclerView.setAdapter(new AssignedSubjectListAdapter(context, list, null));
        }

        @Override
        public int getItemCount() {
            return responseList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.recyclerView)
            RecyclerView recyclerView;
            @BindView(R.id.name)
            TextView name;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }
        }

    }


    public class AssignedSubjectListAdapter extends RecyclerView.Adapter<AssignedSubjectListAdapter.ViewHolder> {

        Activity context;
        List<AssignQuizSubjectResponse> responseList;

        String intent;

        public AssignedSubjectListAdapter(Activity context, List<AssignQuizSubjectResponse> data, String intent) {
            this.context = context;
            this.responseList = data;
            this.intent = intent;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_assigned_subject, viewGroup, false);
            return new ViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(AssignedSubjectListAdapter.ViewHolder viewHolder, int position) {

            subjectPosition=position;
            List list = new ArrayList();
            list.addAll(responseList.get(position).getAssignResponses());

            viewHolder.name.setText(responseList.get(position).getName());
            viewHolder.recyclerView.setHasFixedSize(true);
            viewHolder.recyclerView.setFocusable(false);
            viewHolder.recyclerView.setNestedScrollingEnabled(false);
            viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            viewHolder.recyclerView.setAdapter(new AssignedQuizListAdapter(context, list, null));
        }

        @Override
        public int getItemCount() {
            return responseList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.recyclerView)
            RecyclerView recyclerView;
            @BindView(R.id.name)
            TextView name;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }
        }

    }


    public class AssignedQuizListAdapter extends RecyclerView.Adapter<AssignedQuizListAdapter.ViewHolder> {

        Activity context;
        List<AssignResponse> responseList;

        String intent;

        public AssignedQuizListAdapter(Activity context, List<AssignResponse> data, String intent) {
            this.context = context;
            this.responseList = data;
            this.intent = intent;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_assigned_quiz, viewGroup, false);
            return new ViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(AssignedQuizListAdapter.ViewHolder viewHolder, int position) {
            viewHolder.totalMarks.setText("" + responseList.get(position).getTotalMark());
            viewHolder.totalAttempts.setText("" + responseList.get(position).getTotalAttempts());
            viewHolder.totalTime.setText("" + responseList.get(position).getTotalTime());
            viewHolder.startTime.setText("" + responseList.get(position).getStartTime());
            viewHolder.endTime.setText("" + responseList.get(position).getEndTime());

            viewHolder.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, v);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.unassigned);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    EventBus.getDefault().post(new UnAssignedQuizEvent(classPosition,sectionPosition,subjectPosition,position,responseList.get(position).getId()));
                                    return true;
                                case R.id.delete:

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
            return responseList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.totalMarks)
            TextView totalMarks;
            @BindView(R.id.totalAttempts)
            TextView totalAttempts;
            @BindView(R.id.totalTime)
            TextView totalTime;
            @BindView(R.id.startTime)
            TextView startTime;
            @BindView(R.id.endTime)
            TextView endTime;
            @BindView(R.id.action)
            LinearLayout action;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }
        }

    }
}