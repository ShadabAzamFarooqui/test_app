package com.app.mschooling.attendance.student.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.response.attendance.AttendanceMonthResponse;
import com.mschooling.transaction.response.subject.StandaloneSubject;

import java.util.List;

public class CustomSpinnerAdapter extends BaseAdapter {
    Context context;
    List<AttendanceMonthResponse> list;
    LayoutInflater inflater;

    public CustomSpinnerAdapter(Context applicationContext, List<AttendanceMonthResponse> list) {
        this.context = applicationContext;
        this.list = list;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.row_spinner, null);
        TextView names =  view.findViewById(R.id.name);
        names.setText(list.get(i).getMonthYear());
        return view;
    }
}
