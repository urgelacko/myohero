package com.urgesoft.gopro.event;

import com.urgesoft.myo.states.combo.MyoCombo;
import com.thalmic.myo.Arm;

/**
 * Created by szabol on 2014.11.03..
 */
public class MyoComboEvent {

    private MyoCombo combo;
    //FIXME The arm should be in the combo items if not only same hand combos are used
    private Arm arm;
    private long timestamp;

    public MyoComboEvent(MyoCombo combo, Arm arm, long timestamp) {
        this.combo = combo;
        this.arm = arm;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public MyoCombo getCombo() {
        return combo;
    }


    public Arm getArm() {
        return arm;
    }


}
