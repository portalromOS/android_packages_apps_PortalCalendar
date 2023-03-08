package com.portal.calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.portal.calendar.CalendarNotification.NotificationHelper;
import com.portal.calendar.MonthDay.MonthDayAdapter;
import com.portal.calendar.Utils.CalendarUtils;
import com.portal.calendar.Utils.OnSwipeTouchListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MonthDayAdapter.OnItemListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //For testing night mode theme
        setContentView(R.layout.activity_main);
        CalendarUtils.init();

        monthYearText = findViewById(R.id.monthYear);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        calendarRecyclerView.setOnTouchListener(setupViewSwipeListener());
        //new NotificationHelper(this, null).deleteAllChannels();
        updateUI();
    }
    private OnSwipeTouchListener setupViewSwipeListener(){
        return new OnSwipeTouchListener(this, OnSwipeTouchListener.swipeDirection.HORIZONTAL) {
            @Override
            public void onSwipeLeft() {
                nextMonthAction(calendarRecyclerView);
            }

            @Override
            public void onSwipeRight() {
                prevMonthAction(calendarRecyclerView);
            }
        };
    }
    private void updateUI(){
        setMonthViewTxt();
        setWeekDays();
    }

    private void setWeekDays() {
        Resources res = getResources();
        String[] weekDays = res.getStringArray(R.array.weekDays_sm);

        for(int i = 0; i < 7; i++){
            int id = getResources().getIdentifier("day"+i, "id", getApplicationContext().getPackageName());
            TextView tv = (TextView)findViewById(id);
            tv.setText(weekDays[(CalendarUtils.myFirstDayOfWeek+i)%7]);
        }
    }



    private void setMonthViewTxt() {
        monthYearText.setText(CalendarUtils.monthYearFromDate(getResources(), CalendarUtils.selectedDate));
        ArrayList<Integer> daysInMonth = CalendarUtils.daysInMonthArray(CalendarUtils.selectedDate);

        MonthDayAdapter calendarAdapter = new MonthDayAdapter(daysInMonth, this, this);

        //definir o layout a ser aplicado na recicledView
        /*RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };*/
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);

        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }



    public void prevMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthViewTxt();
    }
    public void nextMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthViewTxt();
    }
    public void monthYearAction(View view) {

    }

    @Override
    public void onItemClick(int position, int day) {
        if(day > 0){
            /*
            String message = "Selected Date "+ day + " " + CalendarUtils.monthYearFromDate(getResources(), CalendarUtils.selectedDate);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            */
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.withDayOfMonth(day);
            startActivity(new Intent(this, DayViewActivity.class));
        }
    }

}