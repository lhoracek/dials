package cz.lhoracek.android.dials.model;

import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.databinding.ObservableField;

/**
 * Created by lhoracek on 19/11/16.
 */

public class DialsModel extends BaseObservable {
    private ObservableField<Values> mValues = new ObservableField<>((Values)null);

    public ObservableField<Values> getValues() {
        return mValues;
    }
}
