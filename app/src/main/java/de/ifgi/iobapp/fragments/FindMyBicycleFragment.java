package de.ifgi.iobapp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import de.ifgi.iobapp.R;
import de.ifgi.iobapp.api.IoBAPI;
import de.ifgi.iobapp.api.GetJSONClient;
import de.ifgi.iobapp.api.MessageJSONParser;
import de.ifgi.iobapp.model.Geofence;
import de.ifgi.iobapp.model.LocationTime;
import de.ifgi.iobapp.model.Message;
import de.ifgi.iobapp.model.MessageComparator;
import de.ifgi.iobapp.model.Notification;
import de.ifgi.iobapp.model.NotificationComparator;
import de.ifgi.iobapp.persistance.NotificationManager;

public class FindMyBicycleFragment extends Fragment implements TagFragment, GetJSONClient.GetJSONListener {

    private static final String TAG = "FindMyBicycle";
    private static final String PACKAGE = "de.ifgi.iobapp";
    private static final String DEVICE_ID = ".deviceid";
    private static final LatLng LAT_LON_UNKNOWN = new LatLng(999, 999);
    private static final int GEOFENCE_DIAMETER = 500;
    private static final float GEOFENCE_TRANSPARENCY = 0.4f;

    private GoogleMap mMap;
    private MapView mMapView;
    private ProgressBar mSpinner;

    private final LocationListener mLocationListener = new MyLocationListener();
    private LocationManager mLocationManager;
    private String mLocationProvider = LocationManager.GPS_PROVIDER;

    private Marker mYourPositionMarker;
    private Marker mYourBicycleMarker;
    private LatLng mBicycleLocation;
    private Date mBicycleTime;

    private static final long LOCATION_REFRESH_DISTANCE = 1;
    private static final long LOCATION_REFRESH_TIME = 1;

