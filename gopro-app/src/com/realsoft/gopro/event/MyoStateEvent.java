package com.realsoft.gopro.event;

import de.greenrobot.event.EventBus;

/**
 * Created by szabol on 2014.11.01..
 */
public class MyoStateEvent {


    private boolean locked = true;

    private boolean connected = false;


    public MyoStateEvent(boolean connected) {
        this.connected = connected;
    }


    public boolean isConnected() {
        return connected;
    }

    public MyoStateEvent setConnected(boolean connected) {
        this.connected = connected;
        return this;
    }

    public boolean isLocked() {
        return locked;
    }

    public MyoStateEvent setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public static MyoStateEvent getCurrent() {
        return EventBus.getDefault().getStickyEvent(MyoStateEvent.class);
    }
}
