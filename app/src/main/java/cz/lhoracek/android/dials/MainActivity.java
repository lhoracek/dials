package cz.lhoracek.android.dials;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import cz.lhoracek.android.dials.service.BluetoothService;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity implements View.OnSystemUiVisibilityChangeListener, View.OnTouchListener {

    public static final String UPDATE_BROADCAST  = "services_state_changed";
    public static final int    REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothService bluetoothService;
    private boolean isBound = false;

    private View mDecorView;
    private View mMainView;

    private final Handler mLeanBackHandler = new Handler();
    private int mLastSystemUIVisibility;
    private final Runnable mEnterLeanback = new Runnable() {
        @Override
        public void run() {
            enableFullScreen(true);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mDecorView = getWindow().getDecorView();

        mMainView = findViewById(R.id.drawer_layout);
        mDecorView.setOnSystemUiVisibilityChangeListener(this);
        mMainView.setOnTouchListener(this);

        enableFullScreen(true);
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

    protected void enableFullScreen(boolean enabled) {
        int newVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        if (enabled) {
            newVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        }
        // Want to hide again after 3s
        if (!enabled) {
            resetHideTimer();
        }
        // Set the visibility
        mDecorView.setSystemUiVisibility(newVisibility);
    }

    private void resetHideTimer() {
        // First cancel any queued events - i.e. resetting the countdown clock
        mLeanBackHandler.removeCallbacks(mEnterLeanback);
        // And fire the event in 3s time
        mLeanBackHandler.postDelayed(mEnterLeanback, 3000);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        resetHideTimer();
        return false;
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        if ((mLastSystemUIVisibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0 && (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
            resetHideTimer();
        }
        mLastSystemUIVisibility = visibility;
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