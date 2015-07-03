package de.ifgi.iobapp.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.ifgi.iobapp.model.Geofence;
import de.ifgi.iobapp.model.Message;

public class GeofenceJSONParser {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public GeofenceJSONParser() {

    }

    public ArrayList<Geofence> parseGeofences(JSONArray jsonResult) throws JSONException, ParseException {
        ArrayList<Geofence> geofences = new ArrayList<Geofence>();

        for (int i = 0; i < jsonResult.length(); i++) {
            JSONObject jsonObject = jsonResult.getJSONObject(i);

            int id = jsonObject.getInt("geofence_id");
            String deviceId = jsonObject.getString("device_id");
            String latString = jsonObject.getString("lat");
            String lonString = jsonObject.getString("lon");
            String radiusString = jsonObject.getString("radius");

            if (latString.equals("null")
                    || lonString.equals("null")
                    || radiusString.equals("null")) {
                continue;
            }

            double lat = jsonObject.getDouble("lat");
            double lon = jsonObject.getDouble("lon");
            int radius = jsonObject.getInt("radius");

            Geofence geofence = new Geofence(id, deviceId, lat, lon, radius);
            geofences.add(geofence);
        }

        return geofences;
    }
}
