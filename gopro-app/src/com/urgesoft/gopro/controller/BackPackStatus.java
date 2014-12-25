package com.urgesoft.gopro.controller;

/**
 * Created by szabol on 2014.12.08..
 */
public class BackPackStatus {

    private boolean turnedOn = false;

    //Right now always true is returned from GoPro
    private boolean previewEnabled = false;

    private GoProSettings.Mode mode = GoProSettings.Mode.CAMERA;

    public boolean isTurnedOn() {
        return turnedOn;
    }

    public void setTurnedOn(boolean turnedOn) {
        this.turnedOn = turnedOn;
    }

    public boolean isPreviewEnabled() {
        return previewEnabled;
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        this.previewEnabled = previewEnabled;
    }

    public GoProSettings.Mode getMode() {
        return mode;
    }

    public void setMode(GoProSettings.Mode mode) {
        this.mode = mode;
    }
}
