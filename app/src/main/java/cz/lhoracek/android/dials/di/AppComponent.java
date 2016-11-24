/**
 * AppModule.Java
 * Symtrac 2
 * <p/>
 * Created by Michek, Karel (Ext) on 12/04/16.
 * Copyright © 2016 Novartis. All rights reserved.
 */

package cz.lhoracek.android.dials.di;

import javax.inject.Singleton;

import cz.lhoracek.android.dials.activity.MainActivity;
import cz.lhoracek.android.dials.fragment.DialsFragment;
import cz.lhoracek.android.dials.receiver.BluetoothStateBroadcastReceiver;
import cz.lhoracek.android.dials.service.BluetoothService;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class}, dependencies = {})
public interface AppComponent {
    /* Activities */
    void inject(MainActivity activity);

    /* Services */
    void inject(BluetoothService service);

    /* Receivers */
    void inject(BluetoothStateBroadcastReceiver receiver);

    /* Fragments */
    void inject(DialsFragment fragment);
}
