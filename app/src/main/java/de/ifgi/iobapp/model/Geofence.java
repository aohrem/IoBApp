package de.ifgi.iobapp.model;

public class Geofence {
    private int id;
    private String deviceId;
    private double lat;
    private double lon;
    private int radius;

    public Geofence(int id, String deviceId, double lat, double lon, int radius) {
        this.id = id;
        this.deviceId = deviceId;
        this.lat = lat;
        this.lon = lon;
        this.radius = radius;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String toString() {
        return id + "; " + deviceId + "; " + lat + "; " + lon + "; " + radius;
    }

    public boolean equals(Geofence geofence) {
        DoubleComparator dc = new DoubleComparator();
        return (deviceId == geofence.getDeviceId()) &&
                dc.equals(lat, geofence.getLat()) &&
                dc.equals(lon, geofence.getLon()) &&
                (radius == geofence.getRadius());
    }
}
