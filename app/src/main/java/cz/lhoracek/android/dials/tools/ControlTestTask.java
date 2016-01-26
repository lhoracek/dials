package cz.lhoracek.android.dials.tools;

import android.os.AsyncTask;

import cz.lhoracek.android.dials.views.ControlView;

/**
 * Created by lhoracek on 1/26/16.
 */
public class ControlTestTask extends AsyncTask<Void, Boolean, Void> {

    private ControlView[] controlViews;

    public ControlTestTask(ControlView... valueViews) {
        this.controlViews = valueViews;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        for (ControlView controlView : controlViews) {
            controlView.setEnabled(false);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        for (ControlView controlView : controlViews) {
            controlView.setEnabled(true);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // nothing
        }
        return null;
    }
}
