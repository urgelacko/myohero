package com.urgesoft.myo.states.combo;

import com.thalmic.myo.Pose;

/**
 * Created by szabol on 2014.11.17..
 */
public enum HoldPose {

    FIST, WAVE_IN, WAVE_OUT, FINGERS_SPREAD;

    private static final String HOLD_SUFFIX = "_HOLD";

    @Override
    public String toString() {
        return super.toString() + HOLD_SUFFIX;
    }

    public static String assignmentKeyForPose(Pose pose) {
        return pose.name() + HOLD_SUFFIX;
    }
}
