package com.example.wombatapp.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.example.wombatapp.datatypes.ScaleMeasurement;

import timber.log.Timber;

public class OpenScale {
    public static boolean DEBUG_MODE = false;
    private BluetoothCommunication btDeviceDriver;
    private Context context;

    public OpenScale(Context context) {
        this.context = context;
        btDeviceDriver = null;
    }
    public int addScaleMeasurement(final ScaleMeasurement scaleMeasurement) {
        return addScaleMeasurement(scaleMeasurement, false);
    }

    public int addScaleMeasurement(final ScaleMeasurement scaleMeasurement, boolean silent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Check user id and do a smart user assign if option is enabled
        if (scaleMeasurement.getUserId() == -1) {

            // don't add scale data if no user is selected
            if (scaleMeasurement.getUserId() == -1) {
                Timber.e("to be added measurement are thrown away because no user is selected");
                return -1;
            }
        }
        return addScaleMeasurement(scaleMeasurement);
    }

    public void connectToBluetoothDeviceDebugMode(String hwAddress, Handler callbackBtHandler) {
        Timber.d("Trying to connect to bluetooth device [%s] in debug mode", hwAddress);
        disconnectFromBluetoothDevice();
        btDeviceDriver = BluetoothFactory.createDebugDriver(context);
        btDeviceDriver.registerCallbackHandler(callbackBtHandler);
        btDeviceDriver.connect(hwAddress);
    }

    public boolean connectToBluetoothDevice(String deviceName, String hwAddress, Handler callbackBtHandler) {
        Timber.d("Trying to connect to bluetooth device [%s] (%s)", hwAddress, deviceName);

        disconnectFromBluetoothDevice();

        btDeviceDriver = BluetoothFactory.createDeviceDriver(context, deviceName);
        if (btDeviceDriver == null) {
            return false;
        }

        btDeviceDriver.registerCallbackHandler(callbackBtHandler);
        btDeviceDriver.connect(hwAddress);

        return true;
    }

    public boolean disconnectFromBluetoothDevice() {
        if (btDeviceDriver == null) {
            return false;
        }

        Timber.d("Disconnecting from bluetooth device");
        btDeviceDriver.disconnect();
        btDeviceDriver = null;

        return true;
    }

}
