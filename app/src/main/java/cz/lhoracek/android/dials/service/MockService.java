package cz.lhoracek.android.dials.service;

import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import cz.lhoracek.android.dials.App;
import cz.lhoracek.android.dials.events.DataUpdateEvent;
import cz.lhoracek.android.dials.model.Values;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lhoracek on 09/02/16.
 */
public class MockService extends BaseService {

    @Inject Gson mGson;
    private Disposable mSubscription;

    public MockService() {
        App.component().inject(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this.toString(), "creating");
        bindingChanged(true);
    }

    public class MockEvent {
    }

    @Subscribe
    public void onMockEvent(MockEvent event) {
    }

    @Override
    protected void bindingChanged(boolean bound) {
        if (mSubscription != null) {
            mSubscription.dispose();
            mSubscription = null;
        }
        if (bound) {
                Log.d(this.toString(), "Starting interval observable");
                mSubscription = Observable.interval(0, 50, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(value -> updateValues(value));
        }
    }

    private void updateValues(long millis) {

        int state = (int) millis;
        boolean on = ((state / 20) % 2) > 0;
        int gearNumber = ((state / 10) % 7);
        Integer gear = gearNumber > 0 ? gearNumber : null;
        Values v = Values.create(
                gear,
                (state * 10) % 20000,
                ((state * 300) % 14000) - 1000,
                (((state) % 50) + 110) / 10.0f,
                ((state * 5) % 900) / 10.0f,
                ((state * 2) + 50) % 100,
                (((state * 3) % 100) / 2.0f) + 60,
                state % 250,
                on,
                !on,
                on,
                !on,
                on
        );
        this.mEventBus.post(new DataUpdateEvent(v));
    }
}
