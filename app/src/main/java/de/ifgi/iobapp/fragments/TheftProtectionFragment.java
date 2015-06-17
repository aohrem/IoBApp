package de.ifgi.iobapp.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import de.ifgi.iobapp.R;
import de.ifgi.iobapp.bluetooth.BluetoothArduino;


public class TheftProtectionFragment extends Fragment implements TagFragment {

    private static final String TAG = "TheftProtection";

    private boolean mCurrentStatusLocked = true;
    private boolean mModeManual = true;

    private BluetoothArduino mBlue = BluetoothArduino.getInstance("IOB");

    public TheftProtectionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theft_protection, container, false);

        TextView currentStatusTextView = (TextView) view.findViewById(R.id.current_status_text_view);
        ImageButton currentStatusImageButton = (ImageButton) view.findViewById(R.id.current_status_image_button);
        if ( mCurrentStatusLocked ) {
            currentStatusTextView.setText(getString(R.string.locked));
            currentStatusImageButton.setImageResource(R.mipmap.theft_protection);
        }
        else {
            currentStatusTextView.setText(getString(R.string.unlocked));
            currentStatusImageButton.setImageResource(R.mipmap.theft_protection_unlocked);
        }

        TextView modeTextView = (TextView) view.findViewById(R.id.mode);
        if ( mModeManual ) {
            modeTextView.setText(getString(R.string.manual_mode_activated));
        }
        else {
            modeTextView.setText(getString(R.string.automatic_mode_activated));
        }

        ToggleButton modeToggleButton = (ToggleButton) view.findViewById(R.id.mode_toggle_button);
        modeToggleButton.setChecked(mModeManual);

        // TEST AREA
        mBlue.Connect();

        Button buttonReceive = (Button) view.findViewById(R.id.button_receive);
        buttonReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = (TextView) view.findViewById(R.id.message_receive_text);
                textView.setText("Received Message: " + mBlue.getLastMessage());
            }
        });

        Button buttonSend = (Button) view.findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBlue.SendMessage("Test message send by Android");
            }
        });
        // END TEST AREA

        return view;
    }

    public String getFragmentTag() {
        return TAG;
    }

}
