package com.realsoft.gopro.event;

import com.thalmic.myo.Pose;

import java.security.Timestamp;

import de.greenrobot.event.EventBus;

/**
 * Created by szabol on 2014.11.01..
 */
public class MyoState {


    private boolean locked = true;

    private boolean connected = false;


    public MyoState(boolean connected) {
        this.connected = connected;
    }


    public boolean isConnected() {
        return connected;
    }

    public MyoState setConnected(boolean connected) {
        this.connected = connected;
        return this;
    }

    public boolean isLocked() {
        return locked;
    }

    public MyoState setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public static MyoState getCurrent() {
        return EventBus.getDefault().getStickyEvent(MyoState.class);
    }
}
