package com.urgesoft.gopro.event;

import com.urgesoft.gopro.controller.GoProCommand;
import com.urgesoft.gopro.controller.GoProSetting;

public class GoProCommandEvent {

    private GoProCommand command;

    private GoProSetting setting;

    public GoProCommandEvent(final GoProCommand command) {
        this(command, null);
    }

    public GoProCommandEvent(final GoProCommand command, final GoProSetting setting) {
        this.command = command;
        this.setting = setting;
    }

    public GoProCommand getCommand() {
        return command;
    }

    public void setCommand(final GoProCommand command) {
        this.command = command;
    }

    public GoProSetting getSetting() {
        return setting;
    }

    public void setSetting(final GoProSetting setting) {
        this.setting = setting;
    }

}
