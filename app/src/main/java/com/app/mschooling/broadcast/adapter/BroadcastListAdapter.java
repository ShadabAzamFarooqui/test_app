package com.app.mschooling.broadcast.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.response.broadcast.BroadcastResponse;

import java.util.ArrayList;
import java.util.List;

public class BroadcastListAdapter extends RecyclerView.Adapter<BroadcastListAdapter.ViewHolder> implements Filterable {

    Activity context;
    public static List<BroadcastResponse> responseList;
    public static List<BroadcastResponse> filteredList;

    public BroadcastListAdapter(Activity context, List<BroadcastResponse> data) {
        this.context = context;
        this.responseList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_broadcast_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.time.setText(responseList.get(position).getDate());
        viewHolder.title.setText(responseList.get(position).getSubject());
        viewHolder.application.setText(responseList.get(position).getContent());
        viewHolder.postedBy.setText(responseList.get(position).getCreator());
        viewHolder.delete.setVisibility(View.GONE);
        if (position==responseList.size()-1&&position>2){
            viewHolder.marginLayout.setVisibility(View.VISIBLE);
        }else {
            viewHolder.marginLayout.setVisibility(View.GONE);
        }



        viewHolder.application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.application.getMaxLines()<4){
                    viewHolder.application.setMaxLines(responseList.get(position).getContent().length());
                }else {
                    viewHolder.application.setMaxLines(3);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView application;
        TextView time;
        TextView postedBy;
        ImageView delete;

        View marginLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            application = itemView.findViewById(R.id.notice);
            time = itemView.findViewById(R.id.time);
            postedBy = itemView.findViewById(R.id.postedBy);
            delete = itemView.findViewById(R.id.delete);
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
                    ArrayList<BroadcastResponse> filteredList = new ArrayList<>();
                    for (BroadcastResponse noticeResponse : responseList) {
                        /*if (noticeResponse.getTime().toLowerCase().startsWith(charString) *//*|| androidVersion.getMobile().startsWith(charString)*//*) {
                            filteredList.add(noticeResponse);
                        } else if (noticeResponse.getTitle().toLowerCase().contains(charString) *//*|| androidVersion.getMobile().contains(charString)*//*) {
                            filteredList.add(noticeResponse);
                        } else if (noticeResponse.getTitle().contains(charString) *//*|| androidVersion.getMobile().contains(charString)*//*) {
                            filteredList.add(noticeResponse);
                        }*/
                    }
                    BroadcastListAdapter.filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<BroadcastResponse>) filterResults.values;
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