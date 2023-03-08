package com.portal.calendar.Events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.portal.calendar.Utils.CalendarUtils;
import com.portal.calendar.R;
import com.portal.calendar.Utils.RecyclerViewInterface;

import java.time.LocalDate;
import java.util.List;

public class CalendarEventAdapter extends  RecyclerView.Adapter<CalendarEventAdapter.DayEventViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private final List<CalendarEventModel> calendarEvents;

    public CalendarEventAdapter(@NonNull Context context, List<CalendarEventModel> events, RecyclerViewInterface recyclerViewInterface) {
        this.calendarEvents = events;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }


    @NonNull
    @Override
    public DayEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.day_event_cell, parent, false);

        return new DayEventViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull DayEventViewHolder holder, int position) {
        //assign values to viewholder based on the position of the recyclerView
        CalendarEventModel model = calendarEvents.get(position);
        if(model.allDay){
            holder.tvTime.setText(R.string.today);
        }
        else{
            String textAux = "";
            if(!model.date.isEqual( model.dateEnd)){
                if(model.date.isBefore(CalendarUtils.selectedDate)){
                    textAux = context.getResources().getString(R.string.dayView_pastStart);
                }
                else{
                    textAux = CalendarUtils.formTime(model.time);
                }

                textAux += System.lineSeparator();

                if(model.dateEnd.isAfter(CalendarUtils.selectedDate)){
                    textAux += context.getResources().getString(R.string.dayView_futureEnd);
                }
                else{
                    textAux += CalendarUtils.formTime(model.timeEnd);
                }
            }
            else{
                textAux = CalendarUtils.formTime(model.time)+ System.lineSeparator() +CalendarUtils.formTime(model.timeEnd);
            }
            holder.tvTime.setText(textAux);
            holder.tvTime.setTextSize(15);
        }

        holder.tvName.setText(model.name);
        holder.tvDetail.setText(model.detail);
    }

    @Override
    public int getItemCount() {
        return calendarEvents.size();
    }

    public static class DayEventViewHolder extends RecyclerView.ViewHolder{

        TextView tvTime;
        TextView tvName;
        TextView tvDetail;

        public DayEventViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            tvTime = itemView.findViewById(R.id.dayEventTime);
            tvName = itemView.findViewById(R.id.dayEventName);
            tvDetail = itemView.findViewById(R.id.dayEventDetail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }


}
