package de.ifgi.iobapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.ifgi.iobapp.R;


public class FindMyBicycleFragment extends Fragment {

    private static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    private GoogleMap mMap;
    private MapView mMapView;

    public FindMyBicycleFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_my_bicycle, container, false);

        setUpMap(view, savedInstanceState);

        return view;
    }

    private void setUpMap(View view, Bundle savedInstanceState) {
        MapsInitializer.initialize(getActivity());

        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) ) {
            case ConnectionResult.SUCCESS:
                mMapView = (MapView) view.findViewById(R.id.map);
                mMapView.onCreate(savedInstanceState);

                if (mMapView != null) {
                    mMap = mMapView.getMap();

                    Marker hamburg = mMap.addMarker(new MarkerOptions().position(HAMBURG).title("Hamburg"));

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));
                }
                break;
            case ConnectionResult.SERVICE_MISSING:
                Toast.makeText(getActivity(), "Google Play Services missing", Toast.LENGTH_SHORT).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Toast.makeText(getActivity(), "Please update Google Play Services", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
        }
    }

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
