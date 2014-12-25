package com.urgesoft.gopro.event;

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
