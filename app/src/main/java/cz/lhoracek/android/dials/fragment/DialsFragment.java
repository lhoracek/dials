package cz.lhoracek.android.dials.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.lhoracek.android.dials.R;
import cz.lhoracek.android.dials.tools.ControlTestTask;
import cz.lhoracek.android.dials.tools.DigitTestTask;
import cz.lhoracek.android.dials.tools.GraphTestTask;
import cz.lhoracek.android.dials.views.BarView;
import cz.lhoracek.android.dials.views.ControlView;
import cz.lhoracek.android.dials.views.DialView;
import cz.lhoracek.android.dials.views.RPMView;

/**
 * Created by horaclu2 on 09/02/16.
 */
public class DialsFragment extends Fragment {
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

    @Bind(R.id.barView_fuel) BarView barViewFuel;
    @Bind(R.id.barView_temp) BarView barViewTemp;

    @Bind(R.id.dialView_oilTemp) DialView dialViewOilTemp;
    @Bind(R.id.dialView_voltage) DialView dialViewVoltage;

    @Bind(R.id.rpmView_revsGraph) RPMView rpmView;

    @Bind(R.id.textView_speed) TextView textViewSpeed;
    @Bind(R.id.textView_gear)  TextView textViewGear;
    @Bind(R.id.textView_rpm)   TextView textViewRpm;

    @Bind(R.id.control_highBeam) ControlView controlViewHighBeam;
    @Bind(R.id.control_lowBeam)  ControlView controlViewLowBeam;
    @Bind(R.id.control_ignition) ControlView controlViewIgnition;
    @Bind(R.id.control_neutral)  ControlView controlViewNeutral;
    @Bind(R.id.control_turn)     ControlView controlViewTurn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dials, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            tests();
        }
    }

    private void tests() {
        new Handler().postDelayed(mRuntests, 3000);
    }

    private void onUpdate() {
        // TODO
        rpmView.setWarningMaxValue(10000);
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

}
