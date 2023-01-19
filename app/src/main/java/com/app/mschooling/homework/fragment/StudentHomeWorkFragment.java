package com.app.mschooling.homework.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.class_section_subject.adapter.SubjectListAdapter;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.filter.SubjectCriteria;
import com.mschooling.transaction.response.subject.GetStandaloneSubjectResponse;

import org.greenrobot.eventbus.Subscribe;

public class StudentHomeWorkFragment extends BaseFragment {

    LinearLayout noRecord;
    RecyclerView recyclerView;
    SubjectListAdapter adapter;
    Button submit;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_home_work, container, false);
        toolbarClick(view,getString(R.string.homework));

        recyclerView =view. findViewById(R.id.recyclerView);
        submit = view. findViewById(R.id.submit);
        noRecord = view. findViewById(R.id.noRecord);
        submit.setVisibility(View.GONE);
        toolbarClick(view,getString(R.string.subjects));
        SubjectCriteria criteria=new SubjectCriteria();
        criteria.setClassId(Preferences.getInstance(getContext()).getConfiguration().getStudentSetup().getClassId());
        apiCallBack(getApiCommonController().getSubjectList(ParameterConstant.getRole(getContext()),
               null,null));
        return view;
    }


    void setAdapter(GetStandaloneSubjectResponse response){
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        adapter = new SubjectListAdapter(getActivity(), "HomeWorkListActivity", response.getStandaloneSubjectResponses());
        recyclerView.setAdapter(adapter);
        if (response.getStandaloneSubjectResponses().size() == 0) {
            noRecord.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noRecord.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }



    @Subscribe
    public void getSubject(GetStandaloneSubjectResponse response) {
        if (response.getStatus().value().equals(Status.SUCCESS.value())) {
            setAdapter(response);
        } else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

}

