package com.portal.calendar.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.portal.calendar.Alarm.AlarmItem;
import com.portal.calendar.Alarm.AlarmScheduler;
import com.portal.calendar.Events.CalendarEventModel;
import com.portal.calendar.Events.CalendarEventSQL;

import java.time.LocalDateTime;
import java.util.ArrayList;


//Quando o telemovel é reeniciado é necessário voltar a marcar todos os alarmes
public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction() == Intent.ACTION_BOOT_COMPLETED){
            AlarmScheduler as = new AlarmScheduler(context);
            CalendarEventSQL sqlHelper = new CalendarEventSQL(context);

            ArrayList<CalendarEventModel> futureAlarms = sqlHelper.getFutureAlarms();
            CalendarUtils.showMsg(context, futureAlarms.size() + " alarms to add");
            Log.i("Calendar Alarm", futureAlarms.size() + " alarms to add");
            for (CalendarEventModel model : futureAlarms) {


                AlarmItem ai = new AlarmItem((int)model.id, model.name, model.detail, model.alarmSoundName);
                as.schedule(ai, model.getAlarmDateTime());
                Log.i("Calendar Alarm", "New Alarm "+ model.id +" "+ model.name +" "+ model.detail);

            }
        }
    }
}
