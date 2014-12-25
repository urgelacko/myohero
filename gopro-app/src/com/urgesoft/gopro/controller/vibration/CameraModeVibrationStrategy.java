package com.urgesoft.gopro.controller.vibration;

import com.urgesoft.gopro.event.GoProStatus;
import com.thalmic.myo.Myo;

/**
 * Vibration feedback by camera mode
 * <br>
 * <ul>
 * <li>Camera - 1</li>
 * <li>Photo - 2</li>
 * <li>Burst - 3</li>
 * <li>Time lapse - 4</li>
 * <p/>
 * </ul>
 * <p/>
 * Created by szabol on 2014.12.22..
 */
public class CameraModeVibrationStrategy implements GoProCommandVibrationStrategy {


    private int modifier;

    public CameraModeVibrationStrategy(int modifier) {
        this.modifier = modifier;
    }

    @Override
    public void vibrate(Myo myo, GoProStatus status) {
        int vibrationCount = status.getBackPackStatus().getMode().ordinal() + 1;
        for (int i = 0; i < vibrationCount; i++) {
            myo.vibrate(Myo.VibrationType.MEDIUM);
        }
    }

    private int getModifier() {
        return modifier;
    }
}
