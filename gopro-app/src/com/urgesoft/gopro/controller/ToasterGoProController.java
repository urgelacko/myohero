package com.urgesoft.gopro.controller;

import com.urgesoft.gopro.event.GoProCommandEvent;
import com.urgesoft.gopro.event.GoProConnectionChangeEvent;
import com.urgesoft.gopro.event.GoProState;
import com.urgesoft.gopro.event.GoProStatus;

import de.greenrobot.event.EventBus;

public final class ToasterGoProController extends EventBusGoProController {

    private BackPackStatus backPackStatus;

    @Override
    protected void execute(final GoProCommandEvent command) throws Exception {
        try {
            Thread.sleep(1500L);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        switch (command.getCommand()) {
            case CONNECT:
                EventBus.getDefault().post(new GoProConnectionChangeEvent(GoProState.CONNECTED));
                break;
            case DISCONNECT:
                EventBus.getDefault().post(new GoProConnectionChangeEvent(GoProState.DISCONNECTED));
                break;
        }

        super.execute(command);
    }

    @Override
    protected void toggleRecord() {

        if (GoProState.RECORDING.equals(getStatus().getState())) {
            stopRecord();
        } else {
            startRecord();
        }

    }

    @Override
    protected void stopPreview() {

        GoProStatus newStatus = getStatus();
        if (!newStatus.getBackPackStatus().isTurnedOn()) {
            return;
        }

        GoProStatus status = getStatus();
        status.getBackPackStatus().setPreviewEnabled(false);

        getEventBus().postSticky(status);
    }

    @Override
    protected void setCameraMode(final GoProSetting mode) {

        GoProStatus newStatus = getStatus();
        if (!newStatus.getBackPackStatus().isTurnedOn() || newStatus.getState().equals(GoProState.RECORDING)) {
            return;
        }

        queryBackPackStatus().setMode(GoProSettings.Mode.forParamValue(mode.getParamValue()));

        GoProStatus status = getStatus();
        status.setBackPackStatus(queryBackPackStatus());

        getEventBus().postSticky(status);
    }

    @Override
    protected void startPreview() {

        GoProStatus newStatus = getStatus();
        if (!newStatus.getBackPackStatus().isTurnedOn()) {
            return;
        }

        GoProStatus status = getStatus();
        status.getBackPackStatus().setPreviewEnabled(true);

        getEventBus().postSticky(status);
    }

    @Override
    protected void startRecord() {

        GoProStatus currentStatus = getStatus();
        if (!GoProState.CONNECTED.equals(currentStatus.getState())) {
            return;
        }

        BackPackStatus actualStatus = getStatus().getBackPackStatus();
        actualStatus.setTurnedOn(true);
        getEventBus().postSticky(new GoProStatus(GoProState.RECORDING, actualStatus));
    }

    @Override
    protected void stopRecord() {

        BackPackStatus actualStatus = getStatus().getBackPackStatus();

        getEventBus().postSticky(new GoProStatus(GoProState.CONNECTED, actualStatus));
    }

    @Override
    protected void power(final boolean poweredOn) throws Exception {

        GoProStatus currentStatus = getStatus();
        if (GoProState.DISCONNECTED.equals(currentStatus.getState()) || GoProState.CONNECTING.equals(currentStatus.getState()) || currentStatus.getBackPackStatus().isTurnedOn() == poweredOn) {
            return;
        }

        BackPackStatus actualStatus = getStatus().getBackPackStatus();

        BackPackStatus newStatus = queryBackPackStatus();
        newStatus.setPreviewEnabled(actualStatus.isPreviewEnabled());
        newStatus.setTurnedOn(poweredOn);

        getEventBus().postSticky(new GoProStatus(GoProState.CONNECTED, newStatus));
    }

    @Override
    public void setPassword(String password) {

    }

    @Override
    public BackPackStatus queryBackPackStatus() {
        if (null == backPackStatus) {
            backPackStatus = new BackPackStatus();

            backPackStatus.setTurnedOn(true);
            backPackStatus.setPreviewEnabled(true);
            backPackStatus.setMode(GoProSettings.Mode.CAMERA);
        }

        return backPackStatus;
    }

}
