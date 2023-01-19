package com.app.mschooling.class_section_subject.adapter;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.app.mschooling.fee.activity.CommonFeeListActivity;
import com.app.mschooling.fee.activity.CommonStudentFeeListActivity;
import com.app.mschooling.icard.activity.IdCardListActivity;
import com.app.mschooling.students.list.activity.StudentsListActivity;
import com.app.mschooling.study_material.activity.StudyMaterialCategoryListActivity;
import com.app.mschooling.utils.AppUser;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.IntentWrapper;
import com.app.mschooling.video.activity.VideoCategoryListActivity;
import com.google.gson.Gson;
import com.mschooling.transaction.response.student.ClassResponse;

import java.util.ArrayList;
import java.util.List;

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.ViewHolder> implements Filterable {

    private final Activity context;
    List<ClassResponse> mDetailList;

    public static List<ClassResponse> mFilteredList;
    String intentString ;


    public ClassListAdapter(Activity context, List<ClassResponse> data, String intentString) {

        this.context = context;
        this.mDetailList = data;
        this.intentString = intentString;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_admin_class_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.name.setText(mDetailList.get(position).getName());
        viewHolder.description.setText(mDetailList.get(position).getDescription());

        if (Helper.getValidatedString(mDetailList.get(position).getDescription()).isEmpty()){
            viewHolder.description.setVisibility(View.GONE);
        }else{
            viewHolder.description.setVisibility(View.VISIBLE);
        }
        viewHolder.main_layout.setOnClickListener(v -> {
            AppUser.getInstance().setSectionResponse(mDetailList.get(position).getSectionResponses());
            Intent intent = new Intent();
            if (intentString == null) {
                intent.putExtra("class", mDetailList.get(position).getName() + "," + mDetailList.get(position).getId());
                intent.putExtra("id", mDetailList.get(position).getId());
                intent.putExtra("name", mDetailList.get(position).getName());
                context.setResult(RESULT_OK, intent);
                context.finish();
            } else {
                switch (intentString) {
                    case "FeeListActivity":
                        intent = new Intent(context, CommonFeeListActivity.class);
                        intent.putExtra("classId", mDetailList.get(position).getId());
                        intent.putExtra("className", mDetailList.get(position).getName());
                        context.startActivity(intent);
                        break;
                    case "StudentsListActivity":
                        intent = new Intent(context, StudentsListActivity.class);
                        intent.putExtra("classId", mDetailList.get(position).getId());
                        intent.putExtra("className", mDetailList.get(position).getName());
                        intent.putExtra("intent", "StudentsListActivity");
                        context.startActivityForResult(intent, 1);
                        break;
                    case "IdCardListActivity":
                        intent = new Intent(context, IdCardListActivity.class);
                        intent.putExtra("classId", mDetailList.get(position).getId());
                        intent.putExtra("className", mDetailList.get(position).getName());
                        context.startActivity(intent);
                        break;
                    case "StudyMaterialCategoryListActivity":
                        intent = new Intent(context, StudyMaterialCategoryListActivity.class);
                        intent.putExtra("classId", mDetailList.get(position).getId());
                        intent.putExtra("className", mDetailList.get(position).getName());
                        context.startActivity(intent);
                        break;
                    case "VideoCategoryListActivity":
                        intent = new Intent(context, VideoCategoryListActivity.class);
                        intent.putExtra("classId", mDetailList.get(position).getId());
                        intent.putExtra("className", mDetailList.get(position).getName());
                        context.startActivity(intent);
                        break;
                }

            }
        });


    }


    @Override
    public int getItemCount() {
        try {
            return mDetailList.size();
        } catch (Exception e) {
            return 0;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout main_layout;
        TextView name;
        TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            main_layout = itemView.findViewById(R.id.main_layout);

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

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (List<ClassResponse>) filterResults.values;
                mDetailList = mFilteredList;
                notifyDataSetChanged();
            }
        };
    }

}