package com.app.mschooling.class_section_subject.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.class_section_subject.activity.AddClassActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDeleteBase;
import com.google.gson.Gson;
import com.mschooling.transaction.response.student.ClassResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ClassSectionListBaseAdapter extends RecyclerView.Adapter<ClassSectionListBaseAdapter.ViewHolder> {

    private Activity context;
    List<ClassResponse> mDetailList;


    public ClassSectionListBaseAdapter(Activity context, List<ClassResponse> data) {

        this.context = context;
        this.mDetailList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_class_base, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.name.setText(mDetailList.get(position).getName());
        viewHolder.description.setText(mDetailList.get(position).getDescription());

        if(mDetailList.get(position).getDescription().trim().isEmpty()){
            viewHolder.description.setVisibility(View.GONE);
        }else{
            viewHolder.description.setVisibility(View.VISIBLE);
        }

        if (viewHolder.recyclerView.getVisibility() == View.VISIBLE) {
            viewHolder.imageView.setImageResource(R.drawable.up_arrow);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.down_arrow);
        }

        viewHolder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                //inflating menu from xml resource
                popup.inflate(R.menu.edit_delete);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                Intent intent = new Intent(context, AddClassActivity.class);
                                intent.putExtra("data", new Gson().toJson(mDetailList.get(position)));
                                intent.putExtra("id", mDetailList.get(position).getId());
                                context.startActivity(intent);
                                return true;
                            case R.id.delete:
                                getDialogDelete(position);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.recyclerView.getVisibility() == View.GONE) {
                    viewHolder.recyclerView.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.recyclerView.setVisibility(View.GONE);
                }

                if (viewHolder.recyclerView.getVisibility() == View.VISIBLE) {
                    viewHolder.imageView.setImageResource(R.drawable.up_arrow);
                } else {
                    viewHolder.imageView.setImageResource(R.drawable.down_arrow);
                }

            }
        });

//        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, AddClassActivity.class);
//                intent.putExtra("data", new Gson().toJson(mDetailList.get(position)));
//                intent.putExtra("id", mDetailList.get(position).getId());
//                context.startActivity(intent);
//            }
//        });

//        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getDialogDelete(position);
//            }
//        });

        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setFocusable(false);
        viewHolder.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        viewHolder.recyclerView.setAdapter(new ClassSectionListChildAdapter(context,  position, mDetailList));
    }


    @Override
    public int getItemCount() {
        try {
            return mDetailList.size();
        } catch (Exception e) {
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        ImageView imageView;
        RecyclerView recyclerView;
        LinearLayout mainLayout;
        LinearLayout action;
//        LinearLayout delete;
//        LinearLayout edit;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            imageView = itemView.findViewById(R.id.image);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            action = itemView.findViewById(R.id.action);
//            delete = itemView.findViewById(R.id.delete);
//            edit = itemView.findViewById(R.id.edit);

        }
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
                EventBus.getDefault().post(new EventDeleteBase(description.getText().toString(), position));
            }
        });
        dialog.show();
    }

}