package cz.lhoracek.android.dials.model;

import android.databinding.BaseObservable;

/**
 * Created by horaclu2 on 19/11/16.
 */

public class DialsModel extends BaseObservable {
    private Values mValues;

    public void setValues(Values values) {
        mValues = values;
        notifyChange();
    }

    public Values getValues() {
        return mValues;
    }
}
