package com.urgesoft.gopro.event;

import com.urgesoft.gopro.controller.GoProCommand;

public class GoProConnectCommandEvent extends GoProCommandEvent {

    public GoProConnectCommandEvent() {
        super(GoProCommand.CONNECT);
    }

}
