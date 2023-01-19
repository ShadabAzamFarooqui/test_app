package com.app.mschooling.event.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mschooling.event.adapter.EventListAdapter;
import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.app.mschooling.event_handler.EventDelete;
import com.app.mschooling.utils.Helper;
import com.app.mschooling.utils.ParameterConstant;
import com.app.mschooling.utils.Preferences;
import com.app.mschooling.utils.calenderview.CalendarListener;
import com.app.mschooling.utils.calenderview.CalendarUtils;
import com.app.mschooling.utils.calenderview.CustomCalendarView;
import com.app.mschooling.utils.calenderview.DayDecorator;
import com.app.mschooling.utils.calenderview.DayView;
import com.mschooling.transaction.common.api.Common;
import com.mschooling.transaction.common.api.Status;
import com.mschooling.transaction.response.event.DeleteEventResponse;
import com.mschooling.transaction.response.event.EventResponse;
import com.mschooling.transaction.response.event.GetEventResponse;

import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CurrentEventFragment extends BaseFragment {

    CustomCalendarView calendarView;
    LinearLayout noRecord;
    RecyclerView recyclerView;
    LinearLayout mainLabel;
    GetEventResponse response;
    SimpleDateFormat simpleDateFormat;
    Date date;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_events, container, false);
        noRecord=view.findViewById(R.id.noRecord);
        calendarView=view.findViewById(R.id.calendar_view);
        recyclerView=view.findViewById(R.id.recyclerView);
        mainLabel=view.findViewById(R.id.mainLabel);
        simpleDateFormat=new SimpleDateFormat(ParameterConstant.getDateFormat() );
        apiCallBack(getApiCommonController().getEventList());
        SimpleDateFormat format=  new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        String dateString =format.format(new Date());
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return view;
    }



    @Subscribe
    public void getEventList(GetEventResponse response){
        if (response.getStatus().value()== Status.SUCCESS.value()) {
            this.response=response;
            intCalendar();
            AllEventFragment.setAdapter(response.getEventResponses(),getActivity());
            filter(Helper.toDate(new SimpleDateFormat(ParameterConstant.getDateFormat() , Locale.getDefault()).format(new Date())));
        }else {
            dialogFailed(response.getMessage().getMessage());
        }
    }

    private void setAdapter(List<EventResponse> eventResponses) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        boolean showActionLayout= Preferences.getInstance(getContext()).getROLE().equals(Common.Role.ADMIN.value())?true:false;
        recyclerView.setAdapter(new EventListAdapter(getActivity(),showActionLayout, eventResponses));
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


    void intCalendar(){
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
        currentCalendar.add(Calendar.MONTH, calendarView.currentMonthIndex);
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarView.setShowOverflowDate(false);
        calendarView.refreshCalendar(currentCalendar);
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                if (!new CalendarUtils(getActivity()).isPastDay(date)) {

                } else {
//                    Toast.makeText(getActivity(), "You cannot select previous date", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onMonthChanged(Date date) {
                CurrentEventFragment.this.date=date;
                filter(date);
            }
        });

        List<DayDecorator> decorators = new ArrayList<>();
        decorators.add(new DisabledColorDecorator());
        calendarView.setDecorators(decorators);
        calendarView.refreshCalendar(currentCalendar);

    }

    void filter(Date date){
        List<EventResponse> responses=new ArrayList<>();
        for (int i = 0; i<CurrentEventFragment.this.response.getEventResponses().size(); i++){
            if (Helper.toDate(CurrentEventFragment.this.response.getEventResponses().get(i).getDate()).getMonth()==date.getMonth()){
                responses.add(CurrentEventFragment.this.response.getEventResponses().get(i));
            }
        }
        setAdapter(responses);
    }


    private class DisabledColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {
            if (new CalendarUtils(getActivity()).isPastDay(dayView.getDate())) {
                int color = Color.parseColor("#a9afb9");
                dayView.setBackgroundColor(color);
            }
            for (int i=0;i<response.getEventResponses().size();i++){
                if (simpleDateFormat.format(dayView.getDate()).equals(response.getEventResponses().get(i).getDate())){
                    if (new CalendarUtils(getActivity()).isPastDay(dayView.getDate())) {
                        dayView.setBackground(getResources().getDrawable(R.drawable.abc));
                    }else {
                        dayView.setBackground(getResources().getDrawable(R.drawable.selected_date_bg));
                    }
                }
            }
        }
    }


    EventDelete event;

    @Subscribe
    public void deleteEvent(EventDelete event) {
        this.event = event;
        apiCallBack(getApiCommonController().deleteEvent(event.getId()));
    }

 @Subscribe
    public void deleteResponse(DeleteEventResponse r) {
     if (r.getStatus().value()== Status.SUCCESS.value()) {
         for (int i=0;i<this.response.getEventResponses().size();i++){
             if (response.getEventResponses().get(i).getId().equals(event.getId())){
                 response.getEventResponses().remove(i);
             }
         }
         intCalendar();
         filter(date);
         AllEventFragment.setAdapter(response.getEventResponses(),getActivity());
     }else {
         dialogFailed(r.getMessage().getMessage());
     }
    }

}
