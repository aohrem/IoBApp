package de.ifgi.iobapp.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class LocationTime {
    private LatLng location;
    private Date time;

    public LocationTime(LatLng location, Date time) {
        this.location = location;
        this.time = time;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
