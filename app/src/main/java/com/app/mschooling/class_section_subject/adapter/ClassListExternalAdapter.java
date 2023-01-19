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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.response.student.ExternalClassResponse;

import java.util.ArrayList;
import java.util.List;

public class ClassListExternalAdapter extends RecyclerView.Adapter<ClassListExternalAdapter.ViewHolder> implements Filterable {

    private Activity context;
    List<ExternalClassResponse> mDetailList;
    List<ExternalClassResponse> mFilteredList;


    public ClassListExternalAdapter(Activity context, List<ExternalClassResponse> data) {

        this.context = context;
        this.mDetailList = data;
    }

    @Override
    public ClassListExternalAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_class_with_section, viewGroup, false);
        return new ClassListExternalAdapter.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ClassListExternalAdapter.ViewHolder viewHolder, int position) {
        viewHolder.name.setText(mDetailList.get(position).getDescription());
        viewHolder.description.setText(mDetailList.get(position).getName());

        if (viewHolder.recyclerView.getVisibility()==View.VISIBLE) {
            viewHolder.imageView.setImageResource(R.drawable.up_arrow);
        } else {
            viewHolder. imageView.setImageResource(R.drawable.down_arrow);
        }

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.recyclerView.getVisibility()==View.GONE) {
                    viewHolder.recyclerView.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.recyclerView.setVisibility(View.GONE);
                }

                if (viewHolder.recyclerView.getVisibility()==View.VISIBLE) {
                    viewHolder.imageView.setImageResource(R.drawable.up_arrow);
                } else {
                    viewHolder. imageView.setImageResource(R.drawable.down_arrow);
                }

            }
        });

        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setFocusable(false);
        viewHolder.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        viewHolder.recyclerView.setAdapter(new ExternalClassInnerAdapter(context,  position,mDetailList));



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
                    List<ExternalClassResponse> filteredList = new ArrayList<>();
                    for (ExternalClassResponse androidVersion : mDetailList) {
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
                mFilteredList = (List<ExternalClassResponse>) filterResults.values;
                mDetailList = mFilteredList;
                notifyDataSetChanged();
            }
        };
    }



    public class ExternalClassInnerAdapter extends RecyclerView.Adapter<ExternalClassInnerAdapter.ViewHolder>  {

        private Activity context;
        List<ExternalClassResponse> responseList;
        int groupPosition;


        public ExternalClassInnerAdapter(Activity context,int groupPosition, List<ExternalClassResponse> data) {

            this.context = context;
            this.groupPosition = groupPosition;
            this.responseList = data;
        }

        @Override
        public ExternalClassInnerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_class_section_child, viewGroup, false);
            return new ExternalClassInnerAdapter.ViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(ExternalClassInnerAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
            viewHolder.name.setText(responseList.get(groupPosition).getExternalSectionResponses().get(position).getName());

            viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent();
                    intent.putExtra("classId",responseList.get(groupPosition).getId());
                    intent.putExtra("className",responseList.get(groupPosition).getName());
                    intent.putExtra("sectionId",responseList.get(groupPosition).getExternalSectionResponses().get(position).getId());
                    intent.putExtra("sectionName",responseList.get(groupPosition).getExternalSectionResponses().get(position).getName());
                    context.setResult(RESULT_OK, intent);
                    context.finish();
                }
            });


        }


        @Override
        public int getItemCount() {
            try {
                return responseList.get(groupPosition).getExternalSectionResponses().size();
            }catch (Exception e){
                return 0;
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout mainLayout;
            TextView name;
            public ViewHolder(View itemView) {
                super(itemView);
                name=itemView.findViewById(R.id.name);
                mainLayout=itemView.findViewById(R.id.mainLayout);

            }
        }



    }

}