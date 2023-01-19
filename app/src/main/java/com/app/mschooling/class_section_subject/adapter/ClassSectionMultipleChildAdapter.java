package com.app.mschooling.class_section_subject.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.response.student.ClassResponse;

import java.util.List;

public class ClassSectionMultipleChildAdapter extends RecyclerView.Adapter<ClassSectionMultipleChildAdapter.ViewHolder> {

    private Activity context;
    List<ClassResponse> responseList;
    ClassSectionMultipleBaseAdapter adapter;
    int groupPosition;


    public ClassSectionMultipleChildAdapter(Activity context, ClassSectionMultipleBaseAdapter adapter, int groupPosition, List<ClassResponse> data) {

        this.context = context;
        this.groupPosition = groupPosition;
        this.responseList = data;
        this.adapter = adapter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_class_section_child, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.name.setText(responseList.get(groupPosition).getSectionResponses().get(position).getName());

        if (responseList.get(groupPosition).getSectionResponses().get(position).isSelected()) {
            viewHolder.mainLayout.setBackground(context.getResources().getDrawable(R.drawable.view_border2));
        } else {
            viewHolder.mainLayout.setBackground(context.getResources().getDrawable(R.drawable.gradient_border));
        }

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (responseList.get(groupPosition).getSectionResponses().get(position).isSelected()) {
                    responseList.get(groupPosition).getSectionResponses().get(position).setSelected(false);
                } else {
                    responseList.get(groupPosition).getSectionResponses().get(position).setSelected(true);
                }
//                setAllSectionSelected();
//                setAllSelected();
                setAllSectionSelectedNew();
                adapter.notifyDataSetChanged();
                notifyDataSetChanged();
            }
        });


    }


    @Override
    public int getItemCount() {
        try {
            return responseList.get(groupPosition).getSectionResponses().size();
        } catch (Exception e) {
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mainLayout;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            mainLayout = itemView.findViewById(R.id.mainLayout);

        }
    }


    void setAllSelected() {
        boolean selected = true;
        for (int i = 1; i < responseList.size(); i++) {
            if (responseList.get(i).isSelected()) {
                for (int j = 0; j < responseList.get(i).getSectionResponses().size(); j++) {
                    if (!responseList.get(i).getSectionResponses().get(j).isSelected()) {
                        selected = false;
                        break;
                    }
                }
            } else {
                selected = false;
                break;
            }

        }
        responseList.get(0).setSelected(selected);
    }


    void setAllSectionSelected() {
        boolean selected = true;
        for (int i = 0; i < responseList.get(groupPosition).getSectionResponses().size(); i++) {
            if (!responseList.get(groupPosition).getSectionResponses().get(i).isSelected()) {
                selected = false;
            }
        }
        responseList.get(groupPosition).setSelected(selected);
    }


    void setAllSectionSelectedNew() {

        for (int i = 0; i < responseList.size(); i++) {
            if (i!=groupPosition){
                responseList.get(i).setSelected(false);
                for (int j = 0; j < responseList.get(i).getSectionResponses().size(); j++) {
                    responseList.get(i).getSectionResponses().get(j).setSelected(false);
                }
            }

        }

        boolean selected = true;
        for (int i = 0; i < responseList.get(groupPosition).getSectionResponses().size(); i++) {
            if (!responseList.get(groupPosition).getSectionResponses().get(i).isSelected()) {
                selected = false;
            }
        }
        responseList.get(groupPosition).setSelected(selected);
    }


}