package com.app.mschooling.enrollment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mschooling.com.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mschooling.transaction.response.login.AuthenticationSchool;

import java.util.List;

public class UserSelectionSpinnerAdapter extends BaseAdapter {
    Context context;
    List<AuthenticationSchool> list;
    LayoutInflater inflter;

    public UserSelectionSpinnerAdapter(Context applicationContext, List<AuthenticationSchool> list) {
        this.context = applicationContext;
        this.list = list;
        inflter = (LayoutInflater.from(applicationContext));
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.row_user_spinner, null);
        TextView schoolName = (TextView) view.findViewById(R.id.schoolName);
        TextView names = (TextView) view.findViewById(R.id.name);
        TextView enrolmentId = (TextView) view.findViewById(R.id.enrolmentId);
//        TextView address = (TextView) view.findViewById(R.id.address);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        schoolName.setText(list.get(i).getSchoolName());
        names.setText(list.get(i).getName());
        enrolmentId.setText(list.get(i).getEnrollmentId());
//        address.setText(list.get(i).getAddress());

        if (list.get(i).getUrl() == null) {
            image.setImageDrawable(context.getResources().getDrawable(R.drawable.m1));
        } else if (list.get(i).getUrl() == null) {
            image.setImageDrawable(context.getResources().getDrawable(R.drawable.m1));
        } else {
            Glide.with(context)
                    .load(list.get(i).getUrl())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(image);
        }
        return view;
    }
}

