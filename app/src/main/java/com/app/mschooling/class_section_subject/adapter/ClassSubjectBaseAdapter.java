package com.app.mschooling.class_section_subject.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.com.R;
import com.app.mschooling.network.pojo.SubjectResponseModel;
import com.app.mschooling.utils.Preferences;
import com.mschooling.transaction.common.api.Common;

import java.util.List;

public class ClassSubjectBaseAdapter extends RecyclerView.Adapter<ClassSubjectBaseAdapter.ViewHolder> {

    private Activity context;
    List<SubjectResponseModel> responseList;


    public ClassSubjectBaseAdapter(Activity context, List<SubjectResponseModel> subjectResponseModelList) {
        this.context = context;
        this.responseList = subjectResponseModelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_class_subject_base, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.name.setText(responseList.get(position).getClassName());
        viewHolder.description.setText(responseList.get(position).getDescription());

        if(responseList.get(position).getDescription().trim().isEmpty()){
            viewHolder.description.setVisibility(View.GONE);
        }else{
            viewHolder.description.setVisibility(View.VISIBLE);
        }


        if (viewHolder.recyclerView.getVisibility() == View.VISIBLE) {
            viewHolder.imageView.setImageResource(R.drawable.up_arrow);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.down_arrow);
        }

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.listContainer.getVisibility() == View.GONE) {
                    viewHolder.listContainer.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.listContainer.setVisibility(View.GONE);
                }

                if (viewHolder.listContainer.getVisibility() == View.VISIBLE) {
                    viewHolder.imageView.setImageResource(R.drawable.up_arrow);
                } else {
                    viewHolder.imageView.setImageResource(R.drawable.down_arrow);
                }

            }
        });

        if (Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value())) {
            viewHolder.delete.setVisibility(View.GONE);
            viewHolder.edit.setVisibility(View.GONE);
        } else {
            viewHolder.delete.setVisibility(View.GONE);
            viewHolder.edit.setVisibility(View.GONE);
        }

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialogDelete();
            }
        });

        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setFocusable(false);
        viewHolder.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        viewHolder.recyclerView.setAdapter(new ClassSubjectChildAdapter(context, responseList.get(position).getClassId(), responseList.get(position).getClassName(), responseList.get(position).getMandatorySubject()));

        viewHolder.recyclerViewOptional.setHasFixedSize(true);
        viewHolder.recyclerViewOptional.setFocusable(false);
        viewHolder.recyclerViewOptional.setNestedScrollingEnabled(false);
        viewHolder.recyclerViewOptional.setLayoutManager(new GridLayoutManager(context, 1));
        viewHolder.recyclerViewOptional.setAdapter(new OptionalBaseAdapter(context, responseList.get(position).getClassId(), responseList.get(position).getClassName(), responseList.get(position).getOptional()));
    }

    @Override
    public int getItemCount() {
        try {
            return responseList.size();
        } catch (Exception e) {
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        ImageView imageView;
        RecyclerView recyclerView;
        RecyclerView recyclerViewOptional;
        LinearLayout listContainer;
        LinearLayout mainLayout;
        LinearLayout delete;
        LinearLayout edit;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            recyclerViewOptional = itemView.findViewById(R.id.recyclerViewOptional);
            imageView = itemView.findViewById(R.id.image);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            listContainer = itemView.findViewById(R.id.listContainer);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);

        }
    }


    public void getDialogDelete() {
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

            }
        });
        dialog.show();
    }

}