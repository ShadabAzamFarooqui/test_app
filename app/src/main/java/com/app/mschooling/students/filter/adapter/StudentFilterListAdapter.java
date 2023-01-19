package com.app.mschooling.students.filter.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.filter.ListCriteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentFilterListAdapter extends RecyclerView.Adapter<StudentFilterListAdapter.ViewHolder>{

    private Activity context;
    public static List<String> responseList;
    public static List<String> classIdList;
    HashMap<Integer, Boolean> map;

    public static List<String> mFilteredList;
    ListCriteria criteria;

    public StudentFilterListAdapter(Activity context, List<String> data, List<String> classIdList, ListCriteria criteria) {

        this.context = context;
        this.responseList = data;
        this.classIdList = classIdList;
        this.criteria=criteria;
        this.criteria.setClassIds(new ArrayList<>());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_admin_filter_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.name.setText(responseList.get(position));
        viewHolder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    criteria.getClassIds().add(classIdList.get(position));
                }else {
                    if (criteria.getClassIds().contains(classIdList.get(position))){
                        criteria.getClassIds().remove(classIdList.get(position));
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox name;
        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}