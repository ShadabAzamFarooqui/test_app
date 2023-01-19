package com.app.mschooling.class_section_subject.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.response.student.ClassResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassSectionMultipleBaseAdapter extends RecyclerView.Adapter<ClassSectionMultipleBaseAdapter.ViewHolder> implements Filterable {

    private Activity context;
    List<ClassResponse> mDetailList;
    HashMap<Integer, Boolean> map;

    List<ClassResponse> mFilteredList;


    public ClassSectionMultipleBaseAdapter(Activity context, List<ClassResponse> data) {

        this.context = context;
        this.mDetailList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_base_class_multiple, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.name.setText(mDetailList.get(position).getDescription());
        viewHolder.description.setText(mDetailList.get(position).getName());

        if (mDetailList.get(position).isSelected()) {
            viewHolder.checkbox.setChecked(true);
        } else {
            viewHolder.checkbox.setChecked(false);
        }


        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* selection(position);
                setAllSelected();*/
                selectionNew(position);
                notifyDataSetChanged();
            }
        });

        viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* selection(position);
                setAllSelected();*/
                selectionNew(position);
                notifyDataSetChanged();
            }
        });

        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setFocusable(false);
        viewHolder.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        viewHolder.recyclerView.setAdapter(new ClassSectionMultipleChildAdapter(context, this, position, mDetailList));


    }

    /*void selection(int position) {
        if (position == 0) {
            if (mDetailList.get(position).isSelected()) {
                for (int i = 0; i < mDetailList.size(); i++) {
                    mDetailList.get(i).setSelected(false);
                    for (int j = 0; j < mDetailList.get(i).getSectionResponses()().size(); j++) {
                        mDetailList.get(i).getSectionResponses()().get(j).setSelected(false);
                    }
                }

            } else {
                for (int i = 0; i < mDetailList.size(); i++) {
                    mDetailList.get(i).setSelected(true);
                    for (int j = 0; j < mDetailList.get(i).getSectionResponses()().size(); j++) {
                        mDetailList.get(i).getSectionResponses()().get(j).setSelected(true);
                    }
                }
            }
        } else {
            if (mDetailList.get(position).isSelected()) {
                mDetailList.get(position).setSelected(false);
                for (int i = 0; i < mDetailList.get(position).getSectionResponses()().size(); i++) {
                    mDetailList.get(position).getSectionResponses()().get(i).setSelected(false);
                }
            } else {
                mDetailList.get(position).setSelected(true);
                for (int i = 0; i < mDetailList.get(position).getSectionResponses()().size(); i++) {
                    mDetailList.get(position).getSectionResponses()().get(i).setSelected(true);
                }
            }
        }
    }*/


    void selectionNew(int position) {

//        this loop will uncheck all item
        for (int i=0;i<mDetailList.size();i++){
            if (i!=position){
                mDetailList.get(i).setSelected(false);
                for (int j = 0; j < mDetailList.get(i).getSectionResponses().size(); j++) {
                    mDetailList.get(i).getSectionResponses().get(j).setSelected(false);
                }
            }
        }
//        this block will check specific item
        if (mDetailList.get(position).isSelected()) {
            mDetailList.get(position).setSelected(false);
            for (int i = 0; i < mDetailList.get(position).getSectionResponses().size(); i++) {
                mDetailList.get(position).getSectionResponses().get(i).setSelected(false);
            }
        } else {
            mDetailList.get(position).setSelected(true);
            for (int i = 0; i < mDetailList.get(position).getSectionResponses().size(); i++) {
                mDetailList.get(position).getSectionResponses().get(i).setSelected(true);
            }
        }

    }


    @Override
    public int getItemCount() {
        try {
            return mDetailList.size();
        } catch (Exception e) {
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        TextView name;
        TextView description;
        RecyclerView recyclerView;
        LinearLayout mainLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            mainLayout = itemView.findViewById(R.id.mainLayout);

        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = mDetailList;
                } else {
                    List<ClassResponse> filteredList = new ArrayList<>();
                    for (ClassResponse androidVersion : mDetailList) {
                        if (androidVersion.getName().toLowerCase().startsWith(charString) /*|| androidVersion.getMobile().startsWith(charString)*/) {
                            filteredList.add(androidVersion);
                        } else if (androidVersion.getDescription().toLowerCase().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
                            filteredList.add(androidVersion);
                        }
                    }
                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (List<ClassResponse>) filterResults.values;
                mDetailList = mFilteredList;
                notifyDataSetChanged();
            }
        };
    }


    void setAllSelected() {
        boolean selected = true;
        for (int i = 1; i < mDetailList.size(); i++) {
            if (mDetailList.get(i).isSelected()) {
                for (int j = 0; j < mDetailList.get(i).getSectionResponses().size(); j++) {
                    if (!mDetailList.get(i).getSectionResponses().get(j).isSelected()) {
                        selected = false;
                        break;
                    }
                }
            } else {
                selected = false;
                break;
            }

        }
        mDetailList.get(0).setSelected(selected);
    }
}