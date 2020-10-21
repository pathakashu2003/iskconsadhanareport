package com.technicalround.iskconsadhanareport;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by ggne0439 on 9/14/2016.
 */
public class SetAlarms {


    public static void setReminderBeforeSleep(Context context) {

        /* Retrieve a PendingIntent that will perform a broadcast */
        PendingIntent pendingIntent;
        Intent alarmIntent = new Intent(context, ReceiverReminderNight.class);
        pendingIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int intervalMs = 1000 * 60 * 60 * 24;//Repeat every 24 hr

        /* Set the alarm to start at 8 PM */
        Calendar calendar = Calendar.getInstance();
        Calendar calendarNow = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        //Set to next date if today's alarm time has passed
        if (calendar.compareTo(calendarNow) <= 0)
            calendar.add(Calendar.DATE, 1);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                intervalMs, pendingIntent);

        Log.d("ISS", "SetAlarms:setReminderBeforeSleep");
    }

    public static void setReminderFrequently(Context context, int intervalMin) {

        /* Retrieve a PendingIntent that will perform a broadcast */
        PendingIntent pendingIntent;
        Intent alarmIntent = new Intent(context, ReceiverAlarmFrequent.class);
        pendingIntent = PendingIntent.getBroadcast(context, 2, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int intervalMs = 1000 * 60 * intervalMin;

        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                intervalMs, pendingIntent);

        Log.d("ISS", "SetAlarms:setReminderFrequent");
    }
}
