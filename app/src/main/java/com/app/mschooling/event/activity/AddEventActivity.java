package com.app.mschooling.event.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.event.adapter.EventListAdapter;
import com.app.mschooling.base.activity.BaseActivity;
import com.app.mschooling.com.R;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.calenderview.CalendarListener;
import com.app.mschooling.utils.calenderview.CalendarUtils;
import com.app.mschooling.utils.calenderview.CustomCalendarView;
import com.app.mschooling.utils.calenderview.DayDecorator;
import com.app.mschooling.utils.calenderview.DayView;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.event.EventResponse;
import com.mschooling.transaction.response.event.GetEventResponse;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEventActivity extends BaseActivity {

    CustomCalendarView calendarView;
    RecyclerView recyclerView;
    LinearLayout mainLabel;
    LinearLayout noRecord;
    SimpleDateFormat simpleDateFormat;
    GetEventResponse response;
    Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_events);
        toolbarClick(getString(R.string.add_event));
        CustomCalendarView.enableMark = true;
        mainLabel = findViewById(R.id.mainLabel);
        noRecord = findViewById(R.id.noRecord);
        calendarView = findViewById(R.id.calendar_view);
        recyclerView = findViewById(R.id.recyclerView);

        simpleDateFormat = new SimpleDateFormat(ParameterConstant.getDateFormat() );
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiCallBack(getApiCommonController().getEventList());
    }

    void intCalendar() {
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
        currentCalendar.add(Calendar.MONTH, calendarView.currentMonthIndex);
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarView.setShowOverflowDate(false);
        calendarView.refreshCalendar(currentCalendar);
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.US);
                Intent intent=new Intent(getApplicationContext(),AddEventFinalActivity.class);
                intent.putExtra("date",dateFormatter.format(date));
                startActivity(intent);

            }

            @Override
            public void onMonthChanged(Date date) {
                AddEventActivity.this.date=date;
                filter(date);
//                SimpleDateFormat df = new SimpleDateFormat("MMM-yyyy");
                //Toast.makeText(BottomNavigationActivity.AddEventActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
            }
        });

        List<DayDecorator> decorators = new ArrayList<>();
        decorators.add(new DisabledColorDecorator());
        calendarView.setDecorators(decorators);
        calendarView.refreshCalendar(currentCalendar);

    }


    @Subscribe
    public void getEventList(GetEventResponse response) {
        if (response.getStatus().value() == Status.SUCCESS.value()) {
            this.response=response;
            intCalendar();
            if (date==null){
                filter(Helper.toDate(new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.getDefault()).format(new Date())));
            }else {
                filter(date);
            }
        } else {
            dialogError(response.getMessage().getMessage());        }
    }


    void filter(Date date){
        List<EventResponse> responses=new ArrayList<>();
        for (int i = 0; i< AddEventActivity.this.response.getEventResponses().size(); i++){
            if (Helper.toDate(AddEventActivity.this.response.getEventResponses().get(i).getDate()).getMonth()==date.getMonth()){
                responses.add(AddEventActivity.this.response.getEventResponses().get(i));
            }
        }
        setAdapter(responses);
    }

    private void setAdapter(List<EventResponse> eventResponses) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        recyclerView.setAdapter(new EventListAdapter(this,false, eventResponses));
        if (eventResponses.size()==0){
            mainLabel.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            noRecord.setVisibility(View.VISIBLE);
        }else {
            mainLabel.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            noRecord.setVisibility(View.GONE);
        }
    }

    private class DisabledColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {
            if (new CalendarUtils(AddEventActivity.this).isPastDay(dayView.getDate())) {
                int color = Color.parseColor("#a9afb9");
                dayView.setBackgroundColor(color);
            }
            for (int i = 0; i < response.getEventResponses().size(); i++) {
                if (simpleDateFormat.format(dayView.getDate()).equals(response.getEventResponses().get(i).getDate())) {
                    if (new CalendarUtils(AddEventActivity.this).isPastDay(dayView.getDate())) {
                        dayView.setBackground(getResources().getDrawable(R.drawable.abc));
                    } else {
                        dayView.setBackground(getResources().getDrawable(R.drawable.selected_date_bg));
                    }
                }
            }

        }
    }






}
