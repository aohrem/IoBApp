package de.ifgi.iobapp.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import de.ifgi.iobapp.R;
import de.ifgi.iobapp.model.Notification;


public class NotificationsFragment extends Fragment {

    private ListView mNotificationListView;

    public NotificationsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        mNotificationListView = (ListView) view.findViewById(R.id.notifcationListView);

        ArrayList<Notification> notifications = new ArrayList<Notification>();
        notifications.add(new Notification("A", "B", new LatLng(51.0, 7.0), 1.2, true));
        notifications.add(new Notification("A", "B", new LatLng(51.0, 7.0), 1.2, true));
        notifications.add(new Notification("A", "B", new LatLng(51.0, 7.0), 1.2, true));
        notifications.add(new Notification("A", "B", new LatLng(51.0, 7.0), 1.2, true));
        notifications.add(new Notification("A", "B", new LatLng(51.0, 7.0), 1.2, true));
        notifications.add(new Notification("A", "B", new LatLng(51.0, 7.0), 1.2, true));
        notifications.add(new Notification("A", "B", new LatLng(51.0, 7.0), 1.2, true));
        notifications.add(new Notification("A", "B", new LatLng(51.0, 7.0), 1.2, true));
        notifications.add(new Notification("A", "B", new LatLng(51.0, 7.0), 1.2, true));

        NotificationListAdapter notificationListAdapter =
                new NotificationListAdapter(getActivity().getApplicationContext(), notifications);

        mNotificationListView.setAdapter(notificationListAdapter);

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
        public View getView(int position, View coverView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.notification_list_item,
                    parent, false);

            TextView nameTextView = (TextView) rowView.findViewById(R.id.notificationName);
            nameTextView.setText(notifications.get(position).getName());

            TextView textTextView = (TextView) rowView.findViewById(R.id.notificationText);
            textTextView.setText(notifications.get(position).getText());

            return rowView;
        }

    }

}