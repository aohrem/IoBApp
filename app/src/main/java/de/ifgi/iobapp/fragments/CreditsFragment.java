package de.ifgi.iobapp.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.ifgi.iobapp.R;


public class CreditsFragment extends Fragment implements TagFragment {

    private static final String TAG = "Credits";

    public CreditsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_credits, container, false);
    }

    public String getFragmentTag() {
        return TAG;
    }


}
