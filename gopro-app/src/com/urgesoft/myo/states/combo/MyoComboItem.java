package com.urgesoft.myo.states.combo;

import com.urgesoft.myo.states.MyoPoseState;
import com.urgesoft.gopro.event.StateTransitionEvent;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;

/**
 * Created by szabol on 2014.11.12..
 */
public interface MyoComboItem {

    boolean matches(StateTransitionEvent stateTransition, MyoPoseState actualState);

    Pose getPose();

    Arm getArm();

    boolean isHold();

}
