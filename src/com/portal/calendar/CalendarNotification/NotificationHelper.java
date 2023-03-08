package com.portal.calendar.CalendarNotification;

import static androidx.core.app.NotificationCompat.CATEGORY_EVENT;
import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;
import static androidx.core.app.NotificationCompat.PRIORITY_LOW;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.portal.calendar.R;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class NotificationHelper extends ContextWrapper {
    private static final String NOTIFICATION_CHANNEL_DEFAULT_ID = "Portal_Calendar_ID";
    private static final String NOTIFICATION_CHANNEL_DEFAULT_NAME = "Portal Calendar";

    private NotificationManager nManager;

    public NotificationHelper(Context context, String soundName) {
        super(context);
        createChannels(soundName);
    }

    public String getChannelId(String soundName){
        if(soundName == null)
            soundName = "";

        String result = NOTIFICATION_CHANNEL_DEFAULT_ID;
        if(!soundName.equals("")){
            result += "-"+soundName;
        }
        return result;
    }
    public String getChannelName(String soundName){
        if(soundName == null)
            soundName = "";

        String result = NOTIFICATION_CHANNEL_DEFAULT_NAME;
        if(!soundName.equals("")){
            result += " - "+soundName;
        }
        return result;
    }
    public void createChannels(String soundName) {
        if(soundName == null)
            soundName = "";
//Needed for versions bigger than 26
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int channelImportance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel= new NotificationChannel(
                    getChannelId(soundName),
                    getChannelName(soundName),
                    channelImportance
            );


            if(soundName.equals("") ){
                channel.setSound(null, null);
            }
            else{
                Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getPackageName() + "/raw/"+soundName);
                //Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getPackageName() + "/"+R.raw.rooster_crowing_in_the_morning);
                //Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/"+soundName);

                AudioAttributes soundAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                channel.setSound(uri,soundAttributes);
            }


            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.setLightColor(R.color.highLight);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager().createNotificationChannel(channel);
        }
    }



    public NotificationManager getManager(){
        if(nManager == null)
            nManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return nManager;
    }

    public NotificationCompat.Builder getChannelNotification(PendingIntent resultPendingIntent,String title, String detail, int icon, String soundName){
        if(soundName == null)
            soundName = "";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getChannelId(soundName));
        builder.setContentTitle(title);
        builder.setContentText(detail);
        builder.setSmallIcon(icon);

        builder.setCategory(CATEGORY_EVENT);
        builder.setAutoCancel(true);
        builder.setPriority(PRIORITY_HIGH);

        if(soundName.equals("")){
            builder.setSilent(true);
            builder.setSound(null);
        }
        else{
            builder.setSilent(false);
            Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/raw/"+soundName);
            builder.setSound(uri);
        }

        builder.setContentIntent(resultPendingIntent);
        return builder;
    }

    public void deleteAllChannels(){
        NotificationManager nm = getManager();
        if (nm != null) {
            List<NotificationChannel> channelList = nm.getNotificationChannels();

            for (int i = 0; channelList != null && i < channelList.size(); i++) {
                nm.deleteNotificationChannel(channelList.get(i).getId());
            }
            Log.i("PORTAL", "All notificationChannels deleted");
        }
    }
}
