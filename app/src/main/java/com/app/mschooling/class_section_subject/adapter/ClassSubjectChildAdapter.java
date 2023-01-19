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
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.class_section_subject.activity.AddSubjectActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.network.ThisApp;
import com.app.mschooling.utils.ConnectivityReceiver;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.app.mschooling.utils.progress_dialog.MyProgressDialog;
import com.google.gson.Gson;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.request.subject.DeleteSubjectRequest;
import com.mschooling.transaction.response.subject.DeleteSubjectResponse;
import com.mschooling.transaction.response.subject.SubjectResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassSubjectChildAdapter extends RecyclerView.Adapter<ClassSubjectChildAdapter.ViewHolder> {

    private Activity context;
    String classId;
    String className;
    List<SubjectResponse> childList;


    public ClassSubjectChildAdapter(Activity context, String classId, String className, List<SubjectResponse> childList) {

        this.context = context;
        this.classId = classId;
        this.className = className;
        this.childList = childList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_class_subject_child, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.name.setText(childList.get(position).getName());
        viewHolder.description.setText(childList.get(position).getDescription());
        if (childList.get(position).getSubjectType().getType().value().equals(Common.Type.MANDATORY.value())) {
            viewHolder.star.setVisibility(View.VISIBLE);
        } else {
            viewHolder.star.setVisibility(View.GONE);
        }


        if (Preferences.getInstance(context).getROLE().equals(Common.Role.ADMIN.value())) {
            viewHolder.action.setVisibility(View.VISIBLE);
        } else {
            viewHolder.action.setVisibility(View.GONE);
        }

        viewHolder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                //inflating menu from xml resource
                popup.inflate(R.menu.edit_delete);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                Intent intent = new Intent(context, AddSubjectActivity.class);
                                intent.putExtra("update", "true");
                                intent.putExtra("classId", classId);
                                intent.putExtra("className", className);
                                intent.putExtra("data", new Gson().toJson(childList.get(position)));
                                context.startActivity(intent);
                                return true;
                            case R.id.delete:

                                getDialogDelete( position);
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


    }


    @Override
    public int getItemCount() {
        try {
            return childList.size();
        } catch (Exception e) {
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout action;
        LinearLayout mainLayout;
        TextView name;
        TextView star;
        TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            action = itemView.findViewById(R.id.action);
            star = itemView.findViewById(R.id.star);
            description = itemView.findViewById(R.id.description);

        }
    }


    public void getDialogDelete( int position) {
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
                recursion(position,description.getText().toString(),dialog);
            }
        });
        dialog.show();
    }


    void recursion(int position,String description,Dialog dialog1){
        if (!ConnectivityReceiver.isConnected()) {
            Dialog dialog = new Dialog(context);
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_internet);
            dialog.setCancelable(false);
            LinearLayout retry = (LinearLayout) dialog.findViewById(R.id.retry);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    recursion(position,description,dialog1);
                }
            });
            dialog.show();
        } else {
            delete(position,description,dialog1);
        }
    }


    void delete(int position,String description,Dialog dialog){
        MyProgressDialog.getInstance(context).show();
        DeleteSubjectRequest request = new DeleteSubjectRequest();
        request.setDescription(description);
        request.setSubjectId(childList.get(position).getId());
        ThisApp.getApiCommonController(context).deleteSubject(request).enqueue(new Callback<DeleteSubjectResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<DeleteSubjectResponse> call, Response<DeleteSubjectResponse> response) {
                MyProgressDialog.setDismiss();
                if (response.code() == 200) {
                    childList.remove(position);
                    notifyDataSetChanged();
                } else {
                    EventBus.getDefault().post(ParameterConstant.getErrorMessage() + " " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DeleteSubjectResponse> call, Throwable t) {
                MyProgressDialog.setDismiss();
                EventBus.getDefault().post(t.getMessage());
            }
        });
        dialog.dismiss();
    }

}