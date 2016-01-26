package cz.lhoracek.android.dials.tools;

import android.os.AsyncTask;

import cz.lhoracek.android.dials.views.ValueView;

/**
 * Created by lhoracek on 1/26/16.
 */
public class GraphTestTask extends AsyncTask<Void, Integer, Void> {

    private ValueView[] valueViews;

    public GraphTestTask(ValueView... valueViews) {
        this.valueViews = valueViews;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int progress = values[0];
        for (ValueView valueView : valueViews) {
            valueView.setValue(valueView.getMinValue() + ((valueView.getMaxValue() - valueView.getMinValue()) / (float) 100 * progress));
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

        for (int i = 0; i < 100; i++) {
            publishProgress(i);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // nothing
            }
        }

        for (int i = 100; i > 0; i--) {
            publishProgress(i);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // nothing
            }
        }

        publishProgress(Integer.MIN_VALUE);
        return null;
    }
}
