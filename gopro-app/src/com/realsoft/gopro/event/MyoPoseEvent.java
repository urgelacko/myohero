package com.realsoft.gopro.event;

import com.thalmic.myo.Pose;

import de.greenrobot.event.EventBus;

/**
 * Created by szabol on 2014.11.01..
 */
public class MyoPoseEvent {

    private Pose pose;

    private long timestamp;

    private boolean hold = false;

    public MyoPoseEvent(Pose pose, long timestamp) {
        this.pose = pose;
        this.timestamp = timestamp;
    }

    public static MyoPoseEvent get() {
        return EventBus.getDefault().getStickyEvent(MyoPoseEvent.class);
    }

    public Pose getPose() {
        return pose;
    }

    public MyoPoseEvent setPose(Pose pose) {
        this.pose = pose;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isHold() {
        return hold;
    }

    public MyoPoseEvent setHold(boolean hold) {
        this.hold = hold;
        return this;
    }
}
