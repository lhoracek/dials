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
import cz.lhoracek.android.dials.service.BluetoothService;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class}, dependencies = {})
public interface AppComponent {
    void inject(MainActivity activity);

    void inject(BluetoothService service);

    void inject(DialsFragment fragment);
}
