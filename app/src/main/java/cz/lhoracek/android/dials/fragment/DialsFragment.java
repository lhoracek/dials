package cz.lhoracek.android.dials.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.lhoracek.android.dials.databinding.FragmentDialsBinding;
import cz.lhoracek.android.dials.model.DialsModel;

/**
 * Created by horaclu2 on 09/02/16.
 */
public class DialsFragment extends Fragment {

    FragmentDialsBinding mBinding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentDialsBinding.inflate(inflater);
        mBinding.setModel(new DialsModel());
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            tests();
        }
    }

    private void tests() {
        onUpdate();
    }

    private void onUpdate() {
        // TODO
       // rpmView.setWarningMaxValue(10000);

        mBinding.getModel().setRevs(8756);
        mBinding.getModel().setSpeed(123);
        mBinding.getModel().setGear(5);
        mBinding.getModel().setVoltage(13.5f);
        mBinding.getModel().setOilTemp(90.5f);

        mBinding.getModel().setTurnlight(true);
        mBinding.getModel().setNeutral(true);
        mBinding.getModel().setEngine(true);
        mBinding.getModel().setLowBeam(true);
        mBinding.getModel().setHighBeam(true);

        mBinding.getModel().setTemp(90.5f);
        mBinding.getModel().setFuel(0.5f);
    }

}
