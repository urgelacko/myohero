package com.urgesoft.myo.states;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.urgesoft.myo.states.combo.MyoCombo;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;

import java.util.Collection;
import java.util.Map;

/**
 * Created by szabol on 2014.11.05..
 */
public class StartMyoPoseState implements MyoPoseState {

    private Map<Arm, Boolean> EMPTY_HOLD_STATE = ImmutableMap.of(Arm.RIGHT, false, Arm.LEFT, false);
    private Map<Arm, Pose> EMPTY_POSE_STATE = ImmutableMap.of(Arm.RIGHT, Pose.REST, Arm.LEFT, Pose.REST);

    long timestamp = System.currentTimeMillis();


    @Override
    public Collection<MyoCombo> getPossibleCombos() {
        return Lists.newArrayList(MyoCombo.values());
    }

    @Override
    public Map<Arm, Pose> getPoses() {
        return EMPTY_POSE_STATE;
    }

    @Override
    public Map<Arm, Boolean> getHoldState() {
        return EMPTY_HOLD_STATE;
    }

    @Override
    public boolean isHold(Arm arm) {
        return false;
    }

    @Override
    public MyoPoseState setHoldArm(Arm holdArm) {
        return this;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int getComboIndex() {
        return -1;
    }


}
