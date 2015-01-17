package com.urgesoft.gopro.controller;

import static com.google.common.base.Preconditions.checkNotNull;

import android.provider.ContactsContract;
import android.util.Log;

import com.urgesoft.gopro.event.GoProCommandEvent;
import com.urgesoft.gopro.event.GoProCommandResultEvent;
import com.urgesoft.gopro.event.GoProConnectionChangeEvent;
import com.urgesoft.gopro.event.GoProErrorEvent;
import com.urgesoft.gopro.event.GoProState;
import com.urgesoft.gopro.event.GoProStatus;
import com.urgesoft.gopro.event.ToastEvent;

import java.io.IOException;

import de.greenrobot.event.EventBus;

public abstract class EventBusGoProController implements GoProController {

    private static final String TAG = EventBusGoProController.class.getSimpleName();

    @Override
    public final void executeCommand(final GoProCommandEvent command) {

        try {
            execute(command);

            Log.d(TAG, String.format("GoPro command executed! command:[%s]", command));
            getEventBus().post(new GoProCommandResultEvent(command.getCommand(), command.getSetting()));
        } catch (IOException ioE) {
            propagateException(command, ioE);
            getEventBus().post(new GoProConnectionChangeEvent(GoProState.DISCONNECTED));
        } catch (final Exception e) {
            propagateException(command, e);
        }

    }

    private void propagateException(GoProCommandEvent command, Exception e) {
        Log.e(TAG, "GoPro error: " + e.getMessage(), e);
        final GoProErrorEvent errorEvent = new GoProErrorEvent(e.getMessage(), command.getCommand());
        errorEvent.setSetting(command.getSetting());
        getEventBus().post(errorEvent);
    }

    protected void execute(final GoProCommandEvent command) throws Exception {
        switch (command.getCommand()) {
            case TURN_ON:
                power(true);
                break;
            case TURN_OFF:
                power(false);
                break;
            case START_RECORD:
                startRecord();
                break;
            case TOGGLE_RECORD:
                toggleRecord();
                break;
            case STOP_RECORD:
                stopRecord();
                break;
            case START_PREVIEW:
                startPreview();
                break;
            case STOP_PREVIEW:
                stopPreview();
                break;
            case SET_CAMERA_MODE:
                setCameraMode(checkNotNull(command.getSetting(), "Camera mode setting is empty!"));
                break;
            case NEXT_CAMERA_MODE:
                GoProSettings.Mode currentCamMode = getStatus().getBackPackStatus().getMode();
                int nextStatusValue = currentCamMode.ordinal() + 1 >= GoProSettings.Mode.values().length ? 0 : currentCamMode.ordinal() + 1;
                setCameraMode(GoProSettings.Mode.values()[nextStatusValue]);
                break;
            case PREV_CAMERA_MODE:
                GoProSettings.Mode currentMode = getStatus().getBackPackStatus().getMode();
                int nextStatus = currentMode.ordinal() - 1 < 0 ? GoProSettings.Mode.values().length - 1 : currentMode.ordinal() - 1;
                setCameraMode(GoProSettings.Mode.values()[nextStatus]);
            default:
                break;
        }

    }

    protected EventBus getEventBus() {
        return EventBus.getDefault();
    }

    protected abstract void toggleRecord() throws Exception;

    protected abstract void power(final boolean poweredOn) throws Exception;

    protected abstract void startPreview() throws Exception;

    protected abstract void stopPreview() throws Exception;

    protected abstract void startRecord() throws Exception;

    protected abstract void stopRecord() throws Exception;

    protected abstract void setCameraMode(GoProSetting mode) throws Exception;

    public GoProStatus getStatus() {
        return getEventBus().getStickyEvent(GoProStatus.class);
    }

}
