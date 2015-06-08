package de.ifgi.iobapp.bluetooth;

public class BluetoothConnection {

    private BluetoothArduino mBlue = BluetoothArduino.getInstance("ExampleRobot");

    public BluetoothConnection() {
        mBlue.Connect();
        String msg = mBlue.getLastMessage();
    }

}