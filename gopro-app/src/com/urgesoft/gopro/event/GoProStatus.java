package com.urgesoft.gopro.event;

import com.urgesoft.gopro.controller.BackPackStatus;

public class GoProStatus {

    private GoProState state = GoProState.DISCONNECTED;


    private BackPackStatus backPackStatus = new BackPackStatus();

    public GoProStatus(final GoProState state) {
        setState(state);
    }


    public GoProStatus(final GoProState state, BackPackStatus backPackStatus) {
        setState(state);
        this.backPackStatus = backPackStatus;
    }

    public GoProState getState() {
        return state;
    }

    public void setState(final GoProState state) {
        this.state = state;
    }

    public BackPackStatus getBackPackStatus() {
        return backPackStatus;
    }

    public void setBackPackStatus(BackPackStatus backPackStatus) {
        this.backPackStatus = backPackStatus;
    }

}
