package com.app.mschooling.class_section_subject.adapter;

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
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.response.student.ClassResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignUpClassListAdapter extends RecyclerView.Adapter<SignUpClassListAdapter.ViewHolder> implements Filterable {

    private Activity context;
    public static  List<ClassResponse> mDetailList;
    public static HashMap<String, String> selectedItems;
    public static  List<ClassResponse> mFilteredList;

    public SignUpClassListAdapter(Activity context, List<ClassResponse> data) {

        this.context = context;
        this.mDetailList = data;
        selectedItems=new HashMap<>();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_sign_up_class, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.name.setText(mDetailList.get(position).getName());
        viewHolder.description.setText(mDetailList.get(position).getDescription());

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.checkBox.isChecked()){
                    viewHolder.checkBox.setChecked(false);
                    selectedItems.remove(mDetailList.get(position).getId());
                }else {
                    viewHolder.checkBox.setChecked(true);
                    selectedItems.put(mDetailList.get(position).getId(),mDetailList.get(position).getDescription());
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return mDetailList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mainLayout;
        TextView name;
        TextView description;
        CheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            description=itemView.findViewById(R.id.description);
            mainLayout=itemView.findViewById(R.id.mainLayout);
            checkBox=itemView.findViewById(R.id.checkbox);

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
                mFilteredList = ( List<ClassResponse>) filterResults.values;
                mDetailList = mFilteredList;
                notifyDataSetChanged();
            }
        };
    }

}