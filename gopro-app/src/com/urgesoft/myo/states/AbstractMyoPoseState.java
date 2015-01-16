package com.urgesoft.myo.states;

import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by szabol on 2014.11.05..
 */
public abstract class AbstractMyoPoseState implements MyoPoseState {

    //FIXME now myo supports actual pose
    private Map<Arm, Pose> poses = new EnumMap<Arm, Pose>(Arm.class);
    private Map<Arm, Boolean> holdSate = new EnumMap<Arm, Boolean>(Arm.class);

    private long timestamp;

    protected AbstractMyoPoseState(long timestamp) {
        this.timestamp = timestamp;
    }

    public AbstractMyoPoseState(MyoPoseState prevState, long timestamp) {
        this(timestamp);
        poses.putAll(prevState.getPoses());
        holdSate.putAll(prevState.getHoldState());
    }

    public AbstractMyoPoseState(MyoPoseState prevState) {
        this(System.currentTimeMillis());
        poses.putAll(prevState.getPoses());
        holdSate.putAll(prevState.getHoldState());
    }

    public AbstractMyoPoseState(Pose right, Pose left) {
        this(System.currentTimeMillis());
        poses = new HashMap<Arm, Pose>();
        poses.put(Arm.RIGHT, right);
        poses.put(Arm.LEFT, left);
        holdSate.put(Arm.RIGHT, false);
        holdSate.put(Arm.LEFT, false);
    }

    public AbstractMyoPoseState withPose(Arm arm, Pose newPose) {
        poses.put(arm, newPose);
        return this;
    }

    public AbstractMyoPoseState setHoldArm(Arm holdArm) {
        holdSate.put(holdArm, true);
        return this;
    }

    public AbstractMyoPoseState clearHoldArm(Arm holdArm) {
        holdSate.put(holdArm, false);
        return this;
    }

    @Override
    public Map<Arm, Boolean> getHoldState() {
        return holdSate;
    }

    @Override
    public boolean isHold(Arm arm) {
        return holdSate.get(arm);
    }


    @Override
    public Map<Arm, Pose> getPoses() {
        return poses;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }


}
