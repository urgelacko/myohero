package com.urgesoft.gopro.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.urgesoft.gopro.MyoHeroApplication;
import com.urgesoft.gopro.controller.GoProApiGoProController;
import com.urgesoft.gopro.controller.GoProCommand;
import com.urgesoft.gopro.controller.GoProController;
import com.urgesoft.gopro.controller.ToasterGoProController;
import com.urgesoft.gopro.event.GoProCommandEvent;
import com.urgesoft.gopro.event.GoProCommandResultEvent;
import com.urgesoft.gopro.event.GoProConnectCommandEvent;
import com.urgesoft.gopro.event.GoProConnectionChangeEvent;
import com.urgesoft.gopro.event.GoProDisconnectCommandEvent;
import com.urgesoft.gopro.event.GoProErrorEvent;
import com.urgesoft.gopro.event.GoProState;
import com.urgesoft.gopro.event.GoProStatus;
import com.urgesoft.gopro.event.ToastEvent;

import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;

public class GoProControllerFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = GoProControllerFragment.class.getSimpleName();

    private WifiManager wifiManager;

    private GoProController goPro;

    private String wifiConfigSSID;


    /**
     * Fragment initialization.  We way we want to be retained and
     * start our thread.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(this.getActivity()).registerOnSharedPreferenceChangeListener(this);
        // Tell the framework to try to keep this fragment around during a configuration change.
        setRetainInstance(true);

        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiConfigSSID = readConfiguredSSID();
        goPro = new GoProApiGoProController(readConfiguredPassword());
        if (MyoHeroApplication.DEV_MODE) {
            goPro = new ToasterGoProController();
        }


        EventBus.getDefault().register(this);
//        Refresh connection status from initial disconnected
        boolean isAlreadyConnected = isConnectedToGoProWifi(quoteWifi(wifiConfigSSID));
        EventBus.getDefault().post(isAlreadyConnected ? new GoProConnectionChangeEvent(GoProState.CONNECTED) : new GoProConnectionChangeEvent(GoProState.DISCONNECTED));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        PreferenceManager.getDefaultSharedPreferences(this.getActivity()).unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {
            case SettingsFragment.KEY_GOPRO_PW:
                goPro.setPassword(sharedPreferences.getString(SettingsFragment.KEY_GOPRO_PW, ""));

                EventBus.getDefault().post(new GoProDisconnectCommandEvent());
                EventBus.getDefault().post(new GoProConnectCommandEvent());

                break;
            case SettingsFragment.KEY_GOPRO_SSID:

                WifiConfigurationPredicate wifiConfigurationPredicate = new WifiConfigurationPredicate(wifiConfigSSID);
                WifiConfiguration actualConfig = findGoProNetwork(wifiConfigurationPredicate);
                if (null != actualConfig) {
                    wifiManager.removeNetwork(actualConfig.networkId);
                    wifiManager.saveConfiguration();
                }

                wifiConfigSSID = readConfiguredSSID();

                EventBus.getDefault().post(new GoProDisconnectCommandEvent());
                EventBus.getDefault().post(new GoProConnectCommandEvent());

                break;
        }
    }

    /**
     * EVENTS
     */
    public void onEventBackgroundThread(final GoProCommandEvent event) {
        Log.i(TAG, "GoProEvent: " + event.getCommand());
        goPro.executeCommand(event);
    }

    public void onEventBackgroundThread(final GoProConnectCommandEvent event) {
        wifiConfigSSID = readConfiguredSSID();
        connectToGoProNetwork();
    }

    public void onEventBackgroundThread(final GoProDisconnectCommandEvent event) {
        wifiManager.disconnect();
    }

    public void onEventBackgroundThread(final GoProConnectionChangeEvent event) {
        refreshGoProPowerStatusOnBus(event.getNewState());

    }

    private void refreshGoProPowerStatusOnBus(GoProState newState) {
        GoProStatus newStatus = refreshBackPackStatus(newState);
        EventBus.getDefault().postSticky(newStatus);
    }

    private GoProStatus refreshBackPackStatus(GoProState newState) {

        GoProStatus newStatus = new GoProStatus(newState);
        if (GoProState.CONNECTED.equals(newState)) {
            newStatus.setBackPackStatus(goPro.queryBackPackStatus());
        }

        return newStatus;
    }

    private boolean checkNoConnectionNeed(String ssid, String pass) {

        if (Strings.isNullOrEmpty(ssid) || Strings.isNullOrEmpty(pass)) {
            EventBus.getDefault().post(new ToastEvent(getString(com.urgesoft.gopro.R.string.gopro_warning_setup_wifi)));
            return true;
        }

        boolean isAlreadyConnected = isConnectedToGoProWifi(quoteWifi(ssid));
        return isAlreadyConnected;
    }

    private boolean isConnectedToGoProWifi(String ssid) {
        return wifiManager.getConnectionInfo().getSSID().equals(ssid) && SupplicantState.COMPLETED.equals(wifiManager.getConnectionInfo().getSupplicantState());
    }


    private void connectToGoProNetwork() {

        String password = readConfiguredPassword();
        if (checkNoConnectionNeed(wifiConfigSSID, password)) {
            EventBus.getDefault().post(new GoProConnectionChangeEvent(GoProState.CONNECTED));
            return;
        }

        String wifiConfigPass = "\"" + password + "\"";

        try {
            wifiManager.setWifiEnabled(true);

            WifiConfigurationPredicate findGoProNetworkPredicate = new WifiConfigurationPredicate(wifiConfigSSID);
            WifiConfiguration actualGoProConfig = findGoProNetwork(findGoProNetworkPredicate);

            WifiConfiguration conf = new WifiConfiguration();
            conf.preSharedKey = wifiConfigPass;
            conf.SSID = quoteWifi(wifiConfigSSID);
            if (null != actualGoProConfig) {
                conf.networkId = actualGoProConfig.networkId;
                wifiManager.updateNetwork(conf);
            } else {
                wifiManager.addNetwork(conf);
            }
            Preconditions.checkState(wifiManager.saveConfiguration(), "GoPro wifi network save failed.");

            WifiConfiguration goProConfig = findGoProNetwork(findGoProNetworkPredicate);
            wifiManager.enableNetwork(Preconditions.checkNotNull(goProConfig).networkId, true);
            wifiManager.reconnect();
        } catch (final Exception e) {
            Log.e(TAG, "WIFI error", e);
            EventBus.getDefault().post(new GoProConnectionChangeEvent(GoProState.DISCONNECTED));
            EventBus.getDefault().post(new GoProErrorEvent(e, GoProCommand.CONNECT));
        }

    }

    private WifiConfiguration findGoProNetwork(WifiConfigurationPredicate findGoProNetworkPredicate) {

        List<WifiConfiguration> configuredNetworks = MoreObjects.firstNonNull(wifiManager.getConfiguredNetworks(), Collections.<WifiConfiguration>emptyList());
        for (WifiConfiguration config : configuredNetworks) {
            if (findGoProNetworkPredicate.apply(config)) {
                return config;
            }
        }

        return null;
    }


    private String readConfiguredPassword() {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return settings.getString(SettingsFragment.KEY_GOPRO_PW, "");
    }

    private String readConfiguredSSID() {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return settings.getString(SettingsFragment.KEY_GOPRO_SSID, "");
    }

    private static String quoteWifi(String quoted) {
        return "\"" + quoted + "\"";
    }

//    public void onEventMainThread(final GoProErrorEvent errorEvent) {
//        Log.e(TAG, String.format("Error during: %s", getString(errorEvent.getCommand().getMessageKey()));
//    }

    public void onEventMainThread(final GoProCommandResultEvent resultEvent) {
        Log.v(TAG, String.format("Success: %s", getString(resultEvent.getCommand().getMessageKey())));
    }


    private static class WifiConfigurationPredicate implements Predicate<WifiConfiguration> {
        private final String wifiConfigSSID;

        public WifiConfigurationPredicate(String wifiConfigSSID) {
            this.wifiConfigSSID = quoteWifi(wifiConfigSSID);
        }

        @Override
        public boolean apply(WifiConfiguration input) {
            return wifiConfigSSID.equals(input.SSID);
        }
    }
}
