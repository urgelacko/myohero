package com.urgesoft.myo.states.combo;

import com.google.common.collect.ImmutableList;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;

import java.util.List;

/**
 * Created by szabol on 2014.11.03..
 */
public enum MyoCombo {

    DOUBLE_FIST(sameHand(Pose.FIST), sameHand(Pose.FIST)),
    DOUBLE_FINGERS_SPREAD(sameHand(Pose.FINGERS_SPREAD), sameHand(Pose.FINGERS_SPREAD)),
    TRIPLE_WAVE_IN(sameHand(Pose.WAVE_IN), sameHand(Pose.WAVE_IN), sameHand(Pose.WAVE_IN)),
    TRIPLE_WAVE_OUT(sameHand(Pose.WAVE_OUT), sameHand(Pose.WAVE_OUT), sameHand(Pose.WAVE_OUT)),
    WAVE_IN_WAVE_OUT(sameHand(Pose.WAVE_IN), sameHand(Pose.WAVE_OUT)),
    WAVE_OUT_WAVE_IN(sameHand(Pose.WAVE_OUT), sameHand(Pose.WAVE_IN)),
    FIST_FINGERS_SPREAD_FIST(sameHand(Pose.FIST), sameHand(Pose.FINGERS_SPREAD), sameHand(Pose.FIST)),
    FINGERS_SPREAD_FIST_FINGERS_SPREAD(sameHand(Pose.FINGERS_SPREAD), sameHand(Pose.FIST), sameHand(Pose.FINGERS_SPREAD)),
    WAVE_IN_FINGERS_SPREAD_WAVE_OUT(sameHand(Pose.WAVE_IN), sameHand(Pose.FINGERS_SPREAD), sameHand(Pose.WAVE_OUT)),
    WAVE_IN_FIST_WAVE_OUT(sameHand(Pose.WAVE_IN), sameHand(Pose.FIST), sameHand(Pose.WAVE_OUT)),
    DOUBLE_TAP_FINGERS_SPREAD_HOLD(sameHand(Pose.DOUBLE_TAP), sameHand(Pose.FINGERS_SPREAD), sameHandHold(Pose.FINGERS_SPREAD)),
    DOUBLE_TAP_FIST_HOLD(sameHand(Pose.DOUBLE_TAP), sameHand(Pose.FIST), sameHandHold(Pose.FIST)),
    DOUBLE_TAP_WAVE_IN_HOLD(sameHand(Pose.DOUBLE_TAP), sameHand(Pose.WAVE_IN), sameHandHold(Pose.WAVE_IN)),
    DOUBLE_TAP_WAVE_OUT_HOLD(sameHand(Pose.DOUBLE_TAP), sameHand(Pose.WAVE_OUT), sameHandHold(Pose.WAVE_OUT)),
    DOUBLE_TAP_WAVE_IN_WAVE_OUT_HOLD(sameHand(Pose.DOUBLE_TAP), sameHand(Pose.WAVE_IN), sameHand(Pose.WAVE_OUT), sameHandHold(Pose.WAVE_OUT)),
    DOUBLE_TAP_WAVE_OUT_WAVE_IN_HOLD(sameHand(Pose.DOUBLE_TAP), sameHand(Pose.WAVE_OUT), sameHand(Pose.WAVE_IN), sameHandHold(Pose.WAVE_IN));

    private List<MyoComboItem> comboChain;

    private MyoCombo(MyoComboItem... items) {
        comboChain = ImmutableList.copyOf(items);
    }

    public List<MyoComboItem> getComboChain() {
        return comboChain;
    }

    /**
     * FACTORY METHODS
     */
    public static MyoComboItem sameHand(Pose pose) {
        return new DefaultMyoComboItem(pose);
    }

    public static MyoComboItem sameHandHold(Pose pose) {
        return new HoldMyoComboItemDecorator(sameHand(pose));
    }

    public static MyoComboItem onHand(Pose pose, Arm arm) {
        return new DefaultMyoComboItem(pose, arm);
    }

    public static MyoComboItem twoHands(Pose pose, Arm arm, Pose poseOnOtherHand) {
        return new WithOtherHandComboItem(onHand(pose, arm), poseOnOtherHand);
    }


}
