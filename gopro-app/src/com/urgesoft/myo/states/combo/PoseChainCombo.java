package com.urgesoft.myo.states.combo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.urgesoft.gopro.event.MyoPoseEvent;
import com.thalmic.myo.Pose;

import java.util.Iterator;
import java.util.List;

/**
 * Created by szabol on 2014.11.03..
 */
public class PoseChainCombo implements MyoPoseCombo {

    private ImmutableList<Pose> poses;

    public PoseChainCombo(Pose... poseChain) {
        poses = ImmutableList.copyOf(poseChain);
    }


    @Override
    public List<Pose> getComboChain() {
        return poses;
    }


    private boolean isComboInChain(List<MyoPoseEvent> comboChain) {

        //The combo chain is a queue so we check the equality from the back
        Iterator<Pose> poseParamIt = poses.iterator();
        Iterator<MyoPoseEvent> currentComboIt = Lists.reverse(comboChain).iterator();


        while (poseParamIt.hasNext()) {
            Pose nextComboPoseParam = poseParamIt.next();
            MyoPoseEvent nextComboItem = currentComboIt.next();

            if (!nextComboItem.getPose().equals(nextComboPoseParam)) {
                System.out.println(nextComboItem.getPose().name() + " <> " + nextComboPoseParam.name());
                return false;
            }

            System.out.println(nextComboItem.getPose().name() + " == " + nextComboPoseParam.name());
        }
        //We get here only if the last items of the current combo chain matched on the parametered combo
        //If the size is also equals we fire a combo
        return comboChain.size() == poses.size();

    }

}
