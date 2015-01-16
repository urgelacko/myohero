package com.urgesoft.gopro.myo;

import com.google.common.collect.Iterables;
import com.urgesoft.gopro.event.MyoPoseEvent;
import com.urgesoft.myo.states.combo.MyoPoseCombo;
import com.thalmic.myo.Pose;

import java.util.Collections;
import java.util.List;

/**
 * Created by szabol on 2014.11.03..
 */
public class HoldPoseCombo implements MyoPoseCombo {

    private static final int POSE_HOLD_TIME = 1000;

    private Pose poseFilter = null;

    public HoldPoseCombo() {

    }

    public HoldPoseCombo(Pose poseFilter) {
        this.poseFilter = poseFilter;
    }


    public boolean matches(List<MyoPoseEvent> combo) {
        MyoPoseEvent lastPose = Iterables.getLast(combo);
        long holdTime = System.currentTimeMillis() - lastPose.getTimestamp();
        return POSE_HOLD_TIME < holdTime;
    }

    @Override
    public List<Pose> getComboChain() {
        return Collections.emptyList();
    }
}
