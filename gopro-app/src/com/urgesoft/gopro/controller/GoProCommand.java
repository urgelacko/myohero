package com.urgesoft.gopro.controller;

import com.urgesoft.gopro.R;
import com.urgesoft.gopro.controller.vibration.CameraModeVibrationStrategy;
import com.urgesoft.gopro.controller.vibration.DefaultVibrationStrategy;
import com.urgesoft.gopro.controller.vibration.GoProCommandVibrationStrategy;
import com.urgesoft.gopro.controller.vibration.NoopVibrationStrategy;

public enum GoProCommand {

    LOCK(com.urgesoft.gopro.R.string.gopro_command_lock, true, new NoopVibrationStrategy()),
    UNLOCK(R.string.gopro_command_unlock, true, new NoopVibrationStrategy()),
    TOGGLE_LOCK(com.urgesoft.gopro.R.string.gopro_command_toggle_lock),
    CONNECT(com.urgesoft.gopro.R.string.gopro_command_connect, false),
    DISCONNECT(com.urgesoft.gopro.R.string.gopro_command_disconnect, false),
    START_RECORD(com.urgesoft.gopro.R.string.gopro_command_start_record),
    STOP_RECORD(com.urgesoft.gopro.R.string.gopro_command_stop_record),
    TOGGLE_RECORD(com.urgesoft.gopro.R.string.gopro_command_toggle_record),
    TURN_ON(com.urgesoft.gopro.R.string.gopro_command_turn_on),
    TURN_OFF(com.urgesoft.gopro.R.string.gopro_command_turn_off),
    START_PREVIEW(com.urgesoft.gopro.R.string.gopro_command_start_preview, false),
    STOP_PREVIEW(com.urgesoft.gopro.R.string.gopro_command_stop_preview, false),
    NEXT_CAMERA_MODE(com.urgesoft.gopro.R.string.gopro_command_next_mode, true, new CameraModeVibrationStrategy(1)),
    PREV_CAMERA_MODE(com.urgesoft.gopro.R.string.gopro_command_prev_mode, true, new CameraModeVibrationStrategy(-1)),
    SET_CAMERA_MODE(com.urgesoft.gopro.R.string.gopro_command_camera_mode, false);

    private int messageKey;

    private boolean assignable = true;

    private GoProCommandVibrationStrategy vibrationStrategy;

    private GoProCommand(final int messageKey) {
        this(messageKey, true);
        this.messageKey = messageKey;
    }

    private GoProCommand(final int messageKey, boolean assignable) {
        this(messageKey, assignable, new DefaultVibrationStrategy());
    }

    private GoProCommand(final int messageKey, boolean assignable, GoProCommandVibrationStrategy vibrationStrategy) {
        this.messageKey = messageKey;
        this.assignable = assignable;
        this.vibrationStrategy = vibrationStrategy;
    }

    public int getMessageKey() {
        return messageKey;
    }

    public boolean isAssignable() {
        return assignable;
    }

    public GoProCommandVibrationStrategy getVibrationStrategy() {
        return vibrationStrategy;
    }
}

