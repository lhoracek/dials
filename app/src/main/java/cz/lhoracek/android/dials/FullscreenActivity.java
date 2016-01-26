package cz.lhoracek.android.dials;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.lhoracek.android.dials.views.BarView;
import cz.lhoracek.android.dials.views.ControlView;
import cz.lhoracek.android.dials.views.DialView;
import cz.lhoracek.android.dials.views.RPMView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements View.OnSystemUiVisibilityChangeListener, View.OnClickListener {

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

    @Bind(R.id.barView_fuel) BarView barViewFuel;
    @Bind(R.id.barView_temp) BarView barViewTemp;

    @Bind(R.id.dialView_oilTemp) DialView dialViewOilTemp;
    @Bind(R.id.dialView_voltage) DialView dialViewVoltage;

    @Bind(R.id.rpmView_revsGraph) RPMView rpmView;

    @Bind(R.id.textView_speed) TextView textViewSpeed;
    @Bind(R.id.textView_gear)  TextView textViewGear;
    @Bind(R.id.textView_rpm)  TextView textViewRpm;

    @Bind(R.id.control_highBeam) ControlView controlViewHighBeam;
    @Bind(R.id.control_lowBeam)  ControlView controlViewLowBeam;
    @Bind(R.id.control_ignition) ControlView controlViewIgnition;
    @Bind(R.id.control_neutral)  ControlView controlViewNeutral;
    @Bind(R.id.control_turn)     ControlView controlViewTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mDecorView = getWindow().getDecorView();

        mMainView = findViewById(R.id.layout_main);
        mMainView.setClickable(true);

        mDecorView.setOnSystemUiVisibilityChangeListener(this);
        mMainView.setOnClickListener(this);

        enableFullScreen(true);
        ButterKnife.bind(this);


        // TODO
        rpmView.setValue(7000);
        barViewFuel.setValue(60);
        barViewTemp.setValue(90);

        controlViewLowBeam.setEnabled(true);
        controlViewHighBeam.setEnabled(true);
        controlViewNeutral.setEnabled(true);

        textViewGear.setText("x");
        textViewRpm.setText("10200");
        textViewSpeed.setText("138");
    }

    protected void enableFullScreen(boolean enabled) {
        int newVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        if (enabled) {
            newVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
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
    public void onClick(View v) {
        // If the `mainView` receives a click event then reset the leanback-mode clock
        resetHideTimer();
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        if ((mLastSystemUIVisibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0 && (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
            resetHideTimer();
        }
        mLastSystemUIVisibility = visibility;
    }

}