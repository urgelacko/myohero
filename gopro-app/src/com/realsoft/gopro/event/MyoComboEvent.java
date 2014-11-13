package com.realsoft.gopro.event;

import com.realsoft.myo.states.combo.MyoCombos;

/**
 * Created by szabol on 2014.11.03..
 */
public class MyoComboEvent {

    private MyoCombos combo;

    private long timestamp;

    public MyoComboEvent(MyoCombos combo, long timestamp) {
        this.combo = combo;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public MyoCombos getCombo() {
        return combo;
    }


}
