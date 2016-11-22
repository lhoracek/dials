package cz.lhoracek.android.dials.utils;

import android.databinding.BindingAdapter;

import cz.lhoracek.android.dials.views.BarView;
import cz.lhoracek.android.dials.views.ControlView;
import cz.lhoracek.android.dials.views.DialView;
import cz.lhoracek.android.dials.views.RPMView;
import cz.lhoracek.android.dials.views.ValueView;

/**
 * Created by horaclu2 on 19/11/16.
 */

public class BindingAdapters {
    @BindingAdapter("value")
    public static void setRPM(RPMView rpmView, int value){
        rpmView.setValue(value);
    }

    @BindingAdapter("warningMax")
    public static void setWarningMax(ValueView valueView, float warning){
        valueView.setWarningMaxValue(warning);
    }

    @BindingAdapter("value")
    public static void setDial(DialView dialView, float value){
        dialView.setValue(value);
    }

    @BindingAdapter("value")
    public static void setBar(BarView barView, float value){
        barView.setValue(value);
    }

    @BindingAdapter("enable")
    public static void setBar(ControlView controlView, boolean enabled){
        controlView.setEnabled(true);
    }
}
