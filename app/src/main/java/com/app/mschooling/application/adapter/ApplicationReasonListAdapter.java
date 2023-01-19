package com.app.mschooling.application.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDeleteBase;
import com.mschooling.transaction.response.application.ApplicationReasonResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.TreeMap;

public class ApplicationReasonListAdapter extends RecyclerView.Adapter<ApplicationReasonListAdapter.ViewHolder> {

    private Activity context;
    public static  List<ApplicationReasonResponse> mDetailList;
    TreeMap<Integer, String> map;


    public ApplicationReasonListAdapter(Activity context, List<ApplicationReasonResponse> data) {
        this.context = context;
        this.mDetailList = data;
        this.map=new TreeMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_complaint_reason, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.count.setText(""+(position+1));
        viewHolder.name.setText(mDetailList.get(position).getTitle());

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventDeleteBase(position));
//                getDialogDelete(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mDetailList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout delete;
        TextView count;
        TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            delete = (LinearLayout) itemView.findViewById(R.id.delete);
            count = (TextView) itemView.findViewById(R.id.count);
            name = ((TextView) itemView.findViewById(R.id.name));

        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }



    public void getDialogDelete(int position) {
        Dialog dialog = new Dialog(context);
      dialog.getWindow().requestFeature(1);
      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.delete_dialog);
        dialog.setCancelable(false);
        TextView delete = (TextView) dialog.findViewById(R.id.delete);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView description = (TextView) dialog.findViewById(R.id.description);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                EventBus.getDefault().post(new EventDeleteBase(position));
            }
        });
        dialog.show();
    }


}