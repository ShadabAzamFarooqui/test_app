package com.app.mschooling.class_section_subject.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.app.mschooling.network.pojo.SubjectResponseModel;

import java.util.List;

public class OptionalBaseAdapter extends RecyclerView.Adapter<OptionalBaseAdapter.ViewHolder> {

    private Activity context;
    String classId;
    String className;
    List<SubjectResponseModel.OptionSubject>childList;


    public OptionalBaseAdapter(Activity context, String classId, String className,   List<SubjectResponseModel.OptionSubject> childList) {

        this.context = context;
        this.classId = classId;
        this.className = className;
        this.childList = childList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.optional_base, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.groupName.setText(childList.get(position).getGroupName().toUpperCase());
        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setFocusable(false);
        viewHolder.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        viewHolder.recyclerView.setAdapter(new OptionalChildAdapter(context, classId, className, childList.get(position).getOptionalSubject(),
                position,childList,this));


    }


    @Override
    public int getItemCount() {
        try {
            return childList.size();
        } catch (Exception e) {
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView groupName;
        RecyclerView recyclerView;


        public ViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }




}