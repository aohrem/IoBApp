package de.ifgi.iobapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class FindMyBicycleFragment extends Fragment {

    private static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    private GoogleMap map;

    public FindMyBicycleFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_my_bicycle, container, false);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG).title("Hamburg"));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

        return view;
    }


}
