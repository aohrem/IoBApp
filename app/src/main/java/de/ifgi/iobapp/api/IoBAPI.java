package de.ifgi.iobapp.api;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import de.ifgi.iobapp.model.Geofence;

public class IoBAPI {
    private static final String TAG = "IoBAPI";


    private static final String API_URL = "http://giv-iob.uni-muenster.de/node/api";
    private static final String MESSAGES = "messages";
    private static final String DEVICE = "device";
    private static final String GEOFENCES = "geofences";

    private static final String GEOFENCE_DEVICE_ID = "device_id";
    private static final String GEOFENCE_LON = "lon";
    private static final String GEOFENCE_LAT = "lat";
    private static final String GEOFENCE_RADIUS = "radius";

    /*private static final String DEVICE_ID = "15E2C";

    private static final String API_URL = "http://192.168.56.1";
    private static final String MESSAGES_PARAMETER = "iob";
    private static final String DEVICE_ID = "messages.json";*/

    private GetJSONClient.GetJSONListener mGetListener;
    private PostJSONClient.GetJSONListener mPostListener;
    private DeleteJSONClient.GetJSONListener mDeleteListener;
    private PutJSONClient.GetJSONListener mPutListener;

    public IoBAPI(GetJSONClient.GetJSONListener listener) {
        mGetListener = listener;
    }

    public IoBAPI(PostJSONClient.GetJSONListener listener) {
        mPostListener = listener;
    }

    public IoBAPI(DeleteJSONClient.GetJSONListener listener) {
        mDeleteListener = listener;
    }

    public IoBAPI(PutJSONClient.GetJSONListener listener) {
        mPutListener = listener;
    }

    private void getJSON(String parameter1, String parameter2, String parameter3) {
        String url = API_URL + "/" + parameter1 + "/" + parameter2 + "/" + parameter3;

        try {
            GetJSONClient getJsonClient = new GetJSONClient(mGetListener);
            getJsonClient.execute(url);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + "");
        }
    }

    private void postJSON(String parameter, ArrayList<NameValuePair> nameValuePairs) {
        String url = API_URL + "/" + parameter;

        try {
            PostJSONClient postJsonClient = new PostJSONClient(mPostListener, nameValuePairs);
            postJsonClient.execute(url);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + "");
        }
    }

    private void deleteJSON(String parameter1, String parameter2) {
        String url = API_URL + "/" + parameter1 + "/" + parameter2;

        try {
            DeleteJSONClient deleteJSONClient = new DeleteJSONClient(mDeleteListener);
            deleteJSONClient.execute(url);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + "");
        }
    }

    private void putJSON(String parameter1, String parameter2,
                         ArrayList<NameValuePair> nameValuePairs) {
        String url = API_URL + "/" + parameter1 + "/" + parameter2;

        try {
            PutJSONClient putJSONClient = new PutJSONClient(mPutListener, nameValuePairs);
            putJSONClient.execute(url);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + "");
        }
    }


    public void getAllMessagesFromDevice(String deviceId) {
        getJSON(MESSAGES, DEVICE, deviceId);
    }

    public void getAllGeofencesFromDevice(String deviceId) {
        getJSON(GEOFENCES, DEVICE, deviceId);
    }

    public void addGeofence(Geofence geofence) {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(GEOFENCE_DEVICE_ID, geofence.getDeviceId()));
        nameValuePairs.add(new BasicNameValuePair(GEOFENCE_LON, geofence.getLon() + ""));
        nameValuePairs.add(new BasicNameValuePair(GEOFENCE_LAT, geofence.getLat() + ""));
        nameValuePairs.add(new BasicNameValuePair(GEOFENCE_RADIUS, geofence.getRadius() + ""));
        postJSON(GEOFENCES, nameValuePairs);
    }

    public void deleteGeofence(int geofenceId) {
        deleteJSON(GEOFENCES, geofenceId + "");
    }

    public void editGeofence(Geofence geofence) {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(GEOFENCE_LON, geofence.getLon() + ""));
        nameValuePairs.add(new BasicNameValuePair(GEOFENCE_LAT, geofence.getLat() + ""));
        nameValuePairs.add(new BasicNameValuePair(GEOFENCE_RADIUS, geofence.getRadius() + ""));
        putJSON(GEOFENCES, geofence.getId() + "", nameValuePairs);
    }

    public void getMessageInGeofence(int messageId, int geofenceId) {
        getJSON(GEOFENCES, messageId + "", geofenceId + "");
    }
}