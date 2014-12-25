package com.urgesoft.gopro.event;

public class GoProPreviewState {

    private boolean playing;

    public GoProPreviewState(boolean playing) {
        this.playing = playing;
    }

    public boolean isPlaying() {
        return playing;
    }
}
