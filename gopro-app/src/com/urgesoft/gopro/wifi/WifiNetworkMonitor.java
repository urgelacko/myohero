package com.urgesoft.gopro.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

import com.urgesoft.gopro.MyoHeroApplication;
import com.urgesoft.gopro.event.GoProConnectionChangeEvent;
import com.urgesoft.gopro.event.GoProState;
import com.urgesoft.gopro.ui.fragment.SettingsFragment;

import de.greenrobot.event.EventBus;

/**
 * Created by szabol on 2014.11.22..
 */
public class WifiNetworkMonitor extends BroadcastReceiver {


    @Override

    public void onReceive(Context context, Intent intent) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        switch (intent.getAction()) {
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                handleSupplicantConnectionChange(context, intent, wifiManager);
                break;
            case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                handleSupplicantStateChange(context, intent, wifiManager);
                break;
        }
    }

    private void handleSupplicantStateChange(Context context, Intent intent, WifiManager wifiManager) {

        SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
        switch (supplicantState) {
            case COMPLETED:
                handleSupplicantConnected(context, wifiManager);
                break;
            case ASSOCIATING:
            case SCANNING:
                handleSupplicantScanning(context, wifiManager);
                break;
            case DISCONNECTED: {
                EventBus.getDefault().post(new GoProConnectionChangeEvent(GoProState.DISCONNECTED));
                break;
            }
        }
    }

    private void handleSupplicantScanning(Context context, WifiManager wifiManager) {
        if (MyoHeroApplication.DEV_MODE) {
            return;
        }

        GoProConnectionChangeEvent connectionEvent = new GoProConnectionChangeEvent(GoProState.CONNECTING);
        postEventIfOnGoProNetwork(context, wifiManager, connectionEvent);
        EventBus.getDefault().post(connectionEvent);
    }

    private void handleSupplicantConnected(Context context, WifiManager wifiManager) {
        if (MyoHeroApplication.DEV_MODE) {
            return;
        }

        GoProConnectionChangeEvent event = new GoProConnectionChangeEvent(GoProState.CONNECTED);
        postEventIfOnGoProNetwork(context, wifiManager, event);
    }

    private void postEventIfOnGoProNetwork(Context context, WifiManager wifiManager, GoProConnectionChangeEvent event) {
        String ssid = "\"" + readConfiguredSSID(context) + "\"";
        //If the wifi network matches
        if (ssid.equals(wifiManager.getConnectionInfo().getSSID())) {
            EventBus.getDefault().post(event);
        }
    }

    private void handleSupplicantConnectionChange(Context context, Intent intent, WifiManager wifiManager) {

        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
        if (WifiManager.WIFI_STATE_DISABLED == wifiState) {
            EventBus.getDefault().post(new GoProConnectionChangeEvent(GoProState.DISCONNECTED));
        }
    }


    private String readConfiguredSSID(Context context) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(SettingsFragment.KEY_GOPRO_SSID, "");
    }

}
