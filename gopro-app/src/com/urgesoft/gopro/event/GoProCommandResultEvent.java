package com.urgesoft.gopro.event;

import com.urgesoft.gopro.controller.GoProCommand;
import com.urgesoft.gopro.controller.GoProSetting;

public class GoProCommandResultEvent {

    private GoProCommand command;

    private GoProSetting setting;

    public GoProCommandResultEvent(final GoProCommand command) {
        this.command = command;
    }

    public GoProCommandResultEvent(final GoProCommand command, final GoProSetting setting) {
        this.command = command;
        this.setSetting(setting);
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

    public void setSetting(GoProSetting setting) {
        this.setting = setting;
    }

}
