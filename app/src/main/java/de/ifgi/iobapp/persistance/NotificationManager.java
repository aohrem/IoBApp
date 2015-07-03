package de.ifgi.iobapp.persistance;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import de.ifgi.iobapp.model.Notification;

public class NotificationManager {
    public static final String INTERNAL_STORAGE_KEY = "notifications";
    private static final String TAG = "NotificationManager";

    private Activity mActivity;

    public NotificationManager(Activity activity) {
        mActivity = activity;
    }

    public ArrayList<Notification> readNotifications() throws IOException, ClassNotFoundException {
        ArrayList<Notification> notifications = (ArrayList<Notification>)
                InternalStorage.readObject(mActivity, INTERNAL_STORAGE_KEY);
        Log.d(TAG, "Stored Notifications: " + notifications.toString());
        return notifications;
    }

    public void writeNotifications(ArrayList<Notification> notifications) throws IOException {
        InternalStorage.writeObject(mActivity, INTERNAL_STORAGE_KEY, notifications);
        Log.d(TAG, "Stored Notifications: " + notifications.toString());
    }
}