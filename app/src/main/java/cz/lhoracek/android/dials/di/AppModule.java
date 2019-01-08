/**
 * AppModule.Java
 * Enlace De Vida
 * <p/>
 * Created by Michek, Karel (Ext) on 12/02/16.
 * Copyright © 2015 Novartis. All rights reserved.
 */

package cz.lhoracek.android.dials.di;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;
import android.support.annotation.Nullable;

import com.github.ivbaranov.rxbluetooth.RxBluetooth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Named;
import javax.inject.Singleton;

import cz.lhoracek.android.dials.service.BluetoothService;
import cz.lhoracek.android.dials.service.MockService;
import cz.lhoracek.android.dials.utils.GsonTypeAdapterFactory;
import dagger.Module;
import dagger.Provides;


@Module()
public class AppModule {

    protected final Application mApp;

    public AppModule(Application app) {
        mApp = app;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mApp.getApplicationContext();
    }

    @Provides
    @Singleton
    LocationManager provideLocationManager(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides
    @Singleton
    EventBus provideBus() {
        return EventBus.getDefault();
    }

    @Provides
    @Singleton
    Gson provideGson(Context context) {
        return new GsonBuilder()
                .registerTypeAdapterFactory(GsonTypeAdapterFactory.create())
                .create();
    }

    @Provides
    @Singleton
    @Named("serviceClass")
    Class<?> provideServiceClass() {
        //return MockService.class;
        return BluetoothService.class;
    }

    @Provides
    @Singleton
    @Nullable
    BluetoothAdapter provideBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    @Provides
    @Singleton
    RxBluetooth provideRxBluetooth(Context context) {
        return new RxBluetooth(context);
    }
}
