package cz.lhoracek.android.dials.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.ivbaranov.rxbluetooth.BluetoothConnection;
import com.github.ivbaranov.rxbluetooth.RxBluetooth;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import cz.lhoracek.android.dials.App;
import cz.lhoracek.android.dials.R;
import cz.lhoracek.android.dials.activity.MainActivity;
import cz.lhoracek.android.dials.events.DataUpdateEvent;
import cz.lhoracek.android.dials.events.bluetooth.BluetoothStateChangedEvent;
import cz.lhoracek.android.dials.events.power.PowerStateChangedEvent;
import cz.lhoracek.android.dials.model.Values;
import cz.lhoracek.android.dials.utils.PowerAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lhoracek on 09/02/16.
 */
public class BluetoothService extends BaseService {
    public static final String SERIAL_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private boolean connected = false;
    private boolean bound = false;

    @Inject Gson mGson;
    @Inject @Nullable BluetoothAdapter mBluetoothAdapter;
    @Inject PowerAdapter mPowerAdapter;
    @Inject RxBluetooth rxBluetooth;

    public BluetoothService() {
        App.component().inject(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this.toString(), "creating");
        stateChanged();
        startBluetooth();
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


        // TODO figure out why this was in
        // TODO display bluetooth state
        //if (mPowerAdapter.isPlugged()){
        if (bound) {
            hideNotification();
        } else {
            showNotification();
        }
        //}
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
        // TODO unbind
        stateChanged();
    }


    private Disposable disposableSubscription;

    public void startBluetooth() {
        Log.d(this.toString(), "starting bluetooth");

        if (!rxBluetooth.isBluetoothAvailable()) {
            Log.d(this.toString(), "Bluetooth is not supported!");
            return;
        }

        if (!rxBluetooth.isBluetoothEnabled()) {
            Log.d(this.toString(), "Bluetooth should be enabled first!");
            return;
        }

        Log.d(this.toString(), "Going observing");
        disposableSubscription = rxBluetooth.observeDevices()
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .filter(bluetoothDevice -> "ESP32test".equals(bluetoothDevice.getName()))
                .doOnEach(bluetoothDevice -> Log.i(this.toString(), "Found device"))
                .subscribe(bluetoothDevice -> subscribeToDevice(bluetoothDevice));

        rxBluetooth.startDiscovery();
        Log.d(this.toString(), "Discovery started");
    }

    private void subscribeToDevice(BluetoothDevice bluetoothDevice) {
        rxBluetooth.connectAsClient(bluetoothDevice, getSppUUID())
                .toObservable()
                .map(socket -> new BluetoothConnection(socket))
                .doOnEach(string -> Log.i(this.toString(), "Got socket"))
                .flatMap(bluetoothConnection -> bluetoothConnection.observeStringStream().toObservable())
                .doOnEach(string -> Log.i(this.toString(), "Got stream"))
                .filter(string -> !string.isEmpty())
                .filter(string -> !string.contains("}{"))
                //.doOnEach(string -> Log.i(this.toString(), "Received " + string))
                .map(string -> mGson.fromJson(string, Values.class))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .retry()
                .subscribe(values -> BluetoothService.this.mEventBus.post(new DataUpdateEvent(values))
                        , throwable -> Log.i(this.toString(), "Error receiving"));
    }

    private UUID getSppUUID() {
        return UUID.fromString(SERIAL_UUID);
    }

    public void stopBluetooth() {
        Log.d(this.toString(), "stopping bluetooth");

        // TODO unsubscribe

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
                // TODO action to turn it off
                // intent to call BroadcastReceiver
                // .addAction(R.mipmap.ic_launcher,"Turn OFF",pi)

                .build();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(R.id.notification, notification);
    }

    private void hideNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(R.id.notification);
    }
}
