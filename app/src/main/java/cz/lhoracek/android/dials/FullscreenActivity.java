package cz.lhoracek.android.dials;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.lhoracek.android.dials.tools.ControlTestTask;
import cz.lhoracek.android.dials.tools.DigitTestTask;
import cz.lhoracek.android.dials.tools.GraphTestTask;
import cz.lhoracek.android.dials.views.BarView;
import cz.lhoracek.android.dials.views.ControlView;
import cz.lhoracek.android.dials.views.DialView;
import cz.lhoracek.android.dials.views.RPMView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements View.OnSystemUiVisibilityChangeListener, View.OnTouchListener {

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

    private final Runnable mRuntests = new Runnable() {
        @Override
        public void run() {
            new GraphTestTask(rpmView, barViewFuel, barViewTemp, dialViewOilTemp, dialViewVoltage).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new ControlTestTask(controlViewHighBeam, controlViewLowBeam, controlViewIgnition, controlViewNeutral, controlViewTurn).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new DigitTestTask(textViewGear).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 1, 6);
            new DigitTestTask(textViewRpm).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0, 13000);
            new DigitTestTask(textViewSpeed).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0, 230);
        }
    };

    @BindView(R.id.barView_fuel) BarView barViewFuel;
    @BindView(R.id.barView_temp) BarView barViewTemp;

    @BindView(R.id.dialView_oilTemp) DialView dialViewOilTemp;
    @BindView(R.id.dialView_voltage) DialView dialViewVoltage;

    @BindView(R.id.rpmView_revsGraph) RPMView rpmView;

    @BindView(R.id.textView_speed) TextView textViewSpeed;
    @BindView(R.id.textView_gear)  TextView textViewGear;
    @BindView(R.id.textView_rpm)   TextView textViewRpm;

    @BindView(R.id.control_highBeam) ControlView controlViewHighBeam;
    @BindView(R.id.control_lowBeam)  ControlView controlViewLowBeam;
    @BindView(R.id.control_ignition) ControlView controlViewIgnition;
    @BindView(R.id.control_neutral)  ControlView controlViewNeutral;
    @BindView(R.id.control_turn)     ControlView controlViewTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mDecorView = getWindow().getDecorView();

        mMainView = findViewById(R.id.layout_main);
        mDecorView.setOnSystemUiVisibilityChangeListener(this);
        mMainView.setOnTouchListener(this);
        mMainView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("TAG", "Long click biatch!");
                return false;
            }
        });

        enableFullScreen(true);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            tests();
        }

        rpmView.setWarningMaxValue(10000);
    }

    private void tests() {
        new Handler().postDelayed(mRuntests, 3000);
    }

    private void onUpdate() {
        // TODO
        rpmView.setValue(8765);
        barViewFuel.setValue(60);
        barViewTemp.setValue(90);

        controlViewLowBeam.setEnabled(true);
        controlViewHighBeam.setEnabled(true);
        controlViewNeutral.setEnabled(true);

        textViewGear.setText("x");
        textViewRpm.setText("8765");
        textViewSpeed.setText("138");

        dialViewVoltage.setValue(13.8f);
        dialViewOilTemp.setValue(90);
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
        // If the `mainView` receives a click event then reset the leanback-mode clock
        Log.d("TAG", "Touch biatch!");
        resetHideTimer();
        return true;
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        if ((mLastSystemUIVisibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0 && (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
            resetHideTimer();
        }
        mLastSystemUIVisibility = visibility;
    }

}