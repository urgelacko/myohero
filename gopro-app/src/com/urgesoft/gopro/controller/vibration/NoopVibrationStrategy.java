package com.urgesoft.gopro.controller.vibration;

import com.urgesoft.gopro.event.GoProStatus;
import com.thalmic.myo.Myo;

/**
 * Created by szabol on 2014.12.22..
 */
public class NoopVibrationStrategy implements GoProCommandVibrationStrategy {

    @Override
    public void vibrate(Myo myo, GoProStatus status) {

    }
}
