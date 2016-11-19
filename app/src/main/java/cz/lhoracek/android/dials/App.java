package cz.lhoracek.android.dials;

import android.app.Application;

import cz.lhoracek.android.dials.di.AppComponent;
import cz.lhoracek.android.dials.di.AppModule;
import cz.lhoracek.android.dials.di.DaggerAppComponent;

/**
 * Created by horaclu2 on 19/11/16.
 */

public class App extends Application {
    private static App instance;

    protected AppComponent mComponent;

    public App() {
        instance = this;
    }

    protected AppComponent createComponent() {
        return DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public static AppComponent component() {
        return (AppComponent) (instance.getComponent());
    }

    public AppComponent getComponent() {
        return this.mComponent;
    }

    public void onCreate() {
        super.onCreate();

        this.mComponent = this.createComponent();
    }
}
