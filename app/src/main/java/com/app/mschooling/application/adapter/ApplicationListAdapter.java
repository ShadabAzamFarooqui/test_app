package com.app.mschooling.application.adapter;

import android.annotation.SuppressLint;
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
import com.app.mschooling.event_handler.GetRoleEvent;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.application.ApplicationResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListAdapter.ViewHolder> implements Filterable {

    Activity context;
    public static List<ApplicationResponse> responseList;
    public static List<ApplicationResponse> filteredList;

    public ApplicationListAdapter(Activity context, List<ApplicationResponse> data) {
        this.context = context;
        this.responseList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_application_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.time.setText(responseList.get(position).getTime());
        viewHolder.title.setText(responseList.get(position).getTitle());
        viewHolder.application.setText(responseList.get(position).getContent());
        viewHolder.postedBy.setText(responseList.get(position).getCreator());
        viewHolder.delete.setVisibility(View.GONE);
        if (position == responseList.size() - 1 && position > 2) {
            viewHolder.marginLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.marginLayout.setVisibility(View.GONE);
        }

        viewHolder.postedBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value())) {
                    EventBus.getDefault().post(new GetRoleEvent(responseList.get(position).getCreator()));
                }
            }
        });

        viewHolder.application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.application.getMaxLines() <= ParameterConstant.getMaxLine()) {
                    viewHolder.application.setMaxLines(responseList.get(position).getContent().length());
                } else {
                    viewHolder.application.setMaxLines(ParameterConstant.getMaxLine());
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
                    ArrayList<ApplicationResponse> filteredList = new ArrayList<>();
                    for (ApplicationResponse noticeResponse : responseList) {
                        if (noticeResponse.getTime().toLowerCase().startsWith(charString) /*|| androidVersion.getMobile().startsWith(charString)*/) {
                            filteredList.add(noticeResponse);
                        } else if (noticeResponse.getTitle().toLowerCase().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
                            filteredList.add(noticeResponse);
                        } else if (noticeResponse.getTitle().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
                            filteredList.add(noticeResponse);
                        }
                    }
                    ApplicationListAdapter.filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<ApplicationResponse>) filterResults.values;
                responseList = filteredList;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
}