    public FindMyBicycleFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_my_bicycle, container, false);

        mSpinner = (ProgressBar) view.findViewById(R.id.progress_bar);
        mSpinner.setVisibility(View.GONE);

        setUpMap(view, savedInstanceState);

        Button relocateButton = (Button) view.findViewById(R.id.relocate_button);
        relocateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpinner.setVisibility(View.VISIBLE);
                updateBicycleLocation();
            }
        });

        return view;
    }

    private void updateBicycleLocation() {
        final SharedPreferences prefs = getActivity().getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
        String deviceId = prefs.getString(PACKAGE + DEVICE_ID, "");
        if (!deviceId.trim().equals("")) {
            IoBAPI ioBAPI = new IoBAPI(this);
            ioBAPI.getAllMessagesFromDevice(deviceId);
        }
    }

    @Override
    public void onRemoteCallComplete(String jsonString) {
        mSpinner.setVisibility(View.GONE);

        if (jsonString != null) {
            JSONArray jsonResult = null;
            try {
                jsonResult = (JSONArray) new JSONTokener(jsonString).nextValue();
                Log.d(TAG, jsonResult.toString());

                if (jsonResult != null) {
                    MessageJSONParser messageJsonParser = new MessageJSONParser();
                    ArrayList<Message> messages = messageJsonParser.parseMessages(jsonResult);
                    Log.d(TAG, messages.toString());

                    LocationTime locationTime = getLastKnownLocation(messages);
                    mBicycleLocation = locationTime.getLocation();
                    mBicycleTime = locationTime.getTime();
                    if (mBicycleLocation != LAT_LON_UNKNOWN) {
                        updateYourBicycleMarker();
                    }
                    else {
                        Log.e(TAG, getString(R.string.error_json_array_empty));
                        Toast.makeText(getActivity(), getString(R.string.location_not_found),
                                Toast.LENGTH_LONG).show();
                    }
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage() + "");
                Toast.makeText(getActivity(), getString(R.string.error_loading_bike_location),
                        Toast.LENGTH_LONG).show();
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage() + "");
                Toast.makeText(getActivity(), getString(R.string.error_loading_bike_location),
                        Toast.LENGTH_LONG).show();
            } catch (ClassCastException e) {
                Log.e(TAG, e.getMessage() + " " + getString(R.string.error_invalid_json_array));
                Toast.makeText(getActivity(), getString(R.string.error_loading_bike_location),
                        Toast.LENGTH_LONG).show();
            }
        }
        else {
            Log.e(TAG, getString(R.string.error_http_request));
            Toast.makeText(getActivity(), getString(R.string.error_loading_bike_location),
                    Toast.LENGTH_LONG).show();
        }
    }

    private LocationTime getLastKnownLocation(ArrayList<Message> messages) {
        if (messages.size() == 0) {
            return new LocationTime(LAT_LON_UNKNOWN, new Date());
        }
        else {
            Collections.sort(messages, new MessageComparator());
            int lastIndex = messages.size() - 1;
            Message lastMessage = messages.get(lastIndex);

            LatLng latLng = new LatLng(lastMessage.getLat(), lastMessage.getLon());

            return new LocationTime(latLng, lastMessage.getTimestamp());
        }
    }

    private void updateYourBicycleMarker() {
        if ( mYourBicycleMarker == null ) {
            mYourBicycleMarker = mMap.addMarker(new MarkerOptions()
                    .position(mBicycleLocation)
                    .title(getString(R.string.your_bicycle))
                    .snippet(new SimpleDateFormat(getString(R.string.date_format)).format(mBicycleTime))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_bicycle)));
        }
        else {
            mYourBicycleMarker.hideInfoWindow();
            mYourBicycleMarker.setPosition(mBicycleLocation);
        }

        mYourBicycleMarker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mBicycleLocation, 12));
    }

    private void updateYourLocationMarker(Location location) {
        if ( location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            if ( mYourPositionMarker == null ) {
                mYourPositionMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(getString(R.string.your_location))
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_your_position)));
            }
            else {
                mYourPositionMarker.setPosition(latLng);
            }
        }
    }

    private void setUpMap(View view, Bundle savedInstanceState) {
        MapsInitializer.initialize(getActivity());

        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) ) {
            case ConnectionResult.SUCCESS:
                mMapView = (MapView) view.findViewById(R.id.map);
                mMapView.onCreate(savedInstanceState);

                mLocationManager = (LocationManager) getActivity().getSystemService(
                        getActivity().LOCATION_SERVICE);

                if (mMapView != null) {
                    mMap = mMapView.getMap();

                    startLocationListener();

                    updateBicycleLocation();

                    createGeofences();
                }
                break;
            case ConnectionResult.SERVICE_MISSING:
                Toast.makeText(getActivity(), getString(R.string.google_play_services_missing),
                        Toast.LENGTH_LONG).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Toast.makeText(getActivity(), getString(R.string.please_update_google_play_service),
                        Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getActivity(), GooglePlayServicesUtil
                        .isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_LONG).show();
        }
    }

    private void createGeofences() {
        NotificationManager notificationManager = new NotificationManager(getActivity());
        ArrayList<Notification> notifications = new ArrayList<Notification>();
        try {
            notifications = notificationManager.readNotifications();
            Collections.sort(notifications, new NotificationComparator());
            for (Notification notification : notifications) {
                createGeofence(notification);
                addNotificationMarker(notification);
            }
        } catch (IOException e) {
            Log.e(TAG, "" + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "" + e.getMessage());
        }
    }

    private void addNotificationMarker(Notification notification) {
        LatLng latLng = new LatLng(notification.getRegionCenterLat(),
                notification.getRegionCenterLon());

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(notification.getName())
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)));
    }

    private void createGeofence(Notification notification) {
        LatLng latLng = new LatLng(notification.getRegionCenterLat(),
                notification.getRegionCenterLon());

        int diameter = GEOFENCE_DIAMETER;
        Bitmap bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.iob_app_red));

        canvas.drawCircle(diameter / 2, diameter / 2, diameter / 2, paint);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);

        mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(bitmapDescriptor)
                .position(latLng, notification.getRegionRadius() * 2,
                        notification.getRegionRadius() * 2)
                .transparency(GEOFENCE_TRANSPARENCY));
    }

    private void startLocationListener() {
        if ( mLocationManager.isProviderEnabled(mLocationProvider) ) {
            Location location = mLocationManager.getLastKnownLocation(mLocationProvider);
            updateYourLocationMarker(location);
        }

        mLocationManager.requestLocationUpdates(mLocationProvider, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);
    }

    private void stopLocationListener() {
        mLocationManager.removeUpdates(mLocationListener);
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(final Location location) {
            updateYourLocationMarker(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }

        startLocationListener();

        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMapView != null) {
            mMapView.onDestroy();
        }

        stopLocationListener();
    }

    @Override
    public void onPause() {
        super.onPause();

        stopLocationListener();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    public String getFragmentTag() {
        return TAG;
    }
}
