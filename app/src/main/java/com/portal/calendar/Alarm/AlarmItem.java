package com.portal.calendar.Alarm;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AlarmItem implements Serializable {
    int eventId;
    String title;
    String detail;

    String alarmSoundName;

    public AlarmItem(int eventId, String title, String detail, String alarmSoundName) {
        this.eventId = eventId;
        this.title = title;
        this.detail = detail;
        this.alarmSoundName = (alarmSoundName == "")?null:alarmSoundName;
    }
}
