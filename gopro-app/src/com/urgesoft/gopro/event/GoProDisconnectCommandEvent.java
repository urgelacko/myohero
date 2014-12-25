package com.urgesoft.gopro.event;

import com.urgesoft.gopro.controller.GoProCommand;

public class GoProDisconnectCommandEvent extends GoProCommandEvent {

    public GoProDisconnectCommandEvent() {
        super(GoProCommand.DISCONNECT);
    }

}
