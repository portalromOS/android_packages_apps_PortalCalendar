package com.portal.calendar.MonthDay;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.portal.calendar.Utils.CalendarUtils;
import com.portal.calendar.R;

import java.util.ArrayList;

public class MonthDayAdapter extends RecyclerView.Adapter<MonthDayViewHolder> {
    private Context context;
    private ArrayList<Integer> daysOfMonth;
    private final OnItemListener onItemListener;

    public MonthDayAdapter(ArrayList<Integer> daysOfMonth, OnItemListener onItemListener, Context context){
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.context = context;
    }

    public void updateData(ArrayList<Integer> daysOfMonth) {
        this.daysOfMonth = daysOfMonth;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MonthDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("PORTAL", "onCreateViewHolder");

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.month_day_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = 100;//(int) (parent.getHeight()*0.166666666);
        return new MonthDayViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthDayViewHolder holder, int position) {
        Log.i("PORTAL", "onBindViewHolder "+position);

        holder.dayOfMonth_txt.setTextColor(context.getResources().getColor(R.color.text, null));

        if(CalendarUtils.selectedDate.getMonth() == CalendarUtils.today.getMonth() &&
                CalendarUtils.selectedDate.getYear() == CalendarUtils.today.getYear()){
            if(daysOfMonth.get(position) == CalendarUtils.today.getDayOfMonth()){
                holder.dayOfMonth_txt.setTextColor(context.getResources().getColor(R.color.highLight, null));
            }
        }

        holder.setDayOfMonth(context, daysOfMonth.get(position));
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface  OnItemListener{
        void onItemClick(int position, int day);
    }
}
