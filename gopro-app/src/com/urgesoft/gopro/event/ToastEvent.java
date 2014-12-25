package com.urgesoft.gopro.event;

/**
 * Created by szabol on 2014.12.19..
 */
public class ToastEvent {

    private String message;


    public ToastEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
