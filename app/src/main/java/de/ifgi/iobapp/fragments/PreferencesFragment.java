package de.ifgi.iobapp.fragments;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import de.ifgi.iobapp.R;


public class PreferencesFragment extends Fragment implements TagFragment {

    private static final String TAG = "Preferences";
    private static final String PACKAGE = "de.ifgi.iobapp";
    private static final String DEVICE_ID = ".deviceid" ;

    public PreferencesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);

        final SharedPreferences prefs = getActivity().getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);

        EditText deviceIdEditText = (EditText) view.findViewById(R.id.device_id_edit_text);
        deviceIdEditText.setText(prefs.getString(PACKAGE + DEVICE_ID, ""));
        deviceIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                prefs.edit().putString(PACKAGE + DEVICE_ID, editable.toString()).apply();
            }
        });

        return view;
    }

    public String getFragmentTag() {
        return TAG;
    }


}
