package cz.lhoracek.android.dials.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by lhoracek on 05/12/16.
 */

public abstract class BaseService extends Service {
    private final IBinder myBinder = new BaseBinder();
    protected @Inject EventBus mEventBus;

    public class BaseBinder extends Binder {
        public BaseService getService() {
            return BaseService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.d(this.toString(), "service creating");
        super.onCreate();
        mEventBus.register(this);
    }

    @Override
    public void onDestroy() {
        mEventBus.unregister(this);
        super.onDestroy();
        Log.d(this.toString(), "service destoyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(this.toString(), "service bound");
        bindingChanged(true);
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        Log.d(this.toString(), "service unbound");
        bindingChanged(false);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(this.toString(), "service rebound");
        bindingChanged(true);
    }

    protected abstract void bindingChanged(boolean bound);
}
