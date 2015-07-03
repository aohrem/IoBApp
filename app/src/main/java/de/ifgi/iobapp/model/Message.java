package de.ifgi.iobapp.model;

import java.util.Date;

public class Message {
    private int id;
    private String deviceId;
    private double lat;
    private double lon;
    private Date timestamp;

    public Message(int id, String deviceId, double lat, double lon, Date timestamp) {
        this.id = id;
        this.deviceId = deviceId;
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        return getId() + "; " + getDeviceId() + "; " + getLat() + "; " + getLon() + "; " + getTimestamp();
    }
}
