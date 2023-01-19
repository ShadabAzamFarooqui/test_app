package com.app.mschooling.class_section_subject.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.response.student.ClassResponse;

import java.util.ArrayList;
import java.util.List;

public class ClassListWithSectionBaseAdapter extends RecyclerView.Adapter<ClassListWithSectionBaseAdapter.ViewHolder> implements Filterable {

    private Activity context;
    List<ClassResponse> mDetailList;
    List<ClassResponse> mFilteredList;
    String intentString;


    public ClassListWithSectionBaseAdapter(Activity context, String intentString, List<ClassResponse> data) {

        this.context = context;
        this.mDetailList = data;
        this.intentString = intentString;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_class_with_section, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.name.setText(mDetailList.get(position).getName());
        viewHolder.description.setText(mDetailList.get(position).getDescription());

        if (mDetailList.get(position).getDescription().trim().isEmpty()) {
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility(View.VISIBLE);
        }

        if (viewHolder.recyclerView.getVisibility() == View.VISIBLE) {
            viewHolder.imageView.setImageResource(R.drawable.up_arrow);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.down_arrow);
        }

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.recyclerView.getVisibility() == View.GONE) {
                    viewHolder.recyclerView.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.recyclerView.setVisibility(View.GONE);
                }

                if (viewHolder.recyclerView.getVisibility() == View.VISIBLE) {
                    viewHolder.imageView.setImageResource(R.drawable.up_arrow);
                } else {
                    viewHolder.imageView.setImageResource(R.drawable.down_arrow);
                }

            }
        });

        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setFocusable(false);
        viewHolder.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        viewHolder.recyclerView.setAdapter(new ClassListWithSectionChildAdapter(context, intentString, position, mDetailList));


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
        TextView name;
        TextView description;
        ImageView imageView;
        RecyclerView recyclerView;
        LinearLayout mainLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            imageView = itemView.findViewById(R.id.image);
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

}