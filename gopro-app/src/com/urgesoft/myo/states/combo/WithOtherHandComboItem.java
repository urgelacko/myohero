package com.urgesoft.myo.states.combo;

import com.urgesoft.myo.states.MyoPoseState;
import com.urgesoft.gopro.event.StateTransitionEvent;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;

/**
 * Created by szabol on 2014.11.12..
 */
public class WithOtherHandComboItem implements MyoComboItem {

    private MyoComboItem decorated;
    private Pose pose;

    protected WithOtherHandComboItem(MyoComboItem decorated, Pose pose) {
        this.decorated = decorated;
        this.pose = pose;
    }

    @Override
    public boolean matches(StateTransitionEvent stateTransition, MyoPoseState actualState) {
        boolean matches = decorated.matches(stateTransition, actualState);

        Arm otherHand = Arm.LEFT.equals(stateTransition.getArm()) ? Arm.RIGHT : Arm.LEFT;

        return matches && pose.equals(actualState.getPoses().get(otherHand));
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
        return false;
    }

}
