package de.ifgi.iobapp.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.ifgi.iobapp.R;
import de.ifgi.iobapp.bluetooth.BluetoothArduino;


public class TheftProtectionFragment extends Fragment {

    private View mView;
    private BluetoothArduino mBlue = BluetoothArduino.getInstance("IOB");

    public TheftProtectionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_theft_protection, container, false);
        mBlue.Connect();

        Button buttonReceive = (Button) mView.findViewById(R.id.button_receive);
        buttonReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setReceivedText(mBlue.getLastMessage());
            }
        });

        Button buttonSend = (Button) mView.findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBlue.SendMessage("Test message send by Android");
            }
        });

        return mView;
    }

    private void setReceivedText(String message) {
        TextView textView = (TextView) mView.findViewById(R.id.message_receive_text);
        textView.setText("Received Message: " + message);
    }
}
