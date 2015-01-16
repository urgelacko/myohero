package com.urgesoft.myo.states;

import android.os.Handler;
import android.util.Log;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.urgesoft.gopro.event.StateTransitionEvent;
import com.urgesoft.myo.states.combo.ComboMatchType;
import com.urgesoft.myo.states.combo.MyoCombo;
import com.urgesoft.myo.states.combo.MyoComboItem;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by szabol on 2014.11.05..
 */
public class MyoPoseStateMachine {

    private static final String TAG = "MyoPoseStateMachine";

    public static final int DEFAULT_POSE_HOLD_TIME = 1200;
    public static final int DEFAULT_POSE_COMBO_EXPIRE_TIME = 550;


    private int poseHoldTime = DEFAULT_POSE_HOLD_TIME;
    private int comboThresholdTime = DEFAULT_POSE_COMBO_EXPIRE_TIME;

    private Handler comboExpireHandler = new Handler();
    private Map<Arm, Handler> holdTimeHandlers = ImmutableMap.of(Arm.RIGHT, new Handler(), Arm.LEFT, new Handler());

    private MyoPoseState state = new StartMyoPoseState();

    public void destroy() {
        removeThreads();
    }

    public void reset() {
        removeThreads();
        setState(new StartMyoPoseState());
    }

    private void removeThreads() {
        comboExpireHandler.removeCallbacksAndMessages(null);
        getHoldTimeHandlerForArm(Arm.RIGHT).removeCallbacksAndMessages(null);
        getHoldTimeHandlerForArm(Arm.LEFT).removeCallbacksAndMessages(null);
    }

    public void newPose(StateTransitionEvent req) {

        Preconditions.checkNotNull(req.getArm(), "The arm is mandatory!");
        Preconditions.checkArgument(!Arm.UNKNOWN.equals(req.getArm()), "Cannot handle state for UNKNOWN arm!");
        Log.v(TAG, String.format("New incoming pose transition. Shutting down scheduled thread handlers! pose:[%s], arm:[%s]", req.getPose(), req.getArm()));

        MyoPoseState actualState = getState();
        if (Pose.REST.equals(req.getPose()) && actualState.getPoses().get(req.getArm()).equals(req.getPose())) {
            Log.v(TAG, String.format("Duplicated pose! pose:[%s], arm:[%s]", req.getPose(), req.getArm()));
            return;
        }

        //Stop the hold time handler for the arm
        getHoldTimeHandlerForArm(req.getArm()).removeCallbacksAndMessages(null);
        //Stop the combo time expired handler for the arm
        comboExpireHandler.removeCallbacksAndMessages(null);

        Collection<MyoCombo> possibleCombos = actualState.getPossibleCombos();

        List<MyoCombo> nextPossibleCombos = new ArrayList<MyoCombo>();
        int nextComboIndex = getNextComboIndex();
        boolean wasPossibleComboWithNextHold = false;
        //This way hold poses can only be handled as last combo items but the REST after a hold pose is immediately clears the hold
        if (Pose.REST.equals(req.getPose()) && !req.isHold()) {
            nextPossibleCombos.addAll(possibleCombos);
            nextComboIndex = actualState.getComboIndex();
        } else {
            for (MyoCombo combo : possibleCombos) {
                ComboMatchType comboMatchType = checkComboMatch(req, combo, nextComboIndex);
                switch (comboMatchType) {
                    case FUll_MATCH:
                        //If found a full match fire the combo event and stop the execution cause we don't need hold state and other state transition checks
                        transitionToCompleteComboState(req, combo);
                        return;
                    //If partial match was found and the next combo item is hold we turn the flag true cause in that case we need to check for combo expiration time later than hold time
                    case PARTIAL_MATCH_NEXT_IS_HOLD:
                        wasPossibleComboWithNextHold = true;
                    case PARTIAL_MATCH:
                        //On partial match we collect the possible combos
                        nextPossibleCombos.add(combo);
                        break;
                }
            }
            //If no continuable chain was found and we are deeper in the chain check it from the beginning
            if (nextPossibleCombos.isEmpty() && nextComboIndex > 0) {
                //TODO refacotor and merge the switches
                for (MyoCombo combo : MyoCombo.values()) {
                    ComboMatchType comboMatchType = checkComboMatch(req, combo, 0);
                    switch (comboMatchType) {
                        //If partial match was found and the next combo item is hold we turn the flag true cause in that case we need to check for combo expiration time later than hold time
                        case PARTIAL_MATCH_NEXT_IS_HOLD:
                            wasPossibleComboWithNextHold = true;
                        case PARTIAL_MATCH:
                            //On partial match we collect the possible combos
                            nextPossibleCombos.add(combo);
                            break;
                    }
                }
            }
        }

        gotoNextState(req, nextPossibleCombos, wasPossibleComboWithNextHold, nextComboIndex);
        //start the hold state transition thread
        if (!req.isHold()) {
            getHoldTimeHandlerForArm(req.getArm()).postDelayed(new CheckPoseHoldRunnable(req), poseHoldTime);
        }
    }

