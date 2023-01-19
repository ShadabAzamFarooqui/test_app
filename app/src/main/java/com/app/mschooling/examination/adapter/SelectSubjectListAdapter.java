package com.app.mschooling.examination.adapter;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.response.subject.SubjectResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectSubjectListAdapter extends RecyclerView.Adapter<SelectSubjectListAdapter.ViewHolder> implements Filterable {

    private Activity context;
    List<SubjectResponse> mDetailList;
    HashMap<Integer, Boolean> map;
    String intentString;

    public static List<SubjectResponse> mFilteredList;


    public SelectSubjectListAdapter(Activity context, String intentString, List<SubjectResponse> data) {

        this.context = context;
        this.mDetailList = data;
        this.intentString = intentString;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_select_subject, viewGroup, false);
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
                Intent intent = new Intent();
                intent.putExtra("id", mDetailList.get(position).getId());
                intent.putExtra("name", mDetailList.get(position).getName());
                context.setResult(RESULT_OK, intent);
                context.finish();

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
        TextView star;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            star = itemView.findViewById(R.id.star);
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
                    List<SubjectResponse> filteredList = new ArrayList<>();
                    for (SubjectResponse androidVersion : mDetailList) {
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
                mFilteredList = (List<SubjectResponse>) filterResults.values;
                mDetailList = mFilteredList;
                notifyDataSetChanged();
            }
        };
    }

}