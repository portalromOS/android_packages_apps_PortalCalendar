package com.portal.calendar.Alarm;


import static com.portal.calendar.EventEditActivity.CALENDAR_EVENT_BUNDLE_EVENT_ID;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.portal.calendar.CalendarNotification.NotificationHelper;
import com.portal.calendar.EventEditActivity;
import com.portal.calendar.Events.CalendarEventModel;
import com.portal.calendar.Events.CalendarEventSQL;
import com.portal.calendar.R;

public class AlarmReceiver extends BroadcastReceiver {
    private Context context;
    public static String ALARM_ITEM_NOTIFICATION = "AlarmEvent";

    //Recebo o evento do sheduler qd este é disparado no dia e hora destinado
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        AlarmItem alarmItem = getAlarm(intent);

        if(alarmItem != null){

            NotificationHelper notificationHelper = new NotificationHelper(context, alarmItem.alarmSoundName);

            NotificationCompat.Builder builder = notificationHelper.getChannelNotification(
                getEventViewIntent(alarmItem.eventId),
                alarmItem.title,
                alarmItem.detail,
                R.drawable.ic_launcher,
                alarmItem.alarmSoundName
            );
            notificationHelper.getManager().notify(alarmItem.eventId, builder.build());
        }
    }

    private PendingIntent getEventViewIntent(int eventId){
        PendingIntent result = null;

        Intent resultIntent = new Intent(context, EventEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(CALENDAR_EVENT_BUNDLE_EVENT_ID, eventId);
        resultIntent.putExtras(bundle);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        result = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return result;
    }

    private AlarmItem getAlarm(Intent intent){
        AlarmItem alarmItem = null;

        if(intent != null){
            Bundle b = intent.getExtras();

            if(b != null){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    alarmItem =  b.getSerializable(ALARM_ITEM_NOTIFICATION, AlarmItem.class) ;
                }else{
                    alarmItem =  (AlarmItem) b.getSerializable(ALARM_ITEM_NOTIFICATION) ;
                }
            }
        }
        return alarmItem;
    }

}
