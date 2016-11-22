package cz.lhoracek.android.dials.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import cz.lhoracek.android.dials.R;
import cz.lhoracek.android.dials.service.BluetoothService;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends BaseActivity {

    public static final String UPDATE_BROADCAST  = "services_state_changed";
    public static final int    REQUEST_ENABLE_BT = 1;

    // TODO
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothService bluetoothService;
    private boolean isBound = false;

    private View mMainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mMainView = findViewById(R.id.drawer_layout);
        startService(new Intent(this, BluetoothService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(this, BluetoothService.class), myConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(UPDATE_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBound) {
            unbindService(myConnection);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private void updateConnection() {
        // TODO
    }


    private ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(getClass().getSimpleName(), "ServiceConnection connected");
            BluetoothService.BluetoothBinder binder = (BluetoothService.BluetoothBinder) service;
            bluetoothService = binder.getService();
            isBound = true;
            updateConnection();
        }

        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(getClass().getSimpleName(), "ServiceConnection disconnected");
            bluetoothService = null;
            isBound = false;
            updateConnection();
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateConnection();
        }
    };
}