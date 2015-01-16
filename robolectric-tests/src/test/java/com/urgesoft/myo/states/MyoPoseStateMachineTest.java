package com.urgesoft.myo.states;

import com.google.common.collect.Iterables;

import com.urgesoft.myo.RobolectricGradleTestRunner;
import com.urgesoft.myo.states.combo.MyoCombo;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;
import com.urgesoft.gopro.event.StateTransitionEvent;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/**
 * Created by szabol on 2014.11.11..
 */
@RunWith(RobolectricGradleTestRunner.class)
public class MyoPoseStateMachineTest {

    private MyoPoseStateMachine fsm;

    private StateTransitionEvent partialCombo;
    private StateTransitionEvent fullCombo;
    private StateTransitionEvent hold;
    private StateTransitionEvent basicPose;


    @Before
    public void init() {
        partialCombo = null;
        fullCombo = null;
        hold = null;
        basicPose = null;

        fsm = new MyoPoseStateMachine() {
            @Override
            protected void onHoldState(StateTransitionEvent stateEvent) {
                hold = stateEvent;
            }

            @Override
            protected void onPossibleComboState(StateTransitionEvent stateEvent, MyoPoseState newState) {
                partialCombo = stateEvent;
            }

            @Override
            protected void onComboState(StateTransitionEvent stateEvent, MyoCombo combo) {
                fullCombo = stateEvent;
            }

            @Override
            protected void onBasicState(StateTransitionEvent stateEvent) {
                basicPose = stateEvent;
            }
        };
    }

    @Test
    public void test_initial() {
        assertEquals(Pose.REST, fsm.getState().getPoses().get(Arm.RIGHT));
        assertEquals(Pose.REST, fsm.getState().getPoses().get(Arm.LEFT));
    }

    @Test
    public void test_basic_right_arm() {

        Arm testedArm = Arm.RIGHT;

        fsm.newPose(new StateTransitionEvent().withPose(Pose.DOUBLE_TAP).onArm(testedArm));

        assertNotNull(basicPose);
        assertEquals(testedArm, basicPose.getArm());
        assertEquals(Pose.DOUBLE_TAP, basicPose.getPose());

        assertEquals(Pose.DOUBLE_TAP, fsm.getState().getPoses().get(testedArm));
        assertEquals(Pose.REST, fsm.getState().getPoses().get(Arm.LEFT));

    }

    @Test
    public void test_basic_right_arm_and_left_arm() {

        fsm.newPose(new StateTransitionEvent().withPose(Pose.FINGERS_SPREAD).onArm(Arm.RIGHT));
        fsm.newPose(new StateTransitionEvent().withPose(Pose.DOUBLE_TAP).onArm(Arm.LEFT));

        assertNotNull(basicPose);
        assertEquals(Arm.LEFT, basicPose.getArm());
        assertEquals(Pose.DOUBLE_TAP, basicPose.getPose());

        assertEquals(Pose.FINGERS_SPREAD, fsm.getState().getPoses().get(Arm.RIGHT));
        assertEquals(Pose.DOUBLE_TAP, fsm.getState().getPoses().get(Arm.LEFT));
    }

    @Test
    @Ignore
    public void test_hold_right_arm() throws InterruptedException {

        //Wait for the hold time to expire
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                fsm.newPose(new StateTransitionEvent().withPose(Pose.FINGERS_SPREAD).onArm(Arm.RIGHT));
            }
        });
        Thread.sleep(5000);

        assertNotNull(hold);
        assertEquals(Arm.RIGHT, hold.getArm());
        assertEquals(Pose.FINGERS_SPREAD, hold.getPose());

        assertEquals(Pose.FINGERS_SPREAD, fsm.getState().getPoses().get(Arm.RIGHT));
        assertEquals(Pose.REST, fsm.getState().getPoses().get(Arm.LEFT));
    }

    @Test
    public void test_combo_right_arm() throws InterruptedException {

        for (com.urgesoft.myo.states.combo.MyoComboItem pose : MyoCombo.FIST_FINGERS_SPREAD_FIST.getComboChain()) {
            fsm.newPose(new StateTransitionEvent().withPose(pose.getPose()).onArm(convertNullArmToRight(pose)));
        }

        assertNotNull(fullCombo);
        com.urgesoft.myo.states.combo.MyoComboItem lastComboPose = Iterables.getLast(MyoCombo.FIST_FINGERS_SPREAD_FIST.getComboChain());
        assertEquals(convertNullArmToRight(lastComboPose), fullCombo.getArm());
        assertEquals(lastComboPose.getPose(), fullCombo.getPose());

        assertEquals(Pose.REST, fsm.getState().getPoses().get(Arm.RIGHT));
        assertEquals(Pose.REST, fsm.getState().getPoses().get(Arm.LEFT));
    }


    @Test
    public void test_partial_combo_right_arm() throws InterruptedException {

        com.urgesoft.myo.states.combo.MyoComboItem comboFirstPose = Iterables.getFirst(MyoCombo.FIST_FINGERS_SPREAD_FIST.getComboChain(), null);

        fsm.newPose(new StateTransitionEvent().withPose(comboFirstPose.getPose()).onArm(Arm.RIGHT));

        assertNotNull(partialCombo);
        assertEquals(Arm.RIGHT, partialCombo.getArm());
        assertEquals(comboFirstPose.getPose(), partialCombo.getPose());

        assertEquals(comboFirstPose.getPose(), fsm.getState().getPoses().get(Arm.RIGHT));
        assertEquals(Pose.REST, fsm.getState().getPoses().get(Arm.LEFT));
    }

    private Arm convertNullArmToRight(com.urgesoft.myo.states.combo.MyoComboItem pose) {
        return null != pose.getArm() ? pose.getArm() : Arm.RIGHT;
    }

}
