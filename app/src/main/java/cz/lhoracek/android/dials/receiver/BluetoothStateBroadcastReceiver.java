package cz.lhoracek.android.dials.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import cz.lhoracek.android.dials.App;

/**
 * Created by horaclu2 on 24/11/16.
 */

public class BluetoothStateBroadcastReceiver extends BroadcastReceiver {

    @Inject EventBus mEventBus;

    public BluetoothStateBroadcastReceiver() {
        App.component().inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                BluetoothAdapter.ERROR);
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                Log.d(getClass().getSimpleName(), "Bluetooth off");
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                Log.d(getClass().getSimpleName(), "Turning Bluetooth off...");
                break;
            case BluetoothAdapter.STATE_ON:
                Log.d(getClass().getSimpleName(), "Bluetooth on");
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                Log.d(getClass().getSimpleName(), "Turning Bluetooth on...");
                break;
        }
    }
}
