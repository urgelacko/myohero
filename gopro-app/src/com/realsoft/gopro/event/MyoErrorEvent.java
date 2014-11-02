package com.realsoft.gopro.event;

import com.realsoft.gopro.controller.GoProCommand;
import com.realsoft.gopro.controller.GoProSetting;
import com.thalmic.myo.Pose;

public class MyoErrorEvent {

    private final String message;

    public MyoErrorEvent(final String message) {
        this.message = message;
    }

    public MyoErrorEvent(final Throwable cause) {
        this(cause.getMessage());
    }

    public String getMessage() {
        return message;
    }

}