    private Handler getHoldTimeHandlerForArm(Arm arm) {
        return holdTimeHandlers.get(arm);
    }


    private void gotoNextState(StateTransitionEvent req, List<MyoCombo> nextPossibleCombos, boolean wasPossibleHoldComboOnNext, int nextComboIndex) {

        //If no possible combos was found go to basic state
        if (req.isHold()) {
            //If no possible combos and the state transition is not a pose hold call basic hook otherwise hold one
            transitionToHoldState(req, nextPossibleCombos, nextComboIndex);
        } else if (nextPossibleCombos.isEmpty()) {
            String logMsg = String.format("No possible combos. Transition to basic state: arm:[%s], pose:[%s] -> [%s]", req.getArm(), getState().getPoses().get(req.getArm()), req.getPose());
            Log.d(TAG, logMsg);

            transitionToBasicState(req);
        } else {
            //if we have found possible combos then go to a possible combo state
            transitionToPossibleComboState(req, nextComboIndex, nextPossibleCombos, wasPossibleHoldComboOnNext);
        }
    }

    /**
     * STATE TRANSITIONS
     */
    private void transitionToPossibleComboState(StateTransitionEvent req, int nextComboIndex, List<MyoCombo> nextPossibleCombos, boolean wasHoldPossibleNextComboItem) {

        MyoPoseState actualState = getState();
        String logMsg = String.format("Possible combos was found! Transition to possible combo state: arm:[%s], pose:[%s] -> [%s], combos:[%s]", req.getArm(),
                actualState.getPoses().get(req.getArm()), req.getPose(), Joiner.on(",").join(nextPossibleCombos));
        Log.d(TAG, logMsg);

        MyoPoseState newState;
        newState = new ComboMyoPoseState(actualState, nextComboIndex, nextPossibleCombos).withPose(req.getArm(), req.getPose()).clearHoldArm(req.getArm());
        //HOOK
        onPossibleComboState(req, newState);


        //If there was a possible combo with hold on the next combo index we need to wait longer than the hold check time
        long combExpireTime = comboThresholdTime > 0 && wasHoldPossibleNextComboItem ? getPoseHoldTime() + 100 : comboThresholdTime;
        comboExpireHandler.postDelayed(new ExpirePossibleComboStateTask(req), combExpireTime);

        //We do not handle REST in combos
        setState(newState);
    }

    private void transitionToBasicState(StateTransitionEvent req) {

        Log.d(TAG, String.format("Transition to basic state: arm:[%s], pose:[%s]  ", req.getArm(), req.getPose()));

        MyoPoseState newState = new BasicMyoPoseState(getState(), req.getTimestamp()).withPose(req.getArm(), req.getPose()).clearHoldArm(req.getArm());
        setState(newState);
        //HOOK
        onBasicState(req);
    }


    private void transitionToCompleteComboState(StateTransitionEvent req, MyoCombo combo) {

        Log.d(TAG, String.format("Combo fired! Resetting state. arm:[%s], combo:[%s]", req.getArm(), combo));

        //Reset both arms on a fired combo
        reset();
        //HOOK
        //FIXME each item should know its own arm if mixed arm combos are possible
        onComboState(req, combo);
    }

    private void transitionToHoldState(StateTransitionEvent req, List<MyoCombo> nextPossibleCombos, int nextComboIndex) {

        Log.d(TAG, String.format("Transition to hold state. arm:[%s], pose:[%s]", req.getArm(), req.getPose()));

        MyoPoseState actualState = getState();

        MyoPoseState newState;
        if (nextPossibleCombos.isEmpty()) {
            newState = new BasicMyoPoseState(actualState, req.getTimestamp()).withPose(req.getArm(), req.getPose()).setHoldArm(req.getArm());
        } else {
            newState = new ComboMyoPoseState(actualState, nextComboIndex, nextPossibleCombos).withPose(req.getArm(), req.getPose()).setHoldArm(req.getArm());
        }
        setState(newState);

        //HOOK
        onHoldState(req);
    }


