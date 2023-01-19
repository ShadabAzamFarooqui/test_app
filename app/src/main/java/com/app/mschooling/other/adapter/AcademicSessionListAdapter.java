package com.app.mschooling.other.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventPublish;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.session.SessionResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class AcademicSessionListAdapter extends RecyclerView.Adapter<AcademicSessionListAdapter.ViewHolder> {

    Activity context;
    public static List<SessionResponse> responseList;
    public static List<SessionResponse> filteredList;

    public AcademicSessionListAdapter(Activity context, List<SessionResponse> data) {
        this.context = context;
        this.responseList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_academic_session, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.name.setText(responseList.get(position).getName());
        if (responseList.get(position).getSessionType().value().equals(Common.SessionType.CURRENT.value())){
            viewHolder.description.setText(responseList.get(position).getStartDate()+" - Till Now");
        }else {
            viewHolder.description.setText(responseList.get(position).getStartDate()+" - "+responseList.get(position).getEndDate());
        }
        viewHolder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.getInstance(context).setAcademicSession(responseList.get(position).getName());
                EventBus.getDefault().post(new EventPublish(responseList.get(position).getId(),position));
            }
        });

        if (responseList.get(position).isSelected()){
            viewHolder.tickLayout.setVisibility(View.VISIBLE);
//            viewHolder.main_layout.setBackground(context.getResources().getDrawable(R.drawable.gradient_solid_border));
        }else {
            viewHolder.tickLayout.setVisibility(View.INVISIBLE);
//            viewHolder.main_layout.setBackground(context.getResources().getDrawable(R.drawable.gradient_border));

        }

    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout main_layout,tickLayout;
        TextView name;
        TextView description;
        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            description=itemView.findViewById(R.id.description);
            main_layout=itemView.findViewById(R.id.main_layout);
            tickLayout=itemView.findViewById(R.id.tickLayout);

        }
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }


}