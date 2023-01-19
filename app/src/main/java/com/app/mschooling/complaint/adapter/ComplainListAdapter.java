package com.app.mschooling.complaint.adapter;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.GetRoleEvent;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.complain.ComplainResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ComplainListAdapter extends RecyclerView.Adapter<ComplainListAdapter.ViewHolder> implements Filterable {

    Activity context;
    public static List<ComplainResponse> responseList;
    public static List<ComplainResponse> filteredList;

    public ComplainListAdapter(Activity context, List<ComplainResponse> data) {
        this.context = context;
        this.responseList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_complain_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.time.setText(responseList.get(position).getTime());
        viewHolder.title.setText(responseList.get(position).getTitle());
        viewHolder.notice.setText(responseList.get(position).getContent());
        viewHolder.postedBy.setText(responseList.get(position).getCreator());
        if (position==responseList.size()-1&&position>2){
            viewHolder.marginLayout.setVisibility(View.VISIBLE);
        }else {
            viewHolder.marginLayout.setVisibility(View.GONE);
        }
        viewHolder.notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.notice.getMaxLines()<= ParameterConstant.getMaxLine()){
                    viewHolder.notice.setMaxLines(responseList.get(position).getContent().length());
                }else {
                    viewHolder.notice.setMaxLines(ParameterConstant.getMaxLine());
                }
            }
        });

        viewHolder.postedBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value())) {
                    EventBus.getDefault().post(new GetRoleEvent(responseList.get(position).getCreator()));
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
        TextView notice;
        TextView time;
        TextView postedBy;
        ImageView delete;
        LinearLayout image;
        View marginLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            notice = itemView.findViewById(R.id.notice);
            time = itemView.findViewById(R.id.time);
            postedBy = itemView.findViewById(R.id.postedBy);
            delete = itemView.findViewById(R.id.delete);
            image = itemView.findViewById(R.id.image);
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
                    ArrayList<ComplainResponse> filteredList = new ArrayList<>();
                    for (ComplainResponse noticeResponse : responseList) {
                        if (noticeResponse.getTime().toLowerCase().startsWith(charString) /*|| androidVersion.getMobile().startsWith(charString)*/) {
                            filteredList.add(noticeResponse);
                        } else if (noticeResponse.getTitle().toLowerCase().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
                            filteredList.add(noticeResponse);
                        } else if (noticeResponse.getTitle().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
                            filteredList.add(noticeResponse);
                        }
                    }
                    ComplainListAdapter.filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<ComplainResponse>) filterResults.values;
                responseList = filteredList;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}