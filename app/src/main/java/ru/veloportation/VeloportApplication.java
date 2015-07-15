package ru.veloportation;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Looper;

import ru.veloportation.veloport.BuildConfig;
import ru.veloportation.veloport.ConstantsVeloportApp;

public class VeloportApplication extends Application {


    public static boolean orderFlagDEBUG = false;

    private volatile static VeloportApplication instance;
    private SharedPreferences applicationPreferences;

    public SharedPreferences getApplicationPreferences() {
        return applicationPreferences;
    }

    private SharedPreferences.Editor applicationPreferencesEditor;

    public SharedPreferences.Editor getApplicationPreferencesEditor() {
        return applicationPreferencesEditor;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        synchronized (VeloportApplication.class) {
            if (BuildConfig.DEBUG) {
                if (instance != null)
                    throw new RuntimeException("Something strange: there is another application instance.");
            }
            instance = this;

            VeloportApplication.class.notifyAll();
        }

        applicationPreferences = getSharedPreferences(ConstantsVeloportApp.SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        applicationPreferencesEditor = applicationPreferences.edit();
    }


    public static VeloportApplication  getInstance() {
        VeloportApplication  application = instance;
        if (application == null) {
            synchronized (VeloportApplication .class) {
                if (instance == null) {
                    if (BuildConfig.DEBUG) {
                        if (Thread.currentThread() == Looper.getMainLooper().getThread())
                            throw new UnsupportedOperationException(
                                    "Current application's instance has not been initialized yet (wait for onCreate, please).");
                    }
                    try {
                        do {
                            VeloportApplication .class.wait();
                        } while ((application = instance) == null);
                    } catch (InterruptedException e) {
                        /* Nothing to do */
                    }
                }
            }
        }
        return application;
    }

}
