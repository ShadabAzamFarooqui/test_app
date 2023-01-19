package com.app.mschooling.class_section_subject.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.mschooling.transaction.response.subject.StandaloneSubject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class OptionalSubjectAdapter extends RecyclerView.Adapter<OptionalSubjectAdapter.ViewHolder> {

    private Activity context;
    Map<String, List<StandaloneSubject>> responseMap;
    public static Hashtable<Integer, String> mapId;
    public static  Hashtable<Integer, Integer> mapPosition;
    boolean enable;


    public OptionalSubjectAdapter(Activity context, boolean enable, Map<String, List<StandaloneSubject>> data) {
        this.context = context;
        this.enable = enable;
        this.responseMap = data;
        mapId = new Hashtable<>();
        if (mapPosition==null) {
            mapPosition = new Hashtable<>();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_optional, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        List<String> groups = new ArrayList<>(responseMap.keySet());
        List<StandaloneSubject> subjectResponses=responseMap.get(groups.get(position));

        viewHolder.group.setText(groups.get(position));
        CustomAdapter spinnerAdapter= new CustomAdapter(context, subjectResponses);
        viewHolder.spinner.setAdapter(spinnerAdapter);
        viewHolder.spinner.setEnabled(enable);
        viewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mapId.put(position, subjectResponses.get(i).getId());
                mapPosition.put(position, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (mapPosition.get(position) != null) {
            viewHolder.spinner.setSelection(mapPosition.get(position));
        }
    }


    @Override
    public int getItemCount() {
        try {
            return responseMap.size();
        } catch (Exception e) {
            return 0;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        Spinner spinner;
        TextView group;

        public ViewHolder(View itemView) {
            super(itemView);
            spinner = itemView.findViewById(R.id.spinner);
            group = itemView.findViewById(R.id.group);

        }
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }




    public static class CustomAdapter extends BaseAdapter {
        Context context;
        List<StandaloneSubject> list;
        LayoutInflater inflater;

        public CustomAdapter(Context applicationContext, List<StandaloneSubject> list) {
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
            names.setText(list.get(i).getName());
            return view;
        }
    }

}