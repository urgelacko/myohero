package com.urgesoft.myo.states.combo;

import com.urgesoft.myo.states.MyoPoseState;
import com.urgesoft.gopro.event.StateTransitionEvent;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;

/**
 * Created by szabol on 2014.11.12..
 */
public class HoldMyoComboItemDecorator implements MyoComboItem {

    private MyoComboItem decorated;

    protected HoldMyoComboItemDecorator(MyoComboItem decorated) {
        this.decorated = decorated;
    }

    @Override
    public boolean matches(StateTransitionEvent stateTransition, MyoPoseState actualState) {
        return stateTransition.isHold() && decorated.getPose().equals(stateTransition.getPose()) && isOnFilteredArm(stateTransition.getArm());
    }

    private boolean isOnFilteredArm(Arm newStateArm) {
        return null == decorated.getArm() || decorated.getArm().equals(newStateArm);
    }

    @Override
    public Pose getPose() {
        return decorated.getPose();
    }

    @Override
    public Arm getArm() {
        return decorated.getArm();
    }

    @Override
    public boolean isHold() {
        return true;
    }


}
