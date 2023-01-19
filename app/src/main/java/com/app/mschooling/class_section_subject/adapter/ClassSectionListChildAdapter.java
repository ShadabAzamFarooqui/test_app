package com.app.mschooling.class_section_subject.adapter;

import android.annotation.SuppressLint;
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
import com.app.mschooling.event_handler.EventDeleteChild;
import com.mschooling.transaction.response.student.ClassResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ClassSectionListChildAdapter extends RecyclerView.Adapter<ClassSectionListChildAdapter.ViewHolder> {

    private Activity context;
    List<ClassResponse> responseList;
    int groupPosition;


    public ClassSectionListChildAdapter(Activity context, int groupPosition, List<ClassResponse> data) {

        this.context = context;
        this.groupPosition = groupPosition;
        this.responseList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_admin_class_child, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.name.setText(responseList.get(groupPosition).getSectionResponses().get(position).getName());
        if (responseList.get(groupPosition).getSectionResponses().size() == 1) {
            viewHolder.delete.setVisibility(View.GONE);
        } else {
            viewHolder.delete.setVisibility(View.VISIBLE);
        }
        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialogDelete(groupPosition, position);
            }
        });


    }


    @Override
    public int getItemCount() {
        try {
            return responseList.get(groupPosition).getSectionResponses().size();
        } catch (Exception e) {
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout delete;
        LinearLayout mainLayout;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            delete = itemView.findViewById(R.id.delete);

        }
    }


    public void getDialogDelete(int position, int childPosition) {
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
                EventBus.getDefault().post(new EventDeleteChild(description.getText().toString(), position, childPosition));
                dialog.dismiss();

            }
        });
        dialog.show();
    }

}