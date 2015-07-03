package de.ifgi.iobapp.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import de.ifgi.iobapp.R;
import de.ifgi.iobapp.api.DeleteListener;
import de.ifgi.iobapp.api.IoBAPI;
import de.ifgi.iobapp.api.GetJSONClient;
import de.ifgi.iobapp.api.MessageJSONParser;
import de.ifgi.iobapp.api.GeofencePostListener;
import de.ifgi.iobapp.bluetooth.BluetoothConnection;
import de.ifgi.iobapp.model.Geofence;
import de.ifgi.iobapp.model.Message;
import de.ifgi.iobapp.model.MessageComparator;
import de.ifgi.iobapp.model.Notification;
import de.ifgi.iobapp.persistance.NotificationManager;


public class TheftProtectionFragment extends Fragment implements TagFragment,
        GetJSONClient.GetJSONListener {

    private static final String TAG = "TheftProtection";
    private static final String PACKAGE = "de.ifgi.iobapp";
    private static final String DEVICE_ID = ".deviceid";
    private static final String THEFT_PROTECTION_STATUS = ".theftprotectionstatus";
    private static final float ACTIVE_LAYOUT_ALPHA = 1.0f;
    private static final float INACTIVE_LAYOUT_ALPHA = 0.3f;
    private static final int THEFT_PROTECTION_RADIUS = 50;

    private boolean mCurrentStatusLocked;

    private BluetoothConnection mBlue;
    private TextView mBluetoothMessageTextView;
    private FrameLayout mCurrentStatusFrame;
    private boolean mUIActive;
    private LatLng mBicycleLocation;

    public TheftProtectionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theft_protection, container, false);

        final SharedPreferences prefs = getActivity().getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
        mCurrentStatusLocked = prefs.getBoolean(PACKAGE + THEFT_PROTECTION_STATUS, false);

        mBluetoothMessageTextView = (TextView) view.findViewById(R.id.bluetooth_mesage_text_view);
        mCurrentStatusFrame = (FrameLayout) view.findViewById(R.id.current_status_frame);
        changeUIStatus(false);

        final FrameLayout startScanFrame = (FrameLayout) view.findViewById(R.id.start_scan_frame);

        Button startScanButton = (Button) view.findViewById(R.id.start_scan_button);
        startScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScanFrame.setVisibility(View.GONE);
                restartFragment();
            }
        });

        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            showMessage(getString(R.string.device_doesnt_support_bluetooth));
            mBluetoothMessageTextView.setTextColor(getResources().getColor(R.color.iob_app_red));
            mCurrentStatusFrame.setVisibility(View.GONE);
            return view;
        }
        else if (!mBluetoothAdapter.isEnabled()) {
            startScanFrame.setVisibility(View.VISIBLE);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage(getResources().getString(R.string.please_enable_bluetooth));

            dialog.setPositiveButton(getResources().getString(R.string.open_bluetooth_setting),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent myIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                            startActivity(myIntent);
                        }
                    });

            dialog.setNegativeButton(getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        }
                    });

            dialog.show();
        }
        else if (mBluetoothAdapter.isEnabled()) {
            mBlue = new BluetoothConnection(getActivity().getApplicationContext(), this);
        }

        final TextView currentStatusTextView = (TextView) view.findViewById(R.id.current_status_text_view);
        final ImageView currentStatusImageView = (ImageView) view.findViewById(R.id.current_status_image);
        if (mCurrentStatusLocked) {
            currentStatusTextView.setText(getString(R.string.locked));
            currentStatusImageView.setImageResource(R.mipmap.theft_protection);
        }
        else {
            currentStatusTextView.setText(getString(R.string.unlocked));
            currentStatusImageView.setImageResource(R.mipmap.theft_protection_unlocked);
        }

        LinearLayout currentStatusLayout = (LinearLayout) view.findViewById(R.id.current_status_layout);
        currentStatusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBlue != null && mUIActive) {
                    String message; boolean locked; int statusText; int imageResource;
                    int whiteImageResource;

                    if (mCurrentStatusLocked) {
                        message = "0";
                        locked = false;
                        statusText = R.string.unlocked;
                        imageResource = R.mipmap.theft_protection_unlocked;
                        whiteImageResource = R.mipmap.theft_protection_white_unlocked;
                        deleteNotification();
                    } else {
                        message = "1";
                        locked = true;
                        statusText = R.string.locked;
                        imageResource = R.mipmap.theft_protection;
                        whiteImageResource = R.drawable.theft_protection_white;
                        updateBicycleLocation();
                    }

                    switchCurrentStatus(locked, statusText, imageResource, whiteImageResource);
                    sendMessage(message);
                }
            }

            private void switchCurrentStatus(boolean locked, int statusText,
                                             int imageResource, int whiteImageResource) {
                mCurrentStatusLocked = locked;
                prefs.edit().putBoolean(PACKAGE + THEFT_PROTECTION_STATUS, locked).apply();
                currentStatusTextView.setText(getString(statusText));
                currentStatusImageView.setImageResource(imageResource);

                TextView theftProtectionTextView = (TextView)
                        getActivity().findViewById(R.id.theft_protection_text_view);
                theftProtectionTextView.setText(getString(statusText));
                ImageView theftProtectionImage = (ImageView)
                        getActivity().findViewById(R.id.theft_protection_image);
                theftProtectionImage.setImageResource(whiteImageResource);
            }
        });

        return view;
    }

    private void restartFragment() {
        Fragment theftProtectionFragment = new TheftProtectionFragment();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,
                theftProtectionFragment).commit();
    }

    public void showMessage(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                mBluetoothMessageTextView.setText(message);
            }
        });
    }

    public void changeUIStatus(final boolean activate) {
        mUIActive = activate;

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (activate) {
                    mCurrentStatusFrame.setAlpha(ACTIVE_LAYOUT_ALPHA);
                } else {
                    mCurrentStatusFrame.setAlpha(INACTIVE_LAYOUT_ALPHA);
                }
            }
        });
    }

    private void sendMessage(String message) {
        final SharedPreferences prefs = getActivity()
                .getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
        String deviceId = prefs.getString(PACKAGE + DEVICE_ID, "");
        mBlue.sendClick(deviceId + message);
    }

    private void deleteNotification() {
        NotificationManager notificationManager = new NotificationManager(getActivity());
        try {
            ArrayList<Notification> notifications = notificationManager.readNotifications();
            for (Notification notification : notifications) {
                if (notification.getText().equals(getString(R.string.theft_protection_key))) {
                    notifications.remove(notification);

                    int geofenceId = notification.getGeofenceId();
                    IoBAPI ioBAPI = new IoBAPI(new DeleteListener());
                    ioBAPI.deleteGeofence(geofenceId);
                }
            }
            notificationManager.writeNotifications(notifications);
        } catch (IOException e) {
            Log.e(TAG, "" + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "" + e.getMessage());
        }
    }

    private void createNotification() {
        Notification notification = new Notification(getString(R.string.theft_protection),
                getString(R.string.theft_protection_key),
                mBicycleLocation.latitude,
                mBicycleLocation.longitude,
                THEFT_PROTECTION_RADIUS,
                false);

        NotificationManager notificationManager = new NotificationManager(getActivity());
        try {
            ArrayList<Notification> notifications = notificationManager.readNotifications();
            notifications.add(notification);
            notificationManager.writeNotifications(notifications);
        } catch (FileNotFoundException f) {
            ArrayList<Notification> notifications = new ArrayList<Notification>();
            try {
                notificationManager.writeNotifications(notifications);
                createNotification();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        } catch (IOException e) {
            Log.e(TAG, "" + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "" + e.getMessage());
        }

        final SharedPreferences prefs = getActivity().getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
        String deviceId = prefs.getString(PACKAGE + DEVICE_ID, "");
        Geofence geofence = new Geofence(0, deviceId, notification.getRegionCenterLat(),
                notification.getRegionCenterLon(), notification.getRegionRadius());
        IoBAPI ioBAPI = new IoBAPI(new GeofencePostListener(getActivity(), notification, false));
        ioBAPI.addGeofence(geofence);
    }

    private void updateBicycleLocation() {
        final SharedPreferences prefs = getActivity().getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
        String deviceId = prefs.getString(PACKAGE + DEVICE_ID, "");
        IoBAPI ioBAPI = new IoBAPI(this);
        ioBAPI.getAllMessagesFromDevice(deviceId);
    }

    @Override
    public void onRemoteCallComplete(String jsonString) {
        if (jsonString != null) {
            JSONArray jsonResult = null;
            try {
                jsonResult = (JSONArray) new JSONTokener(jsonString).nextValue();
                Log.d(TAG, jsonResult.toString());

                if (jsonResult != null) {
                    MessageJSONParser messageJsonParser = new MessageJSONParser();
                    ArrayList<Message> messages = messageJsonParser.parseMessages(jsonResult);

                    Log.d(TAG, messages.toString());

                    mBicycleLocation = getLastKnownLocation(messages);
                    createNotification();
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage() + "");
                Toast.makeText(getActivity(), getString(R.string.error_loading_bike_location),
                        Toast.LENGTH_LONG).show();
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage() + "");
                Toast.makeText(getActivity(), getString(R.string.error_loading_bike_location),
                        Toast.LENGTH_LONG).show();
            } catch (ClassCastException e) {
                Log.e(TAG, e.getMessage() + " (Invalid JSON Array)");
                Toast.makeText(getActivity(), getString(R.string.error_loading_bike_location),
                        Toast.LENGTH_LONG).show();
            }
        }
        else {
            Log.e(TAG, getString(R.string.error_http_request));
            Toast.makeText(getActivity(), getString(R.string.error_loading_bike_location),
                    Toast.LENGTH_LONG).show();
        }
    }

    private LatLng getLastKnownLocation(ArrayList<Message> messages) {
        Collections.sort(messages, new MessageComparator());
        int lastIndex = messages.size() - 1;
        Message lastMessage = messages.get(lastIndex);

        return new LatLng(lastMessage.getLat(), lastMessage.getLon());
    }

    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public void onStop() {
        if (mBlue != null) {
            mBlue.stop();
        }
        super.onStop();
    }

}
