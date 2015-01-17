package com.urgesoft.gopro;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;

public class MyoHeroApplication extends Application {

    private static MyoHeroApplication context;

    public static final boolean DEV_MODE = false;

    @Override
    public void onCreate() {
        super.onCreate();
        MyoHeroApplication.context = (MyoHeroApplication) getApplicationContext();
    }

    @Override
    public void onTerminate() {

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiManager.WifiLock wifiLock = wifiManager.createWifiLock(MyoHeroApplication.class.getSimpleName());
        wifiLock.setReferenceCounted(false);
        wifiLock.release();

        super.onTerminate();
    }

    public static MyoHeroApplication getAppContext() {
        return MyoHeroApplication.context;
    }

}
