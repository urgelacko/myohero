package com.urgesoft.gopro.event;

public enum GoProState {

//    TURNED_OFF(R.string.gopro_status_power_off),
//    READY(R.string.gopro_status_power_on),
    RECORDING(com.urgesoft.gopro.R.string.gopro_status_recording),
    CONNECTED(com.urgesoft.gopro.R.string.gopro_status_connected),
    DISCONNECTED(com.urgesoft.gopro.R.string.gopro_status_disconnected),
    CONNECTING(com.urgesoft.gopro.R.string.gopro_status_connecting);

    private int messageKey;

    private GoProState(final int msgKey) {
        setMessageKey(msgKey);
    }

    public int getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(final int messageKey) {
        this.messageKey = messageKey;
    }

}
