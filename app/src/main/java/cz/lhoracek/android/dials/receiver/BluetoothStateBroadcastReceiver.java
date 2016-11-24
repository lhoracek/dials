package cz.lhoracek.android.dials.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import cz.lhoracek.android.dials.App;
import cz.lhoracek.android.dials.events.bluetooth.TurnedOffEvent;
import cz.lhoracek.android.dials.events.bluetooth.TurnedOnEvent;
import cz.lhoracek.android.dials.events.bluetooth.TurningOffEvent;
import cz.lhoracek.android.dials.events.bluetooth.TurningOnEvent;

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
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                BluetoothAdapter.ERROR);
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                mEventBus.post(new TurnedOffEvent());
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                mEventBus.post(new TurningOffEvent());
                break;
            case BluetoothAdapter.STATE_ON:
                mEventBus.post(new TurnedOnEvent());
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                mEventBus.post(new TurningOnEvent());
                break;
        }
    }
}
