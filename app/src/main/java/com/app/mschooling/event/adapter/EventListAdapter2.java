package com.app.mschooling.event.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.event.activity.AddEventFinalActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.ParameterConstant;
import com.mschooling.transaction.response.event.EventResponse;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventListAdapter2 extends RecyclerView.Adapter<EventListAdapter2.ViewHolder> {
    Activity context;
    public static List<EventResponse> responseList;
    boolean showActionLayout;

    public EventListAdapter2(Activity context, boolean showActionLayout,List<EventResponse> data) {
        this.context = context;
        this.responseList = data;
        this.showActionLayout = showActionLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_events, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        EventResponse eventResponse = responseList.get(position);
        viewHolder.name.setText(eventResponse.getName());
        viewHolder.date.setText(eventResponse.getDate());
        viewHolder.day.setText(getDayFromDateString(eventResponse.getDate()));

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(context);
                dialog.getWindow().requestFeature(1);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setContentView(R.layout.dialog_event);
                dialog.setCancelable(true);
                TextView message = dialog.findViewById(R.id.message);
                TextView tittle = dialog.findViewById(R.id.tittle);
                TextView date = dialog.findViewById(R.id.date);
                TextView day = dialog.findViewById(R.id.day);
                LinearLayout close = dialog.findViewById(R.id.close);
                TextView edit = dialog.findViewById(R.id.edit);
                TextView delete = dialog.findViewById(R.id.delete);
                LinearLayout actionLayout = dialog.findViewById(R.id.actionLayout);
                if (showActionLayout){
                    actionLayout.setVisibility(View.VISIBLE);
                }else {
                    actionLayout.setVisibility(View.GONE);
                }
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent=new Intent(context, AddEventFinalActivity.class);
                        intent.putExtra("id",responseList.get(position).getId());
                        intent.putExtra("name",responseList.get(position).getName());
                        intent.putExtra("description",responseList.get(position).getDescription());
                        intent.putExtra("date",responseList.get(position).getDate());
                        context.startActivity(intent);
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        EventBus.getDefault().post(new EventDelete(responseList.get(position).getId(),position));
                    }
                });
                tittle.setText(eventResponse.getName());
                message.setText(eventResponse.getDescription());
                date.setText(eventResponse.getDate());
                day.setText(getDayFromDateString(eventResponse.getDate()));
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
                dialog.show();

            }
        });
    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mainLayout;
        TextView name;
        TextView date;
        TextView day;

        public ViewHolder(View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            day = itemView.findViewById(R.id.day);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public static String getDayFromDateString(String stringDate) {
        String[] daysArray = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String day = "";
        int dayOfWeek = 0;
        SimpleDateFormat formatter = new SimpleDateFormat(ParameterConstant.getDateFormat() );
        Date date;
        try {
            date = formatter.parse(stringDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
            if (dayOfWeek < 0) {
                dayOfWeek += 7;
            }
            day = daysArray[dayOfWeek];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return day;
    }

    public void getDialog(String tittle, String message) {
        new AlertDialog.Builder(context)
                .setTitle(tittle)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })

//                .setNegativeButton("Cancel", null)
                .setIcon(R.drawable.without_m)
                .show();
    }
}