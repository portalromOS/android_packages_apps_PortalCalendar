package com.portal.calendar.Alarm;

import java.time.LocalDateTime;

public interface AlarmSchedulerInterface {
    void schedule(AlarmItem item, LocalDateTime dateTime);
    void cancel(AlarmItem item);
}
