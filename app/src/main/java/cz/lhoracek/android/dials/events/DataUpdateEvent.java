package cz.lhoracek.android.dials.events;

import cz.lhoracek.android.dials.model.Values;

/**
 * Created by lhoracek on 19/11/16.
 */

public class DataUpdateEvent {
    private final Values mValues;

    public DataUpdateEvent(Values values) {
        mValues = values;
    }

    public Values getValues() {
        return mValues;
    }
}
