package com.urgesoft.myo.states.combo;

import com.urgesoft.myo.states.MyoPoseState;
import com.urgesoft.gopro.event.StateTransitionEvent;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;

/**
 * Created by szabol on 2014.11.12..
 */
public class DefaultMyoComboItem implements MyoComboItem {

    private Pose pose;
    private Arm arm;
    private boolean hold;

    protected DefaultMyoComboItem(Pose pose) {
        this(pose, null);
    }

    protected DefaultMyoComboItem(Pose pose, Arm arm) {
        this.pose = pose;
        this.arm = arm;
    }

    @Override
    public boolean matches(StateTransitionEvent stateTransition, MyoPoseState actualState) {
        return !stateTransition.isHold() && pose.equals(stateTransition.getPose()) && isOnFilteredArm(stateTransition.getArm());
    }

    @Override
    public Pose getPose() {
        return pose;
    }

    @Override
    public Arm getArm() {
        return arm;
    }

    @Override
    public boolean isHold() {
        return false;
    }


    private boolean isOnFilteredArm(Arm newStateArm) {
        return null == arm || arm.equals(newStateArm);
    }


}
