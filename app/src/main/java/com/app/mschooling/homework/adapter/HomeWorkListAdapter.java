package com.app.mschooling.homework.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.databinding.RowHomeWorkListBinding;
import com.app.mschooling.event_handler.CommonEvent;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.other.adapter.ImageListAdapter;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.response.homework.WorksheetResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeWorkListAdapter extends RecyclerView.Adapter<HomeWorkListAdapter.ViewHolder> implements Filterable {

    private Activity context;
    public List<WorksheetResponse> responseList;
    public List<WorksheetResponse> filteredList;


    public HomeWorkListAdapter(Activity context, List<WorksheetResponse> data) {
        this.context = context;
        this.responseList = data;
        this.filteredList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        RowHomeWorkListBinding binding = RowHomeWorkListBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
        return new HomeWorkListAdapter.ViewHolder(binding.getRoot());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        viewHolder.binding.time.setText(responseList.get(position).getTime());
        viewHolder.binding.title.setText(responseList.get(position).getTitle());
        viewHolder.binding.application.setText(responseList.get(position).getContent());
        viewHolder.binding.postedBy.setText(responseList.get(position).getCreator());
        if (position == responseList.size() - 1 && position > 2) {
            viewHolder.binding.marginLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.binding.marginLayout.setVisibility(View.GONE);
        }

        if (Preferences.isStudent(context)) {
            viewHolder.binding.delete.setVisibility(View.GONE);
        } else {
            viewHolder.binding.delete.setVisibility(View.VISIBLE);
        }

        viewHolder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventDelete(responseList.get(position).getId(), position));
            }
        });

        viewHolder.binding.recyclerView.setHasFixedSize(true);
        viewHolder.binding.recyclerView.setFocusable(false);
        viewHolder.binding.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.binding.recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        viewHolder.binding.recyclerView.setAdapter(new ImageListAdapter(context, responseList.get(position).getFirebases()));


        viewHolder.binding.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               EventBus.getDefault().post(new CommonEvent(position));
            }
        });
//        viewHolder.binding.application.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                EventBus.getDefault().post(new CommonEvent(position));
//                if (viewHolder.binding.application.getMaxLines() == 3) {
//                    viewHolder.binding.application.setMaxLines(responseList.get(position).getContent().length());
//                } else {
//
//                    viewHolder.binding.application.setMaxLines(3);
//                }
//            }
//        });

    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        RowHomeWorkListBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.findBinding(itemView.getRootView());
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredList = responseList;
                } else {
                    List<WorksheetResponse> filteredList = new ArrayList<>();
                    for (WorksheetResponse worksheetResponse : responseList) {
                        if (worksheetResponse.getTitle().toLowerCase().startsWith(charString) /*|| androidVersion.getMobile().startsWith(charString)*/) {
                            filteredList.add(worksheetResponse);
                        } else if (worksheetResponse.getContent().toLowerCase().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
                            filteredList.add(worksheetResponse);
                        }
                    }
                    filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (List<WorksheetResponse>) filterResults.values;
                responseList = filteredList;
                notifyDataSetChanged();
            }
        };
    }

}