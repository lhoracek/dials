package cz.lhoracek.android.dials.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.ivbaranov.rxbluetooth.BluetoothConnection;
import com.github.ivbaranov.rxbluetooth.RxBluetooth;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lhoracek on 09/02/16.
 */
public class BluetoothService extends BaseService {
    public static final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String DEVICE_ADDRESS = "B4:E6:2D:8E:52:9F";

    private boolean bound = false;

    @Inject Gson mGson;
    @Inject @Nullable BluetoothAdapter mBluetoothAdapter;
    @Inject PowerAdapter mPowerAdapter;
    @Inject RxBluetooth rxBluetooth;

    Disposable bluetoothSubscription = null;

    public BluetoothService() {
        App.component().inject(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this.toString(), "creating");
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
        // todo take power state into account
        if (bound) {
            startBluetooth();
            hideNotification();
        } else {
            stopBluetooth();
            showNotification();
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
        hideNotification();
    }

    private void stopBluetooth() {
        if (bluetoothSubscription != null) {
            bluetoothSubscription.dispose();
            bluetoothSubscription = null;
        }
    }

    private void startBluetooth() {
        Log.d(this.toString(), "starting bluetooth");

        if (!rxBluetooth.isBluetoothAvailable()) {
            Log.e(this.toString(), "Bluetooth is not supported!");
            return;
        }

        if (!rxBluetooth.isBluetoothEnabled()) {
            Log.e(this.toString(), "Bluetooth should be enabled first!");
            return;
        }

        if(bluetoothSubscription != null){
            Log.e(this.toString(), "Already connected!");
            return;
        }
        bluetoothSubscription = Observable.just(mBluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS))
                .flatMap(bluetoothDevice -> rxBluetooth.connectAsClient(bluetoothDevice, SERIAL_UUID).toObservable())
                .map(socket -> new BluetoothConnection(socket))
                .doOnNext(bluetoothConnectionNotification -> Log.i(this.toString(), "Bluetooth socket open"))
                .flatMap(bluetoothConnection -> bluetoothConnection.observeStringStream('#').toObservable())
                .filter(string -> !string.isEmpty())
                .map(string -> mGson.fromJson(string, Values.class))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .retry((integer, throwable) -> {Log.e(this.toString(), "Got error", throwable); return true;})
                .subscribe(values -> this.mEventBus.post(new DataUpdateEvent(values)));
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
