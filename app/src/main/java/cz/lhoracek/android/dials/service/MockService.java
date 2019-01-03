package cz.lhoracek.android.dials.service;

import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import cz.lhoracek.android.dials.App;
import cz.lhoracek.android.dials.events.DataUpdateEvent;
import cz.lhoracek.android.dials.model.Values;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lhoracek on 09/02/16.
 */
public class MockService extends BaseService {

    @Inject Gson mGson;
    private Subscription mSubscription;

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
        if (bound) {

            Subscription updater = Observable.interval(0, 50, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(value -> updateValues(value));

        } else {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
                mSubscription = null;
            }
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
                (state * 100) % 13000,
                (((state ) % 50) + 110) / 10.0f,
                ((state * 5) % 900) / 10.0f,
                ((state* 2) + 50)  % 100,
                (((state * 3) % 100) / 2.0f)  + 60,
                state  % 250,
                on,
                !on,
                on,
                !on,
                on
        );
        this.mEventBus.post(new DataUpdateEvent(v));
    }
}