    private ComboMatchType checkComboMatch(StateTransitionEvent req, MyoCombo combo, int nextIndex) {

        List<MyoComboItem> comboChain = combo.getComboChain();

        //Handle out of the bounds
        MyoComboItem nextComboItem = comboChain.size() <= nextIndex ? null : comboChain.get(nextIndex);
        //If we have a next combo item AND (nextItem is the new pose OR we are in a started combo chain and the new pose is REST) we handle it as matches.
        //Like this the REST pose is not matters in a combo state
        boolean matches = null != nextComboItem && (nextComboItem.matches(req, getState()));
        if (!matches) {
            return ComboMatchType.NO_MATCH;
        }

        //If the matched element is the last element in the combo chain its a full match
        if (matches && comboChain.size() == nextIndex + 1) {
            return ComboMatchType.FUll_MATCH;
        }

        //If the next item is hold its a special partial match type
        if (matches && comboChain.get(nextIndex + 1).isHold()) {
            return ComboMatchType.PARTIAL_MATCH_NEXT_IS_HOLD;
        } else {
            return ComboMatchType.PARTIAL_MATCH;
        }


    }


    private int getNextComboIndex() {
        return getState().getComboIndex() + 1;
    }

    public synchronized MyoPoseState getState() {
        return state;
    }

    private synchronized void setState(MyoPoseState state) {
        this.state = state;
    }

    public long getPoseHoldTime() {
        return poseHoldTime;
    }

    public long getComboThresholdTime() {
        return comboThresholdTime;
    }

    public void setPoseHoldTime(int poseHoldTime) {
        Preconditions.checkArgument(poseHoldTime > comboThresholdTime, "The pose hold time must be greater than combo threshold time");
        this.poseHoldTime = poseHoldTime;
    }

    public void setComboThresholdTime(int comboThresholdTime) {
        Preconditions.checkArgument(poseHoldTime > comboThresholdTime, "The comboThresholdTime time must be lower than pose hold time time");
        this.comboThresholdTime = comboThresholdTime;
    }

    private class ExpirePossibleComboStateTask implements Runnable {

        private StateTransitionEvent req;

        public ExpirePossibleComboStateTask(StateTransitionEvent req) {
            this.req = req;
        }

        @Override
        public void run() {
            //On combo threshold expired we go to a basic state with the last combo member pose and its timestamp.
            MyoPoseState actualState = getState();
            String logMsg = String.format("Combo threshold expired! arm:[%s], pose:[%s]", req.getArm(), req.getPose());
            Log.v(TAG, logMsg);

            //If the combo threshold expires in a REST state after a combo's first item we should fire the first pose in the combo
            // but if the combo fails deeper in the chain we do not fire anything
            if (Pose.REST.equals(actualState.getPoses().get(req.getArm())) && actualState.getComboIndex() == 0) {
                MyoCombo aCombo = Iterables.getLast(actualState.getPossibleCombos());

                onBasicState(req.withPose(aCombo.getComboChain().get(0).getPose()));
                req.withPose(Pose.REST);
            }
            transitionToBasicState(req.at(actualState.getTimestamp()));

            onComboExpired(req, actualState.getComboIndex(), actualState.getPossibleCombos());
        }


    }

    private class CheckPoseHoldRunnable implements Runnable {

        private StateTransitionEvent req;

        public CheckPoseHoldRunnable(StateTransitionEvent req) {
            this.req = req;
        }

        @Override
        public void run() {
            //On hold we reset the hold arm to REST state
            Log.v(TAG, String.format("Hold threshold changed! arm:[%s], pose:[%s]", req.getArm(), req.getPose()));

            newPose(req.setHold(true));
        }

    }

    /**
     * HOOKS
     */
    protected void onPossibleComboState(StateTransitionEvent stateEvent, MyoPoseState newState) {
    }

    protected void onBasicState(StateTransitionEvent stateEvent) {
    }

    protected void onComboState(StateTransitionEvent stateEvent, MyoCombo combo) {
    }

    protected void onHoldState(StateTransitionEvent stateEvent) {
    }

    protected void onComboExpired(StateTransitionEvent req, int comboIndex, Collection<MyoCombo> possibleCombos) {
    }

}


