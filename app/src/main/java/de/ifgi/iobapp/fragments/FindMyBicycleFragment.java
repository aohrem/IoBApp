package de.ifgi.iobapp.fragments;

import android.app.Fragment;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.ifgi.iobapp.R;


public class FindMyBicycleFragment extends Fragment {

    private static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    private GoogleMap mMap;
    private MapView mMapView;

    private final LocationListener mLocationListener = new MyLocationListener();
    private LocationManager mLocationManager;
    private Marker mYourPositionMarker;
    private Marker mYourBicycleMarker;

    private static final long LOCATION_REFRESH_DISTANCE = 1;
    private static final long LOCATION_REFRESH_TIME = 1;

    public FindMyBicycleFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_my_bicycle, container, false);

        setUpMap(view, savedInstanceState);

        mLocationManager = (LocationManager) getActivity().getSystemService(
                getActivity().LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);

        if ( mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateYourLocationMarker(location);
        }

        Button relocateButton = (Button) view.findViewById(R.id.relocate_button);
        relocateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }

    private void updateYourLocationMarker(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if ( mYourPositionMarker == null ) {
            mYourPositionMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Your Position")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_your_position)));
        }
        else {
            mYourPositionMarker.setPosition(latLng);
        }
    }

    private void setUpMap(View view, Bundle savedInstanceState) {
        MapsInitializer.initialize(getActivity());

        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) ) {
            case ConnectionResult.SUCCESS:
                mMapView = (MapView) view.findViewById(R.id.map);
                mMapView.onCreate(savedInstanceState);

                if (mMapView != null) {
                    mMap = mMapView.getMap();

                    mYourBicycleMarker = mMap.addMarker(new MarkerOptions()
                            .position(HAMBURG)
                            .title("Your Bicycle")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_bicycle)));
                    mYourBicycleMarker.showInfoWindow();

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 12));
                }
                break;
            case ConnectionResult.SERVICE_MISSING:
                Toast.makeText(getActivity(), "Google Play Services missing",
                        Toast.LENGTH_SHORT).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Toast.makeText(getActivity(), "Please update Google Play Services",
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), GooglePlayServicesUtil
                        .isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
        }
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
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }
}
