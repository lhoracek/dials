package cz.lhoracek.android.dials.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Set;

import cz.lhoracek.android.dials.MainActivity;
import cz.lhoracek.android.dials.R;

/**
 * Created by horaclu2 on 09/02/16.
 */
public class BluetoothService extends Service {
    public static final String STOP_BROADCAST = "services_stop_service";


    private final IBinder          myBinder          = new BluetoothBinder();
    private       boolean          running           = false;
    private       BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public class BluetoothBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this.toString(), "service creating");


        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            // TODO
        }
    }

    public boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(this.toString(), "service starting");
        if (!running) {
            running = true;
            Log.d(this.toString(), "Starting");

            startBluetooth();

            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MainActivity.UPDATE_BROADCAST));
            registerReceiver(mMessageReceiver, new IntentFilter(STOP_BROADCAST));

        }
        return START_STICKY;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(this.toString(), "Received broadcast to stop audio");
            stopBluetooth();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(this.toString(), "service bound");
        if (running) {
            hideNotification();
        }
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        Log.d(this.toString(), "service unbound");
        if (running) {
            showNotification();
        }
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(this.toString(), "service rebound");
        // A client is binding to the service with bindService(), after onUnbind() has already been called
        if (running) {
            hideNotification();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRunning()) {
            stopBluetooth();
        }
        Log.d(this.toString(), "service done");
    }

    public boolean isRunning() {
        return running;
    }

    public void startService() {
        startService(new Intent(this, BluetoothService.class));
        Log.d(this.toString(), "service starting bluetooth");
    }

    public void startBluetooth() {
        Log.d(this.toString(), "starting bluetooth");
        // TODO


        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
// If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Log.d(this.toString(), "BT Device: " + device.getName());
                // TODO find mine
            }
        }
    }

    public void stopBluetooth() {
        running = false;
        unregisterReceiver(mMessageReceiver);
        Log.d(this.toString(), "stopping bluetooth");

        // TODO

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MainActivity.UPDATE_BROADCAST));
        hideNotification();
    }

    private void showNotification() {
        Log.d(this.toString(), "Show notification");
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent launchPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent        stopIntent        = new Intent(STOP_BROADCAST);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, 1, stopIntent, PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new Notification.Builder(this)
                .setContentIntent(launchPendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Bluetooth running")
                .setContentText("Headphone loopback is running")
                .addAction(R.mipmap.ic_launcher, "Stop playback", stopPendingIntent)
                .build();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(R.id.notification, notification);
    }

    private void hideNotification() {
        Log.d(this.toString(), "Cancel notification");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(R.id.notification);
    }
}
