package com.app.mschooling.other.adapter;

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

import java.util.List;
import java.util.TreeMap;

public class AddCommonAdapter extends RecyclerView.Adapter<AddCommonAdapter.ViewHolder> {

    private Activity context;
    public static  List<String> mDetailList;
    TreeMap<Integer, String> map;


    public AddCommonAdapter(Activity context, List<String> data) {
        this.context = context;
        this.mDetailList = data;
        this.map=new TreeMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dynamic_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.count.setText(""+(position+1));
        viewHolder.subject.setText(mDetailList.get(position));
        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDetailList.remove(position);
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDetailList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout remove;
        TextView count;
        TextView subject;
        public ViewHolder(View itemView) {
            super(itemView);
            remove = (LinearLayout) itemView.findViewById(R.id.remove);
            count = (TextView) itemView.findViewById(R.id.count);
            subject = ((TextView) itemView.findViewById(R.id.subject));

        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}