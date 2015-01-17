package com.urgesoft.gopro.myo;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.XDirection;
import com.urgesoft.gopro.R;
import com.urgesoft.gopro.event.MyoComboEvent;
import com.urgesoft.gopro.event.MyoErrorEvent;
import com.urgesoft.gopro.event.MyoPoseEvent;
import com.urgesoft.gopro.event.MyoStateEvent;
import com.urgesoft.gopro.event.StateTransitionEvent;
import com.urgesoft.gopro.ui.fragment.SettingsFragment;
import com.urgesoft.myo.states.MyoPoseState;
import com.urgesoft.myo.states.MyoPoseStateMachine;
import com.urgesoft.myo.states.combo.MyoCombo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public abstract class AbstractMyoService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MyoService";


    private Map<String, Arm> armByMyoMac = new HashMap<String, Arm>();

    private MyoServiceStateMachine stateMachine = new MyoServiceStateMachine();
    private boolean autolock = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventBus.getDefault().postSticky(new MyoStateEvent(false));

//BluetoothAdapter ad;
//        ad.

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onCreate() {

        super.onCreate();

        //MYO init
        if (initMyo()) {
            return;
        }

        //SETTINGS
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        //Autolock
        updateAutolockFlag(settings);
        //Listener
        settings.registerOnSharedPreferenceChangeListener(this);
        //Hold time
        String defaultHoldTime = Integer.toString(MyoPoseStateMachine.DEFAULT_POSE_HOLD_TIME);
        if (!settings.contains(SettingsFragment.KEY_MYO_POSE_HOLD_TIME)) {
            settings.edit().putString(SettingsFragment.KEY_MYO_POSE_HOLD_TIME, defaultHoldTime).commit();
        }
        String holdTimeSetting = settings.getString(SettingsFragment.KEY_MYO_POSE_HOLD_TIME, defaultHoldTime);
        stateMachine.setPoseHoldTime(Integer.parseInt(holdTimeSetting));
        //ComboExpire
        updateComboExpireFromPreferences(settings);

        //Command assignments
        String commandAssignmentSetting = settings.getString(SettingsFragment.KEY_MYO_COMMAND_ASSIGNMENTS, MyoAssignments.createDefault().toString());
        MyoAssignments.get().setAssignments(commandAssignmentSetting);
    }

    private boolean initMyo() {
        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            EventBus.getDefault().post(new MyoErrorEvent("Could not initialize myo hub!"));
            stopSelf();

            return true;
        }
        hub.setLockingPolicy(Hub.LockingPolicy.NONE);
        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);

        // If bt is enabled scan for Myo devices and connect the first
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.isEnabled()) {
            hub.attachToAdjacentMyo();
        }

        return false;
    }

    @Override
    public void onDestroy() {

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.unregisterOnSharedPreferenceChangeListener(this);

        settings.edit().putString(SettingsFragment.KEY_MYO_COMMAND_ASSIGNMENTS, MyoAssignments.get().toString()).commit();

        stateMachine.destroy();

        // We don't want any callbacks when the Service is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);
        Hub.getInstance().shutdown();
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        btAdapter.cancelDiscovery();

        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsFragment.KEY_MYO_COMBO_EXPIRE_TIME)) {
            String stringParam = sharedPreferences.getString(key, getComboExpireDefaultValue());
            stateMachine.setComboThresholdTime(Integer.valueOf(stringParam));
        } else if (key.equals(SettingsFragment.KEY_MYO_POSE_HOLD_TIME)) {
            String stringParam = sharedPreferences.getString(key, Integer.toString(MyoPoseStateMachine.DEFAULT_POSE_HOLD_TIME));
            stateMachine.setPoseHoldTime(Integer.valueOf(stringParam));
        } else if (key.equals(SettingsFragment.KEY_MYO_COMBO_ENABLED)) {
            updateComboExpireFromPreferences(sharedPreferences);
        } else if (key.equals(SettingsFragment.KEY_MYO_AUTOLOCK_ENABLED)) {
            updateAutolockFlag(sharedPreferences);
        }
    }

    private void updateAutolockFlag(SharedPreferences sharedPreferences) {
        autolock = sharedPreferences.getBoolean(SettingsFragment.KEY_MYO_AUTOLOCK_ENABLED, false);
    }

    private String getComboExpireDefaultValue() {
        String[] stringArray = getResources().getStringArray(R.array.myo_combo_expire_durationValues);
        return stringArray[0];
    }

    private void updateComboExpireFromPreferences(SharedPreferences sharedPreferences) {
        if (!sharedPreferences.getBoolean(SettingsFragment.KEY_MYO_COMBO_ENABLED, true)) {
            stateMachine.setComboThresholdTime(0);
        } else {
            String stringParam = sharedPreferences.getString(SettingsFragment.KEY_MYO_COMBO_EXPIRE_TIME, getComboExpireDefaultValue());
            stateMachine.setComboThresholdTime(Integer.valueOf(stringParam));
        }
    }

    protected void toggleLock(Myo myo) {
        if (myo.isUnlocked()) {
            myo.lock();
        } else {
            unLockMyo(myo);
        }
    }

    protected void unLockMyo(Myo myo) {
        myo.unlock(autolock ? Myo.UnlockType.TIMED : Myo.UnlockType.HOLD);
    }


    protected MyoStateEvent getMyoState() {
        return MyoStateEvent.getCurrent();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * For onPeriodic() callback
     */

    private DeviceListener mListener = new AbstractDeviceListener() {

        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {

            Log.d(TAG, String.format("Myo arm synced: arm:[%s]", arm.name()));

            super.onArmSync(myo, timestamp, arm, xDirection);

            if (!Arm.UNKNOWN.equals(arm)) {
                armByMyoMac.put(myo.getMacAddress(), arm);
                onMyoConnect(myo, timestamp);

                EventBus.getDefault().postSticky(new MyoStateEvent(true));
            }
        }

        @Override
        public void onArmUnsync(Myo myo, long timestamp) {

            Log.d(TAG, String.format("Myo arm unSynced: arm:[%s]", armByMyoMac.get(myo.getMacAddress())));

            onMyoNotReady(myo, timestamp);

            super.onArmUnsync(myo, timestamp);
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp) {

            Log.d(TAG, String.format("Myo disconnected! arm:[%s]", armByMyoMac.get(myo.getMacAddress())));

            onMyoNotReady(myo, timestamp);

            super.onDisconnect(myo, timestamp);
        }

        private void onMyoNotReady(Myo myo, long timestamp) {

            Log.d(TAG, String.format("Myo not ready! arm:[%s]", armByMyoMac.get(myo.getMacAddress())));

            armByMyoMac.remove(myo.getMacAddress());
            if (armByMyoMac.isEmpty()) {
                onMyoDisconnect(myo, timestamp);
                EventBus.getDefault().postSticky(new MyoStateEvent(false));
            }
        }

        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {

            Log.v(TAG, "New Myo pose: " + pose.name());

            if (Pose.UNKNOWN.equals(pose)) {
                return;
            }

            //Sonmetimes the arm is UNKNOWN. Then myo needs reset
            Arm arm = armByMyoMac.get(myo.getMacAddress());
            if (null == arm) {
                Log.e(TAG, String.format("Arm is not found for myo. Reset state to not ready! myo mac:[%s], arm:[%s]", myo.getMacAddress(), armByMyoMac.get(myo.getMacAddress())));
                onMyoNotReady(myo, timestamp);
                return;
            }

            //If autolock is enabled and Myo is unlocked just extend the unlock time
            if (myo.isUnlocked() && autolock) {
                myo.unlock(Myo.UnlockType.TIMED);
            }

            StateTransitionEvent newStateReq = new StateTransitionEvent().onArm(arm).onMyo(myo).withPose(pose);
            stateMachine.newPose(newStateReq);
        }

        @Override
        public void onLock(Myo myo, long timestamp) {
            AbstractMyoService.this.onLock(myo);
        }

        @Override
        public void onUnlock(Myo myo, long timestamp) {
            AbstractMyoService.this.onUnlock(myo);
        }
    };


    private class MyoServiceStateMachine extends MyoPoseStateMachine {

        @Override
        protected void onBasicState(StateTransitionEvent stateEvent) {

            //Post to event bus
            EventBus.getDefault().post(new MyoPoseEvent(stateEvent.getPose(), stateEvent.getArm(), stateEvent.getTimestamp()));

            onPose(stateEvent);
        }

        @Override
        protected void onComboState(StateTransitionEvent stateEvent, MyoCombo combo) {

            EventBus.getDefault().post(new MyoComboEvent(combo, stateEvent.getArm(), stateEvent.getTimestamp()));

            onCombo(stateEvent, combo);
        }

        @Override
        protected void onHoldState(StateTransitionEvent stateEvent) {

            EventBus.getDefault().post(new MyoPoseEvent(stateEvent.getPose(), stateEvent.getArm(), stateEvent.getTimestamp()).setHold(true));

            onHold(stateEvent);
        }

        @Override
        protected void onPossibleComboState(StateTransitionEvent stateEvent, MyoPoseState newState) {
            onIncompleteCombo(stateEvent, newState.getPossibleCombos());
        }

    }

    /**
     * HOOK METHODS
     */
    protected void onMyoConnect(Myo myo, long timestamp) {
    }

    protected void onMyoDisconnect(Myo myo, long timestamp) {
    }

    protected void onPose(StateTransitionEvent stateEvent) {
    }

    protected void onHold(StateTransitionEvent stateEvent) {
    }

    protected void onCombo(StateTransitionEvent stateEvent, MyoCombo firedCombo) {
    }

    protected void onIncompleteCombo(StateTransitionEvent stateEvent, Collection<MyoCombo> possibleCombos) {
    }

    protected void onLock(Myo myo) {
    }

    protected void onUnlock(Myo myo) {
    }

    protected Map<String, Arm> getArmByMyoMac() {
        return armByMyoMac;
    }
}
