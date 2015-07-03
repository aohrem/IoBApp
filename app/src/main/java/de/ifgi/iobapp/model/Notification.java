package de.ifgi.iobapp.model;

import java.io.Serializable;

public class Notification implements Serializable {
    public static final long serialVersionUID = -5405877562922217922L;

    private String name;
    private String text;
    private double regionCenterLat;
    private double regionCenterLon;
    private int regionRadius;
    private boolean enters;
    private int geofenceId;

    public Notification(String name, String text, double regionCenterLat, double regionCenterLon, int regionRadius, boolean enters) {
        this.name = name;
        this.text = text;
        this.regionCenterLat = regionCenterLat;
        this.regionCenterLon = regionCenterLon;
        this.regionRadius = regionRadius;
        this.enters = enters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getRegionCenterLat() {
        return regionCenterLat;
    }

    public void setRegionCenterLat(double lat) {
        this.regionCenterLat = lat;
    }

    public double getRegionCenterLon() {
        return regionCenterLon;
    }

    public void setRegionCenterLon(double lon) {
        this.regionCenterLon = lon;
    }

    public int getRegionRadius() {
        return regionRadius;
    }

    public void setRegionRadius(int regionRadius) {
        this.regionRadius = regionRadius;
    }

    public boolean isEnters() {
        return enters;
    }

    public void setEnters(boolean enters) {
        this.enters = enters;
    }

    public int getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(int geofenceId) {
        this.geofenceId = geofenceId;
    }

    public String toString() {
        return geofenceId + "; " + name + "; " + text + "; " + regionCenterLat + "; " + regionCenterLon + "; " + regionRadius + "; " + enters;
    }

    public boolean equals(Notification notification) {
        DoubleComparator dc = new DoubleComparator();
        return (notification.getGeofenceId() == getGeofenceId())
                && (notification.getName().equals(getName()))
                && (notification.getText().equals(getText()))
                && dc.equals(notification.getRegionCenterLat(), getRegionCenterLat())
                && dc.equals(notification.getRegionCenterLon(), getRegionCenterLon())
                && (notification.getRegionRadius() == getRegionRadius())
                && (notification.isEnters() == isEnters());
    }
}