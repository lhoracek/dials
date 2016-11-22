package cz.lhoracek.android.dials.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.ivbaranov.rxbluetooth.BluetoothConnection;
import com.github.ivbaranov.rxbluetooth.RxBluetooth;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import cz.lhoracek.android.dials.App;
import cz.lhoracek.android.dials.activity.MainActivity;
import cz.lhoracek.android.dials.R;
import cz.lhoracek.android.dials.events.DataUpdateEvent;
import cz.lhoracek.android.dials.model.Values;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by horaclu2 on 09/02/16.
 */
public class BluetoothService extends Service {
    public static final String STOP_BROADCAST = "services_stop_service";

    private final IBinder myBinder = new BluetoothBinder();
    private       boolean running  = false;

    @Inject           Gson             mGson;
    @Inject @Nullable BluetoothAdapter mBluetoothAdapter;


    public BluetoothService() {
        App.component().inject(this);
    }

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

            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
                // TODO
                return super.onStartCommand(intent, flags, startId);
            }
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

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Log.d(this.toString(), "BT Device: " + device.getName());
                // TODO find mine
                if (device.getName().equals("HC-06")) {
                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                    RxBluetooth rxBluetooth = new RxBluetooth(this);
                    rxBluetooth.observeConnectDevice(device, uuid)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Action1<BluetoothSocket>() {
                                @Override
                                public void call(BluetoothSocket socket) {
                                    // Connected to the device, do anything with the socket
                                    Log.d(this.toString(), "Connected to device");

                                    try {
                                        final BluetoothConnection bluetoothConnection = new BluetoothConnection(socket);

                                        // observe strings received
                                        bluetoothConnection.observeStringStream()
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribeOn(Schedulers.io())
                                                .subscribe(new Action1<String>() {
                                                    @Override
                                                    public void call(String string) {
                                                        //Log.d(this.toString(), "Incomming " + string);
                                                        try {
                                                            Values values = mGson.fromJson(string, Values.class);
                                                            EventBus.getDefault().post(new DataUpdateEvent(values));
                                                        } catch (Exception e) {
                                                            Log.e(this.toString(), "Error receiving " + string, e);
                                                        }
                                                    }
                                                }, new Action1<Throwable>() {
                                                    @Override
                                                    public void call(Throwable throwable) {
                                                        Log.e(this.toString(), "Error receiving");
                                                        throwable.printStackTrace();
                                                        // Error occured
                                                        // TODO
                                                        startBluetooth();
                                                    }
                                                });
                                    } catch (Exception e) {
                                        Log.e(this.toString(), "Error");
                                        e.printStackTrace();
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    // Error occured

                                    Log.e(this.toString(), "Error socket");
                                    throwable.printStackTrace();
                                    // TODO
                                    startBluetooth();
                                }
                            });
                }
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

        Intent stopIntent = new Intent(STOP_BROADCAST);
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
