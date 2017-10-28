package cz.lhoracek.android.dials.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.ivbaranov.rxbluetooth.BluetoothConnection;
import com.github.ivbaranov.rxbluetooth.RxBluetooth;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import cz.lhoracek.android.dials.App;
import cz.lhoracek.android.dials.R;
import cz.lhoracek.android.dials.activity.MainActivity;
import cz.lhoracek.android.dials.events.DataUpdateEvent;
import cz.lhoracek.android.dials.events.bluetooth.BluetoothStateChangedEvent;
import cz.lhoracek.android.dials.events.power.PowerStateChangedEvent;
import cz.lhoracek.android.dials.model.Values;
import cz.lhoracek.android.dials.utils.PowerAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by horaclu2 on 09/02/16.
 */
public class BluetoothService extends BaseService {

    private boolean connected = false;
    private boolean bound     = false;

    @Inject           Gson             mGson;
    @Inject @Nullable BluetoothAdapter mBluetoothAdapter;
    @Inject           PowerAdapter     mPowerAdapter;

    public BluetoothService() {
        App.component().inject(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this.toString(), "Starting");
        stateChanged();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(this.toString(), "service starting");
        return START_STICKY;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothStateChanged(BluetoothStateChangedEvent event) {
        stateChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPowerStateChanged(PowerStateChangedEvent event) {
        stateChanged();
    }


    protected void stateChanged() {
        // TODO shit


        if (mPowerAdapter.isPlugged()){
            if(bound){
                hideNotification();
            }else {
                showNotification();
            }

        }
    }

    @Override
    protected void bindingChanged(boolean bound) {
        this.bound = bound;
        stateChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (connected) {
            stopBluetooth();
        }
        stateChanged();
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
                                    // TODO save socket?
                                    connected = true;
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
        Log.d(this.toString(), "stopping bluetooth");

        // TODO

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MainActivity.UPDATE_BROADCAST));
        hideNotification();
    }

    private void showNotification() {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent launchPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setContentIntent(launchPendingIntent)
                .setSmallIcon(R.drawable.ic_gauge_notification)
                .setContentTitle(getString(R.string.notification_running))
                .setContentText(getString(R.string.notification_running_detail))
                .build();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(R.id.notification, notification);
    }

    private void hideNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(R.id.notification);
    }
}
