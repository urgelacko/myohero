package com.urgesoft.gopro.controller;

import android.util.Log;

import org.gopro.core.Operations;
import org.gopro.core.model.BacPacStatus;
import org.gopro.core.model.CamFields;
import org.gopro.core.model.ENCameraPowerStatus;
import org.gopro.main.GoProApi;

import com.urgesoft.gopro.event.GoProErrorEvent;
import com.urgesoft.gopro.event.GoProState;
import com.urgesoft.gopro.event.GoProStatus;

import de.greenrobot.event.EventBus;

public class GoProApiGoProController extends EventBusGoProController {

    private GoProApi gopro;

    public GoProApiGoProController(final String password) {
        gopro = new GoProApi(password);
    }

    public void setPassword(String password) {
        gopro = new GoProApi(password);
    }


    @Override
    public BackPackStatus queryBackPackStatus() {

        try {
            BacPacStatus bacPackStatus = gopro.getHelper().getBacpacStatus();
            BackPackStatus backPackStatus = new BackPackStatus();
            backPackStatus.setTurnedOn(ENCameraPowerStatus.POWERON.getCode() == bacPackStatus.getCameraPower());
            if (backPackStatus.isTurnedOn()) {
                CamFields cameraSettings = gopro.getHelper().getCameraSettings();

                backPackStatus.setMode(GoProSettings.Mode.forStatusValue(cameraSettings.getMode()));
                //Bug in api, always true
                backPackStatus.setPreviewEnabled(cameraSettings.isPreviewActive());
            }

            return backPackStatus;
        } catch (Exception e) {
            Log.e("GoProApiGoProController", "Error during wait until is ready!", e);
            EventBus.getDefault().post(new GoProErrorEvent("Error during the camera check!", GoProCommand.CONNECT));

            return new BackPackStatus();
        }

    }

    @Override
    protected void setCameraMode(final GoProSetting setting) throws Exception {

        GoProStatus newStatus = getStatus();
        if (!newStatus.getBackPackStatus().isTurnedOn() || newStatus.getState().equals(GoProState.RECORDING)) {
            return;
        }

        gopro.getHelper().sendCommand(Operations.CAMERA_CM, setting.getParamValue());

        newStatus.setBackPackStatus(queryBackPackStatus());
        getEventBus().postSticky(newStatus);

    }


    @Override
    protected void stopPreview() throws Exception {

        GoProStatus newStatus = getStatus();
        if (!newStatus.getBackPackStatus().isTurnedOn()) {
            return;
        }

        //bug in api always true

        //gopro.getHelper().sendCommand(Operations.CAMERA_PV, GoProSettings.Preview.OFF.getParamValue());
        //newStatus.setBackPackStatus(queryBackPackStatus());
        getEventBus().postSticky(newStatus);
    }

    @Override
    protected void startPreview() throws Exception {

        GoProStatus newStatus = getStatus();
        if (!newStatus.getBackPackStatus().isTurnedOn()) {
            return;
        }

        //gopro.getHelper().sendCommand(Operations.CAMERA_PV, GoProSettings.Preview.ON.getParamValue());

        //GoProStatus newStatus = getStatus();
        //newStatus.setBackPackStatus(queryBackPackStatus());
        getEventBus().postSticky(getStatus());
    }

    @Override
    protected void startRecord() throws Exception {

        GoProStatus currentStatus = getStatus();
        if (!GoProState.CONNECTED.equals(currentStatus.getState())) {
            return;
        }

        BackPackStatus backPackStatus = currentStatus.getBackPackStatus();
        if (!backPackStatus.isTurnedOn()) {
            gopro.powerOnAndStartRecord();
        } else {
            gopro.startRecord();
        }

        GoProStatus newStatus = new GoProStatus(GoProState.RECORDING, queryBackPackStatus());
        getEventBus().postSticky(newStatus);

        //If not in continuous recording mode, stop recording also
        if (!backPackStatus.getMode().equals(GoProSettings.Mode.CAMERA) && !backPackStatus.getMode().equals(GoProSettings.Mode.TIMELAPSE)) {
            stopRecord();
        }
    }

    @Override
    protected void stopRecord() throws Exception {
        if (isRecording(getStatus().getState())) {
            gopro.stopRecord();
            getEventBus().postSticky(new GoProStatus(GoProState.CONNECTED, queryBackPackStatus()));
        }
    }


    @Override
    protected void toggleRecord() throws Exception {
        if (isRecording(getStatus().getState())) {
            stopRecord();
        } else {
            startRecord();
        }

    }

    @Override
    protected void power(final boolean poweredOn) throws Exception {

        GoProStatus currentStatus = getStatus();
        if (GoProState.DISCONNECTED.equals(currentStatus.getState()) || GoProState.CONNECTING.equals(currentStatus.getState()) || currentStatus.getBackPackStatus().isTurnedOn() == poweredOn) {
            return;
        }

        if (poweredOn) {
            gopro.powerAndWaitUntilIsReady();
            getEventBus().postSticky(new GoProStatus(GoProState.CONNECTED, queryBackPackStatus()));
        } else {
            if (GoProState.RECORDING.equals(currentStatus)) {
                gopro.stopRecordAndPowerOff();
            } else {
                gopro.powerOff();
            }
            getEventBus().postSticky(new GoProStatus(GoProState.CONNECTED));
        }

    }

    private boolean isRecording(GoProState state) {
        return GoProState.RECORDING.equals(state);
    }

}
