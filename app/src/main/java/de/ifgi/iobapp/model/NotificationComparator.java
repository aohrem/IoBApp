package de.ifgi.iobapp.model;


import java.util.Comparator;

public class NotificationComparator implements Comparator<Notification> {
    @Override
    public int compare(Notification notification1, Notification notification2) {
        return notification1.getName().compareTo(notification2.getName());
    }
}