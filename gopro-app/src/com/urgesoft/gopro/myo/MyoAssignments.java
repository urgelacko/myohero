package com.urgesoft.gopro.myo;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.urgesoft.gopro.controller.GoProCommand;
import com.urgesoft.myo.states.combo.HoldPose;
import com.urgesoft.myo.states.combo.MyoCombo;
import com.thalmic.myo.Pose;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by szabol on 2014.11.16..
 */
public class MyoAssignments {

    private static final MyoAssignments INSTANCE = new MyoAssignments();

    private Map<String, GoProCommand> assignments;

    private MyoAssignments() {
        assignments = Collections.emptyMap();
    }

    public GoProCommand getCommand(Pose pose) {
        return assignments.get(pose.name());
    }

    public GoProCommand getCommand(MyoCombo combo) {
        return assignments.get(combo.name());
    }

    public GoProCommand getCommand(String key) {
        return assignments.get(key);
    }

    public static MyoAssignments get() {
        return INSTANCE;
    }

    public static MyoAssignments createDefault() {

        MyoAssignments myoAssignments = new MyoAssignments();

        myoAssignments.assignments = new HashMap<String, GoProCommand>();
        myoAssignments.assignments.put(MyoCombo.DOUBLE_TAP_FIST_HOLD.toString(), GoProCommand.TOGGLE_LOCK);
        myoAssignments.assignments.put(HoldPose.FINGERS_SPREAD.toString(), GoProCommand.TOGGLE_RECORD);

        return myoAssignments;
    }

    public void setAssignments(String assignmentString) {
        Gson gson = new Gson();
        MyoAssignments myoAssignments = gson.fromJson(assignmentString, MyoAssignments.class);
        assignments = myoAssignments.assignments;
    }

    public void clearAssignment(String pose) {
        assignments.remove(pose);
    }

    public void setAssignment(String pose, GoProCommand command) {
        if (assignments.containsKey(pose)) {
            assignments.remove(pose);
        }
        assignments.put(pose, command);
    }


    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public Map<String, GoProCommand> getAssignments() {
        return assignments;
    }


    public String getPoseKey(final GoProCommand command) {
        final Map<String, GoProCommand> currentAssignment = Maps.filterValues(assignments, new Predicate<GoProCommand>() {
            @Override
            public boolean apply(GoProCommand input) {
                return input.equals(command);
            }
        });

        return Iterables.getFirst(currentAssignment.keySet(), AssignableMyoPoseKeys.NONE);
    }


}
