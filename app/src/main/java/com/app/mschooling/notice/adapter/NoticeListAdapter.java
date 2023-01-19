package com.app.mschooling.notice.adapter;

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
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.other.adapter.ImageListAdapter;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.event_handler.GetRoleEvent;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.notice.NoticeResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.ViewHolder> implements Filterable {

    Activity context;
    public static List<NoticeResponse> responseList;
    public static List<NoticeResponse> filteredList;

    public NoticeListAdapter(Activity context, List<NoticeResponse> data) {
        this.context = context;
        this.responseList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_notice_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        if (Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value())) {
            viewHolder.delete.setVisibility(View.VISIBLE);
        } else {
            viewHolder.delete.setVisibility(View.GONE);
        }
        viewHolder.time.setText(responseList.get(position).getTime());
        viewHolder.title.setText(responseList.get(position).getTitle().trim());
        viewHolder.notice.setText(responseList.get(position).getContent().trim());
        viewHolder.postedBy.setText(responseList.get(position).getCreator());
        if (Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value())) {
            if (position == responseList.size() - 1) {
                viewHolder.marginLayout.setVisibility(View.VISIBLE);
            } else {
                viewHolder.marginLayout.setVisibility(View.GONE);
            }
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


        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventDelete(responseList.get(position).getId(), position));
            }
        });

        viewHolder.notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.notice.getMaxLines() <= ParameterConstant.getMaxLine()) {
                    viewHolder.notice.setMaxLines(responseList.get(position).getContent().length());
                } else {
                    viewHolder.notice.setMaxLines(ParameterConstant.getMaxLine());
                }
            }
        });

        viewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                String shareMessage = "*" + responseList.get(position).getTitle() + "*\n" + responseList.get(position).getContent() + "\n\n_For more details, please download App:_  \nhttps://play.google.com/store/apps/details?id=com.app.mschooling";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                shareIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(shareIntent, "Share with"));
            }
        });


        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setFocusable(false);
        viewHolder.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        viewHolder.recyclerView.setAdapter(new ImageListAdapter(context, responseList.get(position).getFirebase()));


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
        ImageView share;
        RecyclerView recyclerView;
        View marginLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            notice = itemView.findViewById(R.id.notice);
            time = itemView.findViewById(R.id.time);
            postedBy = itemView.findViewById(R.id.postedBy);
            delete = itemView.findViewById(R.id.delete);
            share = itemView.findViewById(R.id.share);
            marginLayout = itemView.findViewById(R.id.marginLayout);
            recyclerView = itemView.findViewById(R.id.recyclerView);
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
                    ArrayList<NoticeResponse> filteredList = new ArrayList<>();
                    for (NoticeResponse noticeResponse : responseList) {
                        if (noticeResponse.getTime().toLowerCase().startsWith(charString) /*|| androidVersion.getMobile().startsWith(charString)*/) {
                            filteredList.add(noticeResponse);
                        } else if (noticeResponse.getTitle().toLowerCase().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
                            filteredList.add(noticeResponse);
                        } else if (noticeResponse.getTitle().contains(charString) /*|| androidVersion.getMobile().contains(charString)*/) {
                            filteredList.add(noticeResponse);
                        }
                    }
                    NoticeListAdapter.filteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<NoticeResponse>) filterResults.values;
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