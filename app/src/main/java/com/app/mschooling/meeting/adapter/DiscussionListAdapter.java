package com.app.mschooling.meeting.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.response.discussion.DiscussionResponse;

import java.util.ArrayList;
import java.util.List;

public class DiscussionListAdapter extends RecyclerView.Adapter<DiscussionListAdapter.ViewHolder> implements Filterable {

    Activity context;
    public static List<DiscussionResponse> responseList;
    public static List<DiscussionResponse> filteredList;

    public DiscussionListAdapter(Activity context, List<DiscussionResponse> data) {
        this.context = context;
        this.responseList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_discussion_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (position==responseList.size()-1&&position>2){
//            viewHolder.marginLayout.setVisibility(View.VISIBLE);
        }else {
//            viewHolder.marginLayout.setVisibility(View.GONE);
        }

        viewHolder.subject.setText(responseList.get(position).getSubject());
        viewHolder.name.setText(responseList.get(position).getName());
        viewHolder.dateTime.setText(responseList.get(position).getDateTime());
        viewHolder.enrollmentId.setText(responseList.get(position).getEnrollmentId());
    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView subject;
        TextView name;
        TextView dateTime;
        TextView enrollmentId;

        View marginLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.subject);
            name = itemView.findViewById(R.id.name);
            dateTime = itemView.findViewById(R.id.dateTime);
            enrollmentId = itemView.findViewById(R.id.enrollmentId);
            marginLayout = itemView.findViewById(R.id.marginLayout);
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
                    ArrayList<DiscussionResponse> filteredList = new ArrayList<>();
                    for (DiscussionResponse noticeResponse : responseList) {
                        if (noticeResponse.getSubject().toLowerCase().startsWith(charString) /*|| androidVersion.getMobile().startsWith(charString)*/) {
                            filteredList.add(noticeResponse);
                        } else if (noticeResponse.getName().toLowerCase().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
                            filteredList.add(noticeResponse);
                        } else if (noticeResponse.getEnrollmentId().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
                            filteredList.add(noticeResponse);
                        }
                    }
                    DiscussionListAdapter.filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<DiscussionResponse>) filterResults.values;
                responseList = filteredList;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
}