package com.urgesoft.myo.states;

import com.urgesoft.myo.states.combo.MyoCombo;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;

import java.util.Collection;
import java.util.Map;

/**
 * Created by szabol on 2014.11.05..
 */
public interface MyoPoseState {

    Collection<MyoCombo> getPossibleCombos();

    Map<Arm, Pose> getPoses();

    Map<Arm, Boolean> getHoldState();

    boolean isHold(Arm arm);

    MyoPoseState setHoldArm(Arm holdArm);

    long getTimestamp();

    int getComboIndex();

}
