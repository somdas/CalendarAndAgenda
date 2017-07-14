package com.example.calendar;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by sbandyop on 7/12/2017.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // Display Notification
        String title = arg1.getExtras().getString(Constants.KEY_TITLE);
        String time = arg1.getExtras().getString(Constants.KEY_TIME);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(arg0)
                        .setSmallIcon(R.drawable.ic_today_white_24dp)
                        .setColor(ContextCompat.getColor(arg0, R.color.notification))
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setContentText(arg0.getString(R.string.meeting) + " " + time)
                        .setTicker(title);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager) arg0
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(DataStore.notificationCount++, mBuilder.build());

    }
}
