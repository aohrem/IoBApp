package de.ifgi.iobapp.model;

import com.google.android.gms.maps.model.LatLng;

public class Notification {
    private String name;
    private String text;
    private LatLng regionCenter;
    private double regionRadius;
    private boolean enters;

    public Notification(String name, String text, LatLng regionCenter, double regionRadius, boolean enters) {
        this.name = name;
        this.text = text;
        this.regionCenter = regionCenter;
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

    public LatLng getRegionCenter() {
        return regionCenter;
    }

    public void setRegionCenter(LatLng regionCenter) {
        this.regionCenter = regionCenter;
    }

    public double getRegionRadius() {
        return regionRadius;
    }

    public void setRegionRadius(double regionRadius) {
        this.regionRadius = regionRadius;
    }

    public boolean isEnters() {
        return enters;
    }

    public void setEnters(boolean enters) {
        this.enters = enters;
    }
}