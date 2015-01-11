/*
 *
 *  Copyright (c) 2014 RealSoft Informatikai Kft. All Rights Reserved.
 *
 *  This software is the confidential and proprietary information of
 *  RealSoft Informatikai Kft. ("Confidential Information").
 *  You shall not disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with RealSoft.
 *
 *  REALSOFT MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 *  THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *  TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *  PARTICULAR PURPOSE, OR NON-INFRINGEMENT. REALSOFT SHALL NOT BE LIABLE FOR
 *  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 *  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.realsoft.gopro.controller;

import static com.google.common.base.Preconditions.checkNotNull;
import android.util.Log;

import com.realsoft.gopro.event.GoProCommandEvent;
import com.realsoft.gopro.event.GoProCommandResultEvent;
import com.realsoft.gopro.event.GoProErrorEvent;
import com.realsoft.gopro.event.GoProStatus;

import de.greenrobot.event.EventBus;

public abstract class EventBusGoProController implements GoProController {

	private static final String TAG = EventBusGoProController.class.getSimpleName();

	@Override
	public final void executeCommand(final GoProCommandEvent command) {

		try {

			execute(command);

			Log.d(TAG, String.format("GoPro command executed! command:[%s]", command));
			getEventBus().post(new GoProCommandResultEvent(command.getCommand(), command.getSetting()));

		} catch (final Exception e) {
			Log.e(TAG, "GoPro error: " + e.getMessage(), e);
			final GoProErrorEvent errorEvent = new GoProErrorEvent(e.getMessage(), command.getCommand());
			errorEvent.setSetting(command.getSetting());
			getEventBus().post(errorEvent);
		}

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
			case STOP_RECORD:
				stopRecord();
				break;
			case START_PREVIEW:
				startPreview();
				break;
			case STOP_PREVIEW:
				stopPreview();
				break;
			case CAMERA_MODE:
				setCameraMode(checkNotNull(command.getSetting(), "Camera mode setting is empty!"));
			default:
				break;
		}
	}

	protected EventBus getEventBus() {
		return EventBus.getDefault();
	}

	protected abstract void power(final boolean poweredOn) throws Exception;

	protected abstract void startPreview() throws Exception;

	protected abstract void stopPreview() throws Exception;

	protected abstract void startRecord() throws Exception;

	protected abstract void stopRecord() throws Exception;

	protected abstract void setCameraMode(GoProSetting mode) throws Exception;

	public GoProStatus getState() {
		return getEventBus().getStickyEvent(GoProStatus.class);
	}

}
