package com.urgesoft.gopro.event;

import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;

/**
 * Created by szabol on 2014.11.01..
 */
public class MyoPoseEvent {

    private final long timestamp;

    private Pose pose;

    private Arm arm;

    private boolean hold = false;

    public MyoPoseEvent(Pose pose, Arm arm, long timestamp) {
        this.pose = pose;
        this.arm = arm;
        this.timestamp = timestamp;
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

    public MyoPoseEvent setHold(boolean isHold) {
        this.hold = isHold;
        return this;
    }

    public boolean isHold() {
        return hold;
    }

    @Override
    public String toString() {
        return pose.name();
    }


    public Arm getArm() {
        return arm;
    }

    public MyoPoseEvent setArm(Arm arm) {
        this.arm = arm;
        return this;
    }
}
