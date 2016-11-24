package cz.lhoracek.android.dials.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import javax.inject.Inject;

import cz.lhoracek.android.dials.App;
import cz.lhoracek.android.dials.R;
import cz.lhoracek.android.dials.service.BluetoothService;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends BaseActivity {

    public static final String UPDATE_BROADCAST  = "services_state_changed";
    public static final int    REQUEST_ENABLE_BT = 1;

    @Inject @Nullable BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private boolean          isBound;
    private BluetoothService bluetoothService;

    public MainActivity() {
        App.component().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        startService(new Intent(this, BluetoothService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(this, BluetoothService.class), myConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBound) {
            unbindService(myConnection);
        }
    }

    private ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(getClass().getSimpleName(), "ServiceConnection connected");
            BluetoothService.BluetoothBinder binder = (BluetoothService.BluetoothBinder) service;
            bluetoothService = binder.getService();
            isBound = true;
            // TODO
        }

        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(getClass().getSimpleName(), "ServiceConnection disconnected");
            bluetoothService = null;
            isBound = false;
            // TODO
        }
    };
}