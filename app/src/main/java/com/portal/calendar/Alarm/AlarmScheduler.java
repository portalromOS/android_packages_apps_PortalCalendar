package com.portal.calendar.Alarm;

import static com.portal.calendar.Alarm.AlarmReceiver.ALARM_ITEM_NOTIFICATION;
import static com.portal.calendar.Alarm.AlarmReceiver.ALARM_ITEM_NOTIFICATION_EVENT_ID;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class AlarmScheduler implements  AlarmSchedulerInterface{
    private Context context;
    private AlarmManager manager;

    public AlarmScheduler(Context context) {
        this.context = context;
        manager = context.getSystemService(AlarmManager.class);
    }
    public boolean validTime(LocalDateTime dateTime){
        return dateTime.isAfter(LocalDateTime.now());
    }

    @Override
    public void schedule(AlarmItem item, LocalDateTime dateTime) {
        Intent intent = new Intent(context, AlarmReceiver.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(ALARM_ITEM_NOTIFICATION, item);
        bundle.putInt(ALARM_ITEM_NOTIFICATION_EVENT_ID, item.eventId);//para quando o serializable nao funciona

        intent.putExtras(bundle);

        manager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                dateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
                PendingIntent.getBroadcast(
                        context,
                        item.eventId,
                        intent,
                        PendingIntent.FLAG_MUTABLE
                )
        );
    }

    @Override
    public void cancel(AlarmItem item) {
        manager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.eventId,
                new Intent(context, AlarmReceiver.class),
                PendingIntent.FLAG_MUTABLE
            )
        );

    }
}
