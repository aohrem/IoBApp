package de.ifgi.iobapp.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import de.ifgi.iobapp.MainActivity;
import de.ifgi.iobapp.R;
import de.ifgi.iobapp.alarm.AlarmManagerBroadcastReceiver;
import de.ifgi.iobapp.api.DeleteListener;
import de.ifgi.iobapp.api.IoBAPI;
import de.ifgi.iobapp.model.Notification;
import de.ifgi.iobapp.model.NotificationComparator;
import de.ifgi.iobapp.persistance.NotificationManager;


public class NotificationsFragment extends Fragment implements TagFragment {

    private static final String TAG = "Notifications";
    private ListView mNotificationListView;
    private NotificationListAdapter mNotificationListAdapter;
    private ArrayList<Notification> mNotifications;

    public NotificationsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        mNotificationListView = (ListView) view.findViewById(R.id.notifcation_list_view);

        NotificationManager notificationManager = new NotificationManager(getActivity());
        mNotifications = new ArrayList<Notification>();
        try {
            mNotifications = notificationManager.readNotifications();
            Collections.sort(mNotifications, new NotificationComparator());
        } catch (IOException e) {
            Log.e(TAG, "" + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "" + e.getMessage());
        }

        mNotificationListAdapter =
                new NotificationListAdapter(getActivity().getApplicationContext(), mNotifications);
        mNotificationListView.setAdapter(mNotificationListAdapter);

        Button createNotificationButton =
                (Button) view.findViewById(R.id.create_notification_button);
        createNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment createNotificationFragment =
                        CreateNotificationFragment.newInstance(false, 0);
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                String fragmentTag = ((TagFragment) createNotificationFragment).getFragmentTag();
                fragmentManager.beginTransaction().replace(R.id.content_frame,
                        createNotificationFragment).addToBackStack(fragmentTag).commit();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity mainActivity = ((MainActivity) getActivity());
        if (!mainActivity.mHeaderClosed) {
            mainActivity.mHeaderClosed = true;
            mainActivity.animateHeader();
        }
    }

    private class NotificationListAdapter extends ArrayAdapter<Notification> {
        Context context;
        private ArrayList<Notification> notifications = new ArrayList<Notification>();

        public NotificationListAdapter(Context context, ArrayList<Notification> notifications) {
            super(context, R.layout.notification_list_item, notifications);
            this.context = context;
            this.notifications = notifications;
        }

        @Override
        public View getView(final int position, View coverView, ViewGroup parent) {
            final Notification notification = notifications.get(position);
            if (notification.getText().equals(getString(R.string.theft_protection_key))) {
                return new View(getActivity());
            }
            else {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.notification_list_item, parent, false);

                TextView nameTextView = (TextView) rowView.findViewById(R.id.notification_name);
                nameTextView.setText(notification.getName());

                TextView textTextView = (TextView) rowView.findViewById(R.id.notification_text);
                textTextView.setText(notification.getText());

                ImageButton editButton = (ImageButton) rowView.findViewById(R.id.edit_button);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment createNotificationFragment =
                                CreateNotificationFragment.newInstance(true, position);
                        FragmentManager fragmentManager = getActivity().getFragmentManager();
                        String fragmentTag = ((TagFragment) createNotificationFragment).getFragmentTag();
                        fragmentManager.beginTransaction().replace(R.id.content_frame,
                                createNotificationFragment).addToBackStack(fragmentTag).commit();
                    }
                });

                ImageButton deleteButton = (ImageButton) rowView.findViewById(R.id.delete_button);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setMessage(getResources().getString(R.string.delete_really));

                        dialog.setPositiveButton(getResources().getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                        if (deleteNotification(notification)) {
                                            Toast.makeText(getActivity(), getString(R.string.notification_was_deleted),
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getActivity(), getString(R.string.notification_delete_error),
                                                    Toast.LENGTH_LONG).show();
                                        }
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
                });

                return rowView;
            }
        }

    }

    private boolean deleteNotification(Notification notification) {
        mNotifications.remove(notification);
        mNotificationListAdapter.notifyDataSetChanged();

        NotificationManager notificationManager = new NotificationManager(getActivity());
        try {
            ArrayList<Notification> notifications = notificationManager.readNotifications();
            Notification deleteNotification = null;
            for (Notification lNotification : notifications) {
                if (lNotification.equals(notification)) {
                    deleteNotification = lNotification;
                }
            }
            if (deleteNotification != null) {
                notifications.remove(deleteNotification);
            }
            if (notifications.size() == 0) {
                AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();
                alarm.cancelAlarm(getActivity().getApplicationContext());
            }
            notificationManager.writeNotifications(notifications);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "" + e.getMessage());
            return false;
        }

        int geofenceId = notification.getGeofenceId();
        IoBAPI ioBAPI = new IoBAPI(new DeleteListener());
        ioBAPI.deleteGeofence(geofenceId);

        return true;
    }

    public String getFragmentTag() {
        return TAG;
    }

}