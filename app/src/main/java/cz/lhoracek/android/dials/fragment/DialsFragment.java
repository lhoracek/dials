package cz.lhoracek.android.dials.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cz.lhoracek.android.dials.databinding.FragmentDialsBinding;
import cz.lhoracek.android.dials.events.DataUpdateEvent;
import cz.lhoracek.android.dials.model.DialsModel;

/**
 * Created by horaclu2 on 09/02/16.
 */
public class DialsFragment extends Fragment {

    private FragmentDialsBinding mBinding;
    private DialsModel           viewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentDialsBinding.inflate(inflater);
        mBinding.setViewModel(viewModel = new DialsModel());
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            tests();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DataUpdateEvent event) {
        viewModel.setRevs(event.getRevs());
       // mBinding.rpmViewRevsGraph.setValue(event.getRevs());
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    private void tests() {
        onUpdate();
    }

    private void onUpdate() {
        // TODO

        // rpmView.setWarningMaxValue(10000);
        viewModel.setRevs(8756);
        viewModel.setSpeed(123);
        viewModel.setGear(5);
        viewModel.setVoltage(13.5f);
        viewModel.setOilTemp(90.5f);

        viewModel.setTurnlight(true);
        viewModel.setNeutral(true);
        viewModel.setEngine(true);
        viewModel.setLowBeam(true);
        viewModel.setHighBeam(true);

        viewModel.setTemp(90.5f);
        viewModel.setFuel(50f);
    }

}
