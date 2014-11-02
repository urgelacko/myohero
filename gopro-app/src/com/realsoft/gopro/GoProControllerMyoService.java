package com.realsoft.gopro;

import android.provider.ContactsContract;
import android.widget.Toast;

import com.realsoft.gopro.controller.GoProCommand;
import com.realsoft.gopro.event.GoProCommandEvent;
import com.realsoft.gopro.event.MyoState;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

import de.greenrobot.event.EventBus;

/**
 * Created by szabol on 2014.11.01..
 */
public class GoProControllerMyoService extends AbstractMyoService {


    @Override
    protected void onThumbToPinky(Myo myo, long timestamp, Pose pose) {

        super.onThumbToPinky(myo, timestamp, pose);

        MyoState currentState = MyoState.getCurrent();
        currentState.setLocked(!currentState.isLocked());

        myo.vibrate(Myo.VibrationType.SHORT);
        //2 vibrations on lock
        if (currentState.isLocked()) {
            myo.vibrate(Myo.VibrationType.SHORT);
        }

        EventBus.getDefault().postSticky(currentState);
    }

    @Override
    protected void onFingersSpread(Myo myo, long timestamp, Pose pose) {
        super.onFingersSpread(myo, timestamp, pose);

        EventBus.getDefault().post(new GoProCommandEvent(GoProCommand.START_RECORD));
    }

    @Override
    protected void onFist(Myo myo, long timestamp, Pose pose) {
        super.onFist(myo, timestamp, pose);

        EventBus.getDefault().post(new GoProCommandEvent(GoProCommand.STOP_RECORD));
    }
}
