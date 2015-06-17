package de.ifgi.iobapp.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.ifgi.iobapp.R;


public class PreferencesFragment extends Fragment implements TagFragment {

    private static final String TAG = "Preferences";

    public PreferencesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);

        return view;
    }

    public String getFragmentTag() {
        return TAG;
    }


}
