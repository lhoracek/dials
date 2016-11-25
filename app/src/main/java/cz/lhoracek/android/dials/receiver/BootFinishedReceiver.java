package cz.lhoracek.android.dials.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cz.lhoracek.android.dials.service.BluetoothService;

/**
 * Created by horaclu2 on 24/05/16.
 */
public class BootFinishedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,BluetoothService.class));
    }
}
