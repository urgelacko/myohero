package com.urgesoft.gopro;

import android.app.Application;

public class MyoHeroApplication extends Application {

    private static MyoHeroApplication context;

    public static final boolean DEV_MODE = false;

    @Override
    public void onCreate() {
        super.onCreate();
        MyoHeroApplication.context = (MyoHeroApplication) getApplicationContext();
    }

    public static MyoHeroApplication getAppContext() {
        return MyoHeroApplication.context;
    }

}
