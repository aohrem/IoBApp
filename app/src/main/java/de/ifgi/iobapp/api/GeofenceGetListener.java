package de.ifgi.iobapp.api;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import de.ifgi.iobapp.R;
import de.ifgi.iobapp.fragments.NotificationsFragment;
import de.ifgi.iobapp.fragments.TagFragment;
import de.ifgi.iobapp.model.DoubleComparator;
import de.ifgi.iobapp.model.Geofence;
import de.ifgi.iobapp.model.Notification;
import de.ifgi.iobapp.persistance.NotificationManager;

public class GeofenceGetListener implements GetJSONClient.GetJSONListener {
    private static final String TAG = "GeofenceGetListener";

    private final Activity mActivity;
    private final Notification mNotification;
    private final boolean mRedirect;

    public GeofenceGetListener(Activity activity, Notification notification, boolean redirect) {
        mActivity = activity;
        mNotification = notification;
        mRedirect = redirect;
    }

    @Override
    public void onRemoteCallComplete(String jsonString) {
        if (jsonString != null) {
            JSONArray jsonResult = null;
            try {
                jsonResult = (JSONArray) new JSONTokener(jsonString).nextValue();
                Log.d(TAG, jsonResult.toString());

                if (jsonResult != null) {
                    GeofenceJSONParser jsonParser = new GeofenceJSONParser();
                    ArrayList<Geofence> geofences = jsonParser.parseGeofences(jsonResult);
                    Log.d(TAG, geofences.toString());

                    int geofenceId = getGeofenceId(geofences, mNotification);

                    NotificationManager notificationManager = new NotificationManager(mActivity);
                    ArrayList<Notification> notifications = notificationManager.readNotifications();
                    for (Notification notification : notifications) {
                        if (notification.equals(mNotification)) {
                            notification.setGeofenceId(geofenceId);
                        }
                    }
                    notificationManager.writeNotifications(notifications);
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage() + "");
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage() + "");
            } catch (ClassCastException e) {
                Log.e(TAG, e.getMessage() + " " + mActivity.getString(R.string.error_invalid_json_array));
            } catch (ClassNotFoundException e) {
                Log.e(TAG, e.getMessage() + "");
            } catch (IOException e) {
                Log.e(TAG, e.getMessage() + "");
            }

            if (mRedirect) {
                Toast.makeText(mActivity, mActivity.getString(R.string.notification_was_created),
                        Toast.LENGTH_LONG).show();

                Fragment notificationsFragment = new NotificationsFragment();
                FragmentManager fragmentManager = mActivity.getFragmentManager();
                String fragmentTag = ((TagFragment) notificationsFragment).getFragmentTag();
                fragmentManager.beginTransaction().replace(R.id.content_frame,
                        notificationsFragment).addToBackStack(fragmentTag).commit();
            }
        }
        else {
            Log.e(TAG, mActivity.getString(R.string.error_http_request));
        }
    }

    private int getGeofenceId(ArrayList<Geofence> geofences, Notification notification) {
        DoubleComparator dc = new DoubleComparator();
        for (Geofence geofence : geofences) {
            if (    dc.equals(geofence.getLon(), notification.getRegionCenterLon()) &&
                    dc.equals(geofence.getLat(), notification.getRegionCenterLat()) &&
                    (geofence.getRadius() == notification.getRegionRadius())) {
                return geofence.getId();
            }
        }
        return 0;
    }
}
