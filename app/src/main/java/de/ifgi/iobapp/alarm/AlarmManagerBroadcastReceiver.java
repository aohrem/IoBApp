package de.ifgi.iobapp.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import de.ifgi.iobapp.MainActivity;
import de.ifgi.iobapp.R;
import de.ifgi.iobapp.api.GetJSONClient;
import de.ifgi.iobapp.api.IoBAPI;
import de.ifgi.iobapp.api.MessageGetListener;
import de.ifgi.iobapp.api.PostJSONClient;
import de.ifgi.iobapp.model.Notification;
import de.ifgi.iobapp.model.NotificationComparator;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmManagerBroadcast";
    private static final String PACKAGE = "de.ifgi.iobapp";
    private static final String DEVICE_ID = ".deviceid";
    private static final int INTERVAL_SECONDS = 60;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        wl.acquire();

        final SharedPreferences prefs = context.getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
        String deviceId = prefs.getString(PACKAGE + DEVICE_ID, "");
        IoBAPI ioBAPI = new IoBAPI(new MessageGetListener(context));
        ioBAPI.getAllMessagesFromDevice(deviceId);

        wl.release();
    }

    public void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * INTERVAL_SECONDS, pi);
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}