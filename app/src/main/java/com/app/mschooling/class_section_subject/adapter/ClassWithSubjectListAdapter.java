package com.app.mschooling.class_section_subject.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mschooling.class_section_subject.activity.AddSubjectActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.response.student.ClassResponse;

import java.util.List;

public class ClassWithSubjectListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    public static List<ClassResponse> responseList;


    public ClassWithSubjectListAdapter(Activity context,  List<ClassResponse> data) {
        this.context = context;
        this.responseList = data;
    }

    @Override
    public int getGroupCount() {
        return responseList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return responseList.get(groupPosition).getSectionResponses().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }

        ImageView imageview = (ImageView) convertView.findViewById(R.id.image);
        if (isExpanded) {
            imageview.setImageResource(R.drawable.up_arrow);
        } else {
            imageview.setImageResource(R.drawable.down_arrow);
        }

        TextView clazz = (TextView) convertView.findViewById(R.id.clazz);
        clazz.setTypeface(null, Typeface.BOLD);
        clazz.setText(responseList.get(groupPosition).getName()+" ("+responseList.get(groupPosition).getDescription()+")");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        LinearLayout mainLayout = (LinearLayout) convertView.findViewById(R.id.mainLayout);
        LinearLayout addSubject = (LinearLayout) convertView.findViewById(R.id.addSubject);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        try {
                mainLayout.setVisibility(View.VISIBLE);
                addSubject.setVisibility(View.GONE);
                name.setText(responseList.get(groupPosition).getSectionResponses().get(childPosition).getName());
                description.setText(responseList.get(groupPosition).getSectionResponses().get(childPosition).getDescription());
        }catch (Exception e){
//            mainLayout.setVisibility(View.GONE);
//            addSubject.setVisibility(View.VISIBLE);
        }

        addSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,AddSubjectActivity.class);
                intent.putExtra("id",responseList.get(groupPosition).getId());
                context.startActivity(intent);

            }
        });

        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


}