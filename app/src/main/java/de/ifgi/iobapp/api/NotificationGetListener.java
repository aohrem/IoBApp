package de.ifgi.iobapp.api;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import de.ifgi.iobapp.MainActivity;
import de.ifgi.iobapp.R;
import de.ifgi.iobapp.model.Notification;

public class NotificationGetListener implements GetJSONClient.GetJSONListener {
    private static final String TAG = "NotificationGetListener";

    private final Context mContext;
    private final Notification mNotification;

    public NotificationGetListener(Notification notification, Context context) {
        mNotification = notification;
        mContext = context;
    }

    @Override
    public void onRemoteCallComplete(String jsonString) {
        if (jsonString != null) {
            JSONObject jsonResult = null;
            try {
                jsonResult = (JSONObject) new JSONTokener(jsonString).nextValue();
                Log.d(TAG, jsonResult.toString());

                if (jsonResult != null) {
                    boolean pointInPolygon = jsonResult.getBoolean("pointInPolygon");
                    if (mNotification.isEnters() != pointInPolygon)  {
                        createAndroidNotification(mContext, mNotification.getName(),
                                mNotification.getText());
                    }
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage() + "");
            } catch (ClassCastException e) {
                Log.e(TAG, e.getMessage() + "");
            }
        }
    }

    private void createAndroidNotification(Context context, String title, String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.iob_app_icon_notification)
                        .setColor(context.getResources().getColor(R.color.iob_app_red))
                        .setContentTitle(title)
                        .setContentText(text);
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = 001;
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
