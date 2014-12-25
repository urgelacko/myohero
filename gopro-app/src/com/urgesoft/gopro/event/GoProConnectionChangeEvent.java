package com.urgesoft.gopro.event;

/**
 * Created by szabol on 2014.11.27..
 */
public class GoProConnectionChangeEvent {

    private GoProState state;

    public GoProConnectionChangeEvent(GoProState state) {
        this.state = state;
    }

    public GoProState getNewState() {
        return state;
    }

    public void setState(GoProState state) {
        this.state = state;
    }
}
