package com.app.mschooling.event.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.event.adapter.EventListAdapter2;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.response.event.EventResponse;

import java.util.List;

public class AllEventFragment extends BaseFragment {

    LinearLayout noRecord;
    RecyclerView recyclerView;
    LinearLayout mainLabel;
    static AllEventFragment context;
    static Activity activity;
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_events, container, false);
        context=this;
        activity=getActivity();
        noRecord=view.findViewById(R.id.noRecord);
        mainLabel=view.findViewById(R.id.mainLabel);
        recyclerView=view.findViewById(R.id.recyclerView);
        return view;
    }



    public static void setAdapter(List<EventResponse> eventResponses, FragmentActivity activity) {
        context.recyclerView.setHasFixedSize(true);
        context.recyclerView.setFocusable(false);
        context.recyclerView.setNestedScrollingEnabled(false);
        context.recyclerView.setLayoutManager(new GridLayoutManager(activity, 1));
        boolean showActionLayout= Preferences.getInstance(activity).getROLE().equals(Common.Role.ADMIN.value())?true:false;
        context.recyclerView.setAdapter(new EventListAdapter2(activity, showActionLayout,eventResponses));
        if (eventResponses.size()==0){
            context.mainLabel.setVisibility(View.GONE);
            context.recyclerView.setVisibility(View.GONE);
            context.noRecord.setVisibility(View.VISIBLE);
        }else {
            context.mainLabel.setVisibility(View.VISIBLE);
            context.recyclerView.setVisibility(View.VISIBLE);
            context.noRecord.setVisibility(View.GONE);
        }
    }



}
