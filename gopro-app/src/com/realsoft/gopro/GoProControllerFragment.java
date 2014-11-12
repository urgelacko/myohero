/*
 *
 *  Copyright (c) 2014 RealSoft Informatikai Kft. All Rights Reserved.
 *
 *  This software is the confidential and proprietary information of
 *  RealSoft Informatikai Kft. ("Confidential Information").
 *  You shall not disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with RealSoft.
 *
 *  REALSOFT MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 *  THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *  TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *  PARTICULAR PURPOSE, OR NON-INFRINGEMENT. REALSOFT SHALL NOT BE LIABLE FOR
 *  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 *  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.realsoft.gopro;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.realsoft.gopro.controller.GoProApiGoProController;
import com.realsoft.gopro.controller.GoProCommand;
import com.realsoft.gopro.controller.GoProController;
import com.realsoft.gopro.controller.ToasterGoProController;
import com.realsoft.gopro.event.GoProCommandEvent;
import com.realsoft.gopro.event.GoProCommandResultEvent;
import com.realsoft.gopro.event.GoProErrorEvent;
import com.realsoft.gopro.event.GoProStatus;

import de.greenrobot.event.EventBus;

public class GoProControllerFragment extends Fragment {

    private static final String TAG = GoProControllerFragment.class.getSimpleName();

    private WifiManager wifiManager;

    private String wifiSSID;
    private String goProPass;

    private GoProController goPro;


    /**
     * Fragment initialization.  We way we want to be retained and
     * start our thread.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Tell the framework to try to keep this fragment around
        // during a configuration change.
        setRetainInstance(true);
        EventBus.getDefault().register(this);

        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        wifiSSID = settings.getString(SettingsActivity.KEY_GOPRO_SSID, "");
        goProPass = settings.getString(SettingsActivity.KEY_GOPRO_PW, "");

         goPro = new ToasterGoProController();
//        goPro = new GoProApiGoProController(goProPass);

        refreshWifi();

        EventBus.getDefault().postSticky(new GoProStatus());
    }

    public void onEventBackgroundThread(final GoProCommandEvent event) {

        Log.i(TAG, "GoProEvent: " + event.getCommand());

        goPro.executeCommand(event);

    }

	/*
     * EVENTS
	 */

    /**
     * Error
     *
     * @param errorEvent
     */
    public void onEventMainThread(final GoProErrorEvent errorEvent) {
        Toast.makeText(getActivity(), String.format("Error during: %s", getString(errorEvent.getCommand().getMessageKey())), Toast.LENGTH_SHORT).show();
    }

    /**
     * Command executed
     *
     * @param resultEvent
     */
    public void onEventMainThread(final GoProCommandResultEvent resultEvent) {
        Toast.makeText(getActivity(), String.format("Success: %s", getString(resultEvent.getCommand().getMessageKey())), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void refreshWifi() {

        try {

            wifiManager.setWifiEnabled(true);

            final WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + wifiSSID + "\"";
            conf.preSharedKey = "\"" + goProPass + "\"";
            wifiManager.addNetwork(conf);

            final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (!wifiSSID.equals(wifiInfo.getSSID())) {

                final String wifiConfigSSID = "\"" + wifiSSID + "\"";
                for (final WifiConfiguration i : wifiManager.getConfiguredNetworks()) {
                    if (wifiConfigSSID.equals(i.SSID)) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(i.networkId, true);
                        wifiManager.reconnect();

                        break;
                    }
                }
            }

        } catch (final Exception e) {
            Log.e(TAG, "WIFI error", e);
            EventBus.getDefault().post(new GoProErrorEvent(e, GoProCommand.CONNECT));
        }
    }
}
