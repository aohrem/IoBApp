package de.ifgi.iobapp.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import de.ifgi.iobapp.model.Notification;

public class GeofencePostListener implements PostJSONClient.GetJSONListener {
    private static final String PACKAGE = "de.ifgi.iobapp";
    private static final String DEVICE_ID = ".deviceid";

    private final Activity mActivity;
    private final Notification mNotification;
    private final boolean mRedirect;

    public GeofencePostListener(Activity activity, Notification notification, boolean redirect) {
        mActivity = activity;
        mNotification = notification;
        mRedirect = redirect;
    }

    @Override
    public void onRemoteCallComplete(String jsonFromNet) {
        final SharedPreferences prefs = mActivity.getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
        String deviceId = prefs.getString(PACKAGE + DEVICE_ID, "");
        IoBAPI ioBAPI = new IoBAPI(new GeofenceGetListener(mActivity, mNotification, mRedirect));
        ioBAPI.getAllGeofencesFromDevice(deviceId);
    }
}
