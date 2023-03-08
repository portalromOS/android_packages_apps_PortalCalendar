package com.portal.calendar.MonthDay;

import static android.widget.LinearLayout.*;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.portal.calendar.Events.CalendarEventModel;
import com.portal.calendar.Events.CalendarEventSQL;
import com.portal.calendar.R;
import com.portal.calendar.Utils.CalendarUtils;

import java.time.LocalDate;
import java.util.ArrayList;

public class MonthDayViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
    public final TextView dayOfMonth_txt;
    //public final ImageView dayOfMonth_event_notification;
    public final LinearLayout dayOfMonth_events_view;
    private final MonthDayAdapter.OnItemListener onItemListener;

    private static  CalendarEventSQL helperSQL;
    private int day;
    public MonthDayViewHolder(@NonNull View itemView, MonthDayAdapter.OnItemListener onItemListener) {
        super(itemView);
        dayOfMonth_txt = itemView.findViewById(R.id.cellEventTextView);
        //dayOfMonth_event_notification= itemView.findViewById(R.id.cellEventNotification);
        dayOfMonth_events_view= itemView.findViewById(R.id.eventsView);

        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    public void setDayOfMonth(Context context, int day){
        this.day = day;
        //dayOfMonth_event_notification.setVisibility(View.GONE);

        Log.i("PORTAL", "setDayOfMonth day "+day);

        if(day>0) {
            dayOfMonth_txt.setText(day + "");
            /*
            LocalDate date = LocalDate.of(CalendarUtils.selectedDate.getYear(), CalendarUtils.selectedDate.getMonth(), day);

            ArrayList<CalendarEventModel> eventsOfTheDay = getHelperSQL(context).getEventsByDayMonthView(date);
            for (CalendarEventModel model:eventsOfTheDay) {
                dayOfMonth_events_view.addView(getEventLayoutByTime(context, model, date));
            }
            */
        }
        else {
            dayOfMonth_txt.setText("");
        }
    }

    private LinearLayout getEventLayoutByTime(Context context, CalendarEventModel model, LocalDate calendarDay){

        //Criar linear Horizontal para trabalhar com Weights como se fosse percentagem
        LinearLayout horizontalSizeManager = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 20);
        params.setMargins(2, 2, 2,2);
        horizontalSizeManager.setLayoutParams(params);
        horizontalSizeManager.setOrientation(HORIZONTAL);
        horizontalSizeManager.setWeightSum(24);


        //Criar imagem do evento
        ImageView event = new ImageView(context);
        int colorId = R.color.highLight;
        if(model.color != null && !model.color.equals("")){
            colorId = CalendarUtils.getColorIdByName(context, model.color);
        }
        if(colorId > 0){
            event.setBackgroundColor(context.getResources().getColor(colorId, null));
        }
        else{
            event.setBackgroundColor(context.getResources().getColor(R.color.highLight, null));

        }

        LinearLayout.LayoutParams eventLayoutParams;

        int totalH = 1;

        if(model.allDay){
            totalH = 24;
        }
        else{

            if(model.date.isBefore(calendarDay)){
                if(model.dateEnd.isAfter(calendarDay)){ // dia inteiro, começa passado acaba futuro
                    totalH = 24;
                }
                else{// passado acaba hj
                    totalH = model.timeEnd.getHour();
                }
            }
            else{
                if(model.dateEnd.isAfter(calendarDay)){// começa hj acaba futuro
                    totalH = 24 - model.time.getHour();
                }
                else{// começa hj acaba hj
                    totalH = model.timeEnd.getHour() - model.time.getHour();
                }

                //Criar uma view para fazer um offset do inicio do evento
                View offsetView = new View(context);
                offsetView.setBackgroundColor(context.getResources().getColor(R.color.transparent, null));

                LinearLayout.LayoutParams offsetLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        model.time.getHour()
                );
                offsetLayoutParams.width = 0;
                offsetView.setLayoutParams(offsetLayoutParams);
                horizontalSizeManager.addView(offsetView);
            }
        }
        totalH = Math.max(totalH, 2);
        eventLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, totalH);
        eventLayoutParams.width = 0;
        event.setLayoutParams(eventLayoutParams);


        horizontalSizeManager.addView(event);

        return horizontalSizeManager;
    }

    private CalendarEventSQL getHelperSQL(Context context){
        if(helperSQL == null){
            helperSQL = new CalendarEventSQL(context);
        }
        return helperSQL;
    }

    @Override
    public void onClick(View view) {
        onItemListener.onItemClick(getAdapterPosition(), day);
    }
}
