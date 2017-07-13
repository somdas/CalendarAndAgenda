package com.example.calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by sbandyop on 7/12/2017.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        DataStore store = DataStore.getInstance(arg0);
        store.open();
        List<NotificationData> notificationDataList = store.getNotificationData();
        store.close();
        Calendar current = Calendar.getInstance(TimeZone.getDefault());
        Calendar start;
        for (NotificationData data : notificationDataList) {
            if (data.reminderTime != -1) {
                start = new GregorianCalendar(data.startYear, data.startMonth, data.startDay, data.startHour, data.startMinute);
                start.add(Calendar.MINUTE, -data.reminderTime);
                if (start.after(current)) {
                    Intent alarmIntent = new Intent(arg0,
                            AlarmReceiver.class);
                    alarmIntent.putExtra(Constants.KEY_TITLE, data.title);
                    alarmIntent.putExtra(Constants.KEY_TIME, data.time);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            arg0, data.eventID,
                            alarmIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) arg0.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,
                            start.getTimeInMillis(),
                            pendingIntent);
                }
            }
        }
    }
}
