package com.urgesoft.gopro.controller.vibration;

import com.urgesoft.gopro.event.GoProStatus;
import com.thalmic.myo.Myo;

/**
 * Created by szabol on 2014.12.22..
 */
public interface GoProCommandVibrationStrategy {

    void vibrate(Myo myo, GoProStatus goProStatus);

}
