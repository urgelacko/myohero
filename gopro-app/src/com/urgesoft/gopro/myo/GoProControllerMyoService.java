package com.urgesoft.gopro.myo;

import android.content.SharedPreferences;

import com.urgesoft.gopro.controller.GoProCommand;
import com.urgesoft.gopro.event.GoProCommandEvent;
import com.urgesoft.gopro.event.GoProCommandResultEvent;
import com.urgesoft.gopro.event.GoProErrorEvent;
import com.urgesoft.gopro.event.GoProState;
import com.urgesoft.gopro.event.GoProStatus;
import com.urgesoft.gopro.event.MyoStateEvent;
import com.urgesoft.gopro.event.StateTransitionEvent;
import com.urgesoft.myo.states.combo.HoldPose;
import com.urgesoft.myo.states.combo.MyoCombo;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;

import de.greenrobot.event.EventBus;

/**
 * Created by szabol on 2014.11.01..
 */
public class GoProControllerMyoService extends AbstractMyoService implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate() {

        super.onCreate();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {

        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }

    public void onEvent(GoProCommandEvent command) {
        if (GoProCommand.LOCK.equals(command.getCommand())) {
            Hub hub = Hub.getInstance();
            for (Myo s : hub.getConnectedDevices()) {
                s.lock();
            }
        }
    }

    @Override
    protected void onPose(StateTransitionEvent stateEvent) {
        GoProCommand command = MyoAssignments.get().getCommand(stateEvent.getPose());
        checkLockAndExecute(stateEvent, command);
    }

    @Override
    protected void onHold(StateTransitionEvent stateEvent) {
        String holdPoseKey = HoldPose.assignmentKeyForPose(stateEvent.getPose());
        GoProCommand command = MyoAssignments.get().getCommand(holdPoseKey);
        checkLockAndExecute(stateEvent, command);
    }

    @Override
    protected void onCombo(StateTransitionEvent stateEvent, MyoCombo firedCombo) {
        GoProCommand command = MyoAssignments.get().getCommand(firedCombo);
        checkLockAndExecute(stateEvent, command);
    }

    private void checkLockAndExecute(StateTransitionEvent stateEvent, GoProCommand command) {

        GoProStatus goProStatus = EventBus.getDefault().getStickyEvent(GoProStatus.class);
        if (null == command || GoProState.DISCONNECTED.equals(goProStatus.getState()) || GoProState.CONNECTING.equals(goProStatus.getState())) {
            //Do nothing
            return;
        }

        if (executeLock(stateEvent, command)) {
            return;
        }

        if (stateEvent.getMyo().isUnlocked()) {
            executeCommandIfNotNull(command, stateEvent.getMyo());
        }
    }

    private boolean executeLock(StateTransitionEvent stateEvent, GoProCommand command) {
        switch (command) {
            case LOCK:
                stateEvent.getMyo().lock();
                return true;
            case TOGGLE_LOCK:
                toggleLock(stateEvent.getMyo());
                return true;
            case UNLOCK:
                unLockMyo(stateEvent.getMyo());
                return true;
        }

        return false;
    }


    @Override
    protected void onLock(Myo myo) {
        updateMyoLockState(true);
    }


    @Override
    protected void onUnlock(Myo myo) {
        updateMyoLockState(false);
    }

    private void updateMyoLockState(boolean locked) {
        MyoStateEvent currentState = MyoStateEvent.getCurrent();
        currentState.setLocked(locked);
        EventBus.getDefault().postSticky(currentState);
    }

    private void executeCommandIfNotNull(GoProCommand command, Myo myo) {
        if (null != command) {
            EventBus.getDefault().post(new GoProCommandEvent(command));
            myo.vibrate(Myo.VibrationType.SHORT);

            EventBus.getDefault().register(new VibrateOnCommandResultListener(command, myo));
        }
    }

    private static final class VibrateOnCommandResultListener {

        private GoProCommand command;
        private Myo myo;

        private VibrateOnCommandResultListener(GoProCommand command, Myo myo) {
            this.command = command;
            this.myo = myo;
        }

        public void onEventBackgroundThread(GoProCommandResultEvent event) {
            if (command.equals(event.getCommand())) {
                EventBus.getDefault().unregister(this);

                GoProStatus goProStatus = EventBus.getDefault().getStickyEvent(GoProStatus.class);
                event.getCommand().getVibrationStrategy().vibrate(myo, goProStatus);
            }
        }

        public void onEventBackgroundThread(GoProErrorEvent event) {
            if (command.equals(event.getCommand())) {
                EventBus.getDefault().unregister(this);
            }
        }
    }

}
