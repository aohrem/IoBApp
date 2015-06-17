package de.ifgi.iobapp.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import de.ifgi.iobapp.MainActivity;
import de.ifgi.iobapp.R;
import de.ifgi.iobapp.model.Notification;
import de.ifgi.iobapp.persistance.InternalStorage;


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

        mNotifications = new ArrayList<Notification>();
        try {
            mNotifications = (ArrayList<Notification>) InternalStorage.readObject(getActivity(),
                    CreateNotificationFragment.INTERNAL_STORAGE_KEY);
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
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.notification_list_item, parent, false);

            TextView nameTextView = (TextView) rowView.findViewById(R.id.notification_name);
            nameTextView.setText(notifications.get(position).getName());

            TextView textTextView = (TextView) rowView.findViewById(R.id.notification_text);
            textTextView.setText(notifications.get(position).getText());

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
                    if (deleteNotification(position)) {
                        Toast.makeText(getActivity(), getString(R.string.notification_was_deleted),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.notification_delete_error),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            return rowView;
        }

    }

    private boolean deleteNotification(int position) {
        mNotifications.remove(position);
        mNotificationListAdapter.notifyDataSetChanged();

        try {
            InternalStorage.writeObject(getActivity(),
                    CreateNotificationFragment.INTERNAL_STORAGE_KEY, mNotifications);
            return true;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    public String getFragmentTag() {
        return TAG;
    }

}