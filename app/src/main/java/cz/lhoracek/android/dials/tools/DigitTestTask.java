package cz.lhoracek.android.dials.tools;

import android.os.AsyncTask;
import android.widget.TextView;

/**
 * Created by lhoracek on 1/26/16.
 */
public class DigitTestTask extends AsyncTask<Integer, Integer, Void> {

    private TextView textView;

    public DigitTestTask(TextView valueView) {
        this.textView = valueView;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int progress = values[0];
        textView.setText(progress < 0 ? "" : String.valueOf(progress));
    }

    @Override
    protected Void doInBackground(Integer... params) {

        int min = params[0];
        int max = params[1];
        int scale = max - min;

        for (int i = 0; i < 100; i++) {
            publishProgress(min + Math.round(((scale) * ((float) i) / 100)));
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // nothing
            }
        }

        for (int i = 100; i > 0; i--) {
            publishProgress(min + Math.round(((scale) * ((float) i) / 100)));
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // nothing
            }
        }

        publishProgress(-1);
        return null;
    }
}
