package com.portal.calendar;

import static com.portal.calendar.EventEditActivity.CALENDAR_EVENT_BUNDLE_EVENT_ID;
//import static com.portal.calendar.EventEditActivity.CALENDAR_EVENT_BUNDLE_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.portal.calendar.Events.CalendarEventAdapter;
import com.portal.calendar.Events.CalendarEventModel;
import com.portal.calendar.Events.CalendarEventSQL;
import com.portal.calendar.Utils.CalendarUtils;
import com.portal.calendar.Utils.OnSwipeTouchListener;
import com.portal.calendar.Utils.RecyclerViewInterface;

import java.util.ArrayList;

public class DayViewActivity extends AppCompatActivity implements RecyclerViewInterface {
    private  ArrayList<CalendarEventModel> dailyEvents;
    private RecyclerView calendarEventListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_view);

        calendarEventListView= findViewById(R.id.calendarEventListView);
        calendarEventListView.setOnTouchListener(setupListViewSwipeListener());

        updateUI();
        //setEventAdapter();
    }
    @Override
    protected void onResume() {
        super.onResume();
        setEventAdapter();
    }

    private OnSwipeTouchListener setupListViewSwipeListener(){
        return new OnSwipeTouchListener(this, OnSwipeTouchListener.swipeDirection.HORIZONTAL) {
            @Override
            public void onSwipeLeft() {
                nextDayAction(calendarEventListView);
            }

            @Override
            public void onSwipeRight() {
                prevDayAction(calendarEventListView);
            }
        };
    }

    private void setEventAdapter() {
        //ArrayList<CalendarEventModel> dailyEvents = CalendarEventModel.eventsForDate(CalendarUtils.selectedDate);
        CalendarEventSQL helper = new CalendarEventSQL(this);
        dailyEvents = helper.getByDay_minInfo(CalendarUtils.selectedDate);

        CalendarEventAdapter ceAdapter = new CalendarEventAdapter(getApplicationContext(), dailyEvents, this);
        calendarEventListView.setAdapter(ceAdapter);
        calendarEventListView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateUI() {
        setMonthViewTxt();
        setDayTxt();
        setEventAdapter();
    }
    private void setDayTxt() {
        TextView day_txt = (TextView)findViewById(R.id.day);

        String[] weekDays = getResources().getStringArray(R.array.weekDays);

        if(CalendarUtils.selectedDate != null){
            day_txt.setText( weekDays[CalendarUtils.selectedDate.getDayOfWeek().getValue() - 1] + " " + CalendarUtils.selectedDate.getDayOfMonth());
        }
    }

    private void setMonthViewTxt() {
        TextView monthYear_txt = (TextView)findViewById(R.id.monthYear);
        monthYear_txt.setText(CalendarUtils.monthYearFromDate(getResources(), CalendarUtils.selectedDate));
    }
    public void closeDayAction(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void prevDayAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
        updateUI();
    }

    public void nextDayAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
        updateUI();
    }

    public void addEventAction(View view) {
        startActivity(new Intent(this, EventEditActivity.class));
    }

    @Override
    public void onItemClick(int position) {
        long eventId = dailyEvents.get(position).id;

        Intent intent = new Intent(this, EventEditActivity.class);
        Bundle bundle = new Bundle();
        //bundle.putSerializable(CALENDAR_EVENT_BUNDLE_NAME, dailyEvents.get(position));
        bundle.putLong(CALENDAR_EVENT_BUNDLE_EVENT_ID, eventId);
        intent.putExtras(bundle);



        startActivity(intent);
    }
}