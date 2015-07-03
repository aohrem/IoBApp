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

public class GeofencePutListener implements PutJSONClient.GetJSONListener {
    private static final String TAG = "GeofencePutListener";

    private final Activity mActivity;

    public GeofencePutListener(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onRemoteCallComplete(String jsonString) {
        Toast.makeText(mActivity, mActivity.getString(R.string.notification_was_edited),
                Toast.LENGTH_LONG).show();

        Fragment notificationsFragment = new NotificationsFragment();
        FragmentManager fragmentManager = mActivity.getFragmentManager();
        String fragmentTag = ((TagFragment) notificationsFragment).getFragmentTag();
        fragmentManager.beginTransaction().replace(R.id.content_frame,
                notificationsFragment).addToBackStack(fragmentTag).commit();
    }
}
