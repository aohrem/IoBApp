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

    public String toString() {
        return name + "; " + text + "; " + regionCenterLat + "; " + regionCenterLon + "; " + regionRadius + ", " + enters;
    }
}