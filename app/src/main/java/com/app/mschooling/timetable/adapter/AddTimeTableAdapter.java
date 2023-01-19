package com.app.mschooling.timetable.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.timetable.activity.AddTimeTableActivity;
import com.app.mschooling.com.R;
import com.mschooling.transaction.common.TimeTableRow;
import com.mschooling.transaction.common.Timeslot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class AddTimeTableAdapter extends RecyclerView.Adapter<AddTimeTableAdapter.ViewHolder> {

    AddTimeTableActivity context;
    public static List<TimeTableRow> timeTableList;
    Dialog dialog;
    TextView space;
    private Integer hour;
    private Integer minute;
    boolean isStudent;


    public AddTimeTableAdapter(AddTimeTableActivity context, boolean isStudent, List<TimeTableRow> timeTableList, TextView space) {
        this.context = context;
        this.timeTableList = timeTableList;
        this.space = space;
        this.isStudent = isStudent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_time_table2, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.srNo.setText("" + (position + 1));

        if (timeTableList.get(position).getTimeslot().getStart().equals("From")) {
            viewHolder.fromIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.fromIcon.setVisibility(View.GONE);
        }
        if (timeTableList.get(position).getTimeslot().getEnd().equals("To")) {
            viewHolder.toIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.toIcon.setVisibility(View.GONE);
        }

        if (timeTableList.get(position).getTimeslot().getStart().equals("From")) {
            viewHolder.from.setText("");
        } else {
            viewHolder.from.setText("" + timeTableList.get(position).getTimeslot().getStart());

        }

        if (timeTableList.get(position).getTimeslot().getEnd().equals("To")) {
            viewHolder.to.setText("");
        } else {
            viewHolder.to.setText("" + timeTableList.get(position).getTimeslot().getEnd());

        }


        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setFocusable(false);
        viewHolder.recyclerView.setNestedScrollingEnabled(false);
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        viewHolder.recyclerView.setAdapter(new TimeTableHorizontalAdapter(context, isStudent, timeTableList, position));

        viewHolder.from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStudent) {
                    if (checkTime(position)) {
                        timePicker(position, "from");
                    } else {
                        context.dialogError( "Please select the time of previous period");
                    }
                }

            }
        });

        viewHolder.to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStudent) {
                    if (timeTableList.get(position).getTimeslot().getStart().equals("From")) {
                        context.dialogError( "Please select the start time of the period");
                        return;
                    }
                    if (checkTime(position)) {
                        timePicker(position, "to");
                    } else {
                        context.dialogError( "Please select the time of previous period");
                    }
                }
            }
        });


        if (timeTableList.size() == 1) {
//            space.setVisibility(View.GONE);
            space.setVisibility(View.VISIBLE);
        } else {
            space.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return timeTableList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView srNo;
        TextView from;
        TextView to;
        RecyclerView recyclerView;
        ImageView fromIcon;
        ImageView toIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            srNo = itemView.findViewById(R.id.srNo);
            from = itemView.findViewById(R.id.from);
            to = itemView.findViewById(R.id.to);
            toIcon = itemView.findViewById(R.id.toIcon);
            fromIcon = itemView.findViewById(R.id.fromIcon);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }


    void timePicker(int position, String type) {
        final Calendar c = Calendar.getInstance();
        int hour;
        int minute;
        if (AddTimeTableAdapter.this.hour == null) {
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        } else {
            hour = AddTimeTableAdapter.this.hour;
            minute = AddTimeTableAdapter.this.minute;
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String h;
                        String m;
                        if (hourOfDay <= 9) {
                            h = "0" + hourOfDay;
                        } else {
                            h = "" + hourOfDay;
                        }
                        if (minute <= 9) {
                            m = "0" + minute;
                        } else {
                            m = "" + minute;
                        }
                        if (type.equals("from")) {
                            if (position > 0) {
                                String to = timeTableList.get(position - 1).getTimeslot().getEnd();
                                if (checkGreater(to, "" + h + ":" + m)) {
                                    setText(position, h, m, type);
                                } else {
                                    context.dialogError( "You can't select less time from previously selected time");
                                }
                            } else {
                                setText(position, h, m, type);
                            }
                        } else {
                            String from = timeTableList.get(position).getTimeslot().getStart();
                            if (from.equals("" + h + ":" + m)){
                                context.dialogError( "You can't select same start and end time");
                                return;
                            }
                            if (checkGreater(from, "" + h + ":" + m)) {
                                setText(position, h, m, type);
                                try {
                                    Timeslot timeslot = timeTableList.get(position+1).getTimeslot();
                                    timeslot.setStart(h + ":" + m);
                                    timeTableList.get(position+1).setTimeslot(timeslot);
                                    notifyDataSetChanged();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            } else {
                                context.dialogError( "You can't select less time from previously selected time");
                            }
                        }
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }


    boolean checkTime(int position) {
        for (int i = 0; i < position; i++) {
            try {
                if (AddTimeTableAdapter.timeTableList.get(i).getTimeslot().getStart().equals("From")) {
                    return false;
                }
                if (AddTimeTableAdapter.timeTableList.get(i).getTimeslot().getEnd().equals("To")) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }

        }
        return true;

    }


    void setText(int position, String hour, String minute, String type) {
        AddTimeTableAdapter.this.hour = Integer.valueOf(hour);
        AddTimeTableAdapter.this.minute = Integer.valueOf(minute);
        Timeslot timeslot = timeTableList.get(position).getTimeslot();
        if (type.equals("from")) {
            timeslot.setStart(hour + ":" + minute);
            timeTableList.get(position).setTimeslot(timeslot);
        } else {
            timeslot.setEnd(hour + ":" + minute);
            timeTableList.get(position).setTimeslot(timeslot);
        }
        notifyDataSetChanged();


    }


    boolean checkGreater(String time1, String time2) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        try {
            if (dateFormat.parse(time1).after(dateFormat.parse(time2))) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    }


}