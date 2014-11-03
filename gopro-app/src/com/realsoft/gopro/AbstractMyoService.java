/*
 * Copyright (C) 2014 Thalmic Labs Inc.
 * Distributed under the Myo SDK license agreement. See LICENSE.txt for details.
 */

package com.realsoft.gopro;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.common.collect.Iterables;
import com.realsoft.gopro.event.MyoErrorEvent;
import com.realsoft.gopro.event.MyoPoseEvent;
import com.realsoft.gopro.event.MyoState;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

public abstract class AbstractMyoService extends Service {

    private static final String TAG = "MyoService";

    /**
     * For onPeriodic() callback
     */
    Handler mHandler = new Handler();

    private DeviceListener mListener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
            onMyoConnect(myo, timestamp);
            EventBus.getDefault().postSticky(new MyoState(true));
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            onMyoDisconnect(myo, timestamp);
            EventBus.getDefault().postSticky(new MyoState(false));
        }

        // onPose() is called whenever the Myo detects that the person wearing it has changed their pose, for example,
        // making a fist, or not making a fist anymore.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {

            MyoState currentState = getMyoState();
            //FIXME timestamp format???
            EventBus.getDefault().postSticky(new MyoPoseEvent(pose, System.currentTimeMillis()));

            if (currentState.isLocked() && !Pose.THUMB_TO_PINKY.equals(pose)) {
                return;
            }
            switch (pose) {
                case THUMB_TO_PINKY:
                    onThumbToPinky(myo, timestamp, pose);
                    break;
                case FINGERS_SPREAD:
                    onFingersSpread(myo, timestamp, pose);
                    break;
                case FIST:
                    onFist(myo, timestamp, pose);
                    break;
                case WAVE_IN:
                    onWaveIn(myo, timestamp, pose);
                    break;
                case WAVE_OUT:
                    onWaveOut(myo, timestamp, pose);
                    break;
                case REST:
                    onRest(myo, timestamp, pose);
                    break;
            }
        }
    };
    private PeriodicCallbackRunnable periodicTask;

    @Override
    public void onCreate() {
        super.onCreate();


        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            EventBus.getDefault().post(new MyoErrorEvent("Could not initialize myo hub!"));
            stopSelf();

            return;
        }

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);
        //Start the onPeriodic executor
        periodicTask = new PeriodicCallbackRunnable();
        mHandler.postDelayed(periodicTask, 100);

        // Finally, scan for Myo devices and connect to the first one found.
        hub.pairWithAnyMyo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        mHandler.removeCallbacks(periodicTask);

        // We don't want any callbacks when the Service is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);
        Hub.getInstance().shutdown();


    }

    protected int getPoseHoldTime(Pose pose) {
        //The default hold time is almost unlimited, so it will never happen
        return Integer.MAX_VALUE;
    }


    protected MyoState getMyoState() {
        return MyoState.getCurrent();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class PeriodicCallbackRunnable implements Runnable {

        @Override
        public void run() {

            MyoPoseEvent lastPose = MyoPoseEvent.get();
            if (null != lastPose) {
                checkHold(lastPose);
            }
            onPeriodic(getMyoState(), lastPose);

            mHandler.postDelayed(this, 15);
        }

        private void checkHold(MyoPoseEvent lastPose) {

            List<Myo> connectedDevices = Hub.getInstance().getConnectedDevices();
            Myo myo = Iterables.getLast(connectedDevices);

            long holdTime = System.currentTimeMillis() - lastPose.getTimestamp();
            //If the hold event is not processed already and we reached the Pose dependant hold time
            if (!lastPose.isHold() && getPoseHoldTime(lastPose.getPose()) < holdTime) {
                //FIXME not works with multiple myo
                onHold(myo, lastPose.getPose());
                //The notification flag toggle
                EventBus.getDefault().postSticky(lastPose.setHold(true));
            }
        }


    }

    protected void onMyoConnect(Myo myo, long timestamp) {

    }

    protected void onMyoDisconnect(Myo myo, long timestamp) {

    }

    protected void onWaveIn(Myo myo, long timestamp, Pose pose) {
    }

    protected void onWaveOut(Myo myo, long timestamp, Pose pose) {
    }

    protected void onFist(Myo myo, long timestamp, Pose pose) {
    }

    protected void onFingersSpread(Myo myo, long timestamp, Pose pose) {
    }

    protected void onThumbToPinky(Myo myo, long timestamp, Pose pose) {
    }

    protected void onRest(Myo myo, long timestamp, Pose pose) {

    }

    protected void onHold(Myo myo, Pose pose) {
    }

    protected void onPeriodic(MyoState myoState, MyoPoseEvent lastPose) {

    }
}
