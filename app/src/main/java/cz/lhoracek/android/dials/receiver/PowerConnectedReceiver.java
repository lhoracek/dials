package cz.lhoracek.android.dials.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import cz.lhoracek.android.dials.App;
import cz.lhoracek.android.dials.events.bluetooth.TurnedOffEvent;

/**
 * Created by horaclu2 on 24/11/16.
 */

public class PowerConnectedReceiver extends BroadcastReceiver {

    @Inject EventBus mEventBus;

    public PowerConnectedReceiver() {
        App.component().inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_POWER_CONNECTED:
                mEventBus.post(new TurnedOffEvent());
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                mEventBus.post(new TurnedOffEvent());
                break;
        }
    }
}
