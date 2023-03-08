package com.portal.calendar.Events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.portal.calendar.Utils.CalendarUtils;
import com.portal.calendar.R;

import java.util.List;

public class CalendarEventsAdapter_old extends  ArrayAdapter<CalendarEventModel> {


    public CalendarEventsAdapter_old(@NonNull Context context, List<CalendarEventModel> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        CalendarEventModel event = getItem(position);

        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.calendar_event_cell, parent, false);

        TextView eventCell = convertView.findViewById(R.id.cellEventText);

        String eventTitle = event.name + " " + CalendarUtils.formTime(event.time);
        eventCell.setText(eventTitle);

        return convertView;
    }
}
