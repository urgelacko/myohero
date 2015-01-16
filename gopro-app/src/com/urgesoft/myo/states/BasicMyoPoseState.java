package com.urgesoft.myo.states;

import com.google.common.collect.Lists;
import com.urgesoft.myo.states.combo.MyoCombo;

import java.util.Collection;

/**
 * Created by szabol on 2014.11.05..
 */
public class BasicMyoPoseState extends AbstractMyoPoseState {


    public BasicMyoPoseState(MyoPoseState prevState, long timestamp) {
        super(prevState, timestamp);
    }

    public BasicMyoPoseState(MyoPoseState prevState) {
        super(prevState);
    }

    @Override
    public Collection<MyoCombo> getPossibleCombos() {
        return Lists.newArrayList(MyoCombo.values());
    }



    @Override
    public int getComboIndex() {
        return -1;
    }

}
