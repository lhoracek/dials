package cz.lhoracek.android.dials.activity;

import android.app.Service;
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
import javax.inject.Named;

import cz.lhoracek.android.dials.App;
import cz.lhoracek.android.dials.R;
import cz.lhoracek.android.dials.service.BaseService;
import cz.lhoracek.android.dials.service.BluetoothService;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends BaseActivity {

    public static final String UPDATE_BROADCAST = "services_state_changed";
    public static final int REQUEST_ENABLE_BT = 1;

    @Inject @Named("serviceClass") Class<?> serviceClass;

    private boolean isBound;
    private BaseService bindingService;

    public MainActivity() {
        App.component().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        startService(new Intent(this, serviceClass));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(this, serviceClass), myConnection, Context.BIND_AUTO_CREATE);
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
            BaseService.BaseBinder binder = (BaseService.BaseBinder) service;
            bindingService = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(getClass().getSimpleName(), "ServiceConnection disconnected");
            bindingService = null;
            isBound = false;
        }
    };
}