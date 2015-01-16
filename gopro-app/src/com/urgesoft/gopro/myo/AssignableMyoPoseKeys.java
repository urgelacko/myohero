package com.urgesoft.gopro.myo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.urgesoft.myo.states.combo.HoldPose;
import com.urgesoft.myo.states.combo.MyoCombo;
import com.thalmic.myo.Pose;

import java.util.List;

/**
 * Created by szabol on 2014.11.18..
 */
public class AssignableMyoPoseKeys {

    public static final String NONE = "NONE";

    private static final List<Object> POSE_KEYS = ImmutableList.<Object>builder()
            .add(NONE)
            .add(Pose.DOUBLE_TAP)
            .add(Pose.FINGERS_SPREAD)
            .add(Pose.WAVE_IN)
            .add(Pose.WAVE_OUT)
            .add(Pose.FIST)
             .addAll(Lists.newArrayList(HoldPose.values()))
            .build();

    private static final List<Object> POSE_KEYS_WITH_COMBO = ImmutableList.builder()
            .addAll(POSE_KEYS)
            .addAll(Lists.newArrayList(MyoCombo.values()))
            .build();

    private AssignableMyoPoseKeys() {

    }

    public static List<Object> noCombo() {
        return POSE_KEYS;
    }

    public static List<Object> withCombo() {
        return POSE_KEYS_WITH_COMBO;
    }

}
