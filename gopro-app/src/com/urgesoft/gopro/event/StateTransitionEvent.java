package com.urgesoft.gopro.event;

import com.thalmic.myo.Arm;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

/**
 * Created by szabol on 2014.11.10..
 */
public class StateTransitionEvent {

    private Pose pose;

    private long timestamp = System.currentTimeMillis();

    private Myo myo;

    private Arm arm;

    private boolean hold = false;

    public Pose getPose() {
        return pose;
    }

    public StateTransitionEvent withPose(Pose pose) {
        this.pose = pose;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public StateTransitionEvent at(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Myo getMyo() {
        return myo;
    }

    public StateTransitionEvent onMyo(Myo myo) {
        this.myo = myo;
        return this;
    }

    public Arm getArm() {
        return arm;
    }

    public StateTransitionEvent onArm(Arm arm) {
        this.arm = arm;
        return this;
    }


    public boolean isHold() {
        return hold;
    }

    public StateTransitionEvent setHold(boolean hold) {
        this.hold = hold;
        return this;
    }
}
