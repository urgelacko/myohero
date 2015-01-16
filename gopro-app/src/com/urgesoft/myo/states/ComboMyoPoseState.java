package com.urgesoft.myo.states;

import com.urgesoft.myo.states.combo.MyoCombo;

import java.util.Collection;
import java.util.List;

/**
 * Created by szabol on 2014.11.05..
 */
public class ComboMyoPoseState extends AbstractMyoPoseState {

    private int index;

    private Collection<MyoCombo> possibleCombos;


    public ComboMyoPoseState(MyoPoseState prevState, int index, List<MyoCombo> possibleCombos) {
        super(prevState);
        this.index = index;
        this.possibleCombos = possibleCombos;
    }

    @Override
    public Collection<MyoCombo> getPossibleCombos() {
        return possibleCombos;
    }

    @Override
    public int getComboIndex() {
        return index;
    }


}
