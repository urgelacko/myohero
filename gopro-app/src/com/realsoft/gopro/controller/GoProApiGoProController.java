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

import org.gopro.main.GoProApi;

import com.realsoft.gopro.event.GoProState;
import com.realsoft.gopro.event.GoProStatus;

public class GoProApiGoProController extends EventBusGoProController {

	private GoProApi gopro;

	public GoProApiGoProController(final String password) {
		gopro = new GoProApi(password);
	}

	@Override
	protected void stopPreview() throws Exception {
		gopro.getHelper().setCamLivePreview(false);
	}

	@Override
	protected void setCameraMode(final GoProSetting setting) throws Exception {
		gopro.getHelper().setCamMode(setting.getValue());
	}

	@Override
	protected void startPreview() throws Exception {
		gopro.getHelper().setCamLivePreview(true);
	}

	@Override
	protected void startRecord() throws Exception {
		if (GoProState.TURNED_OFF.equals(getState())) {
			gopro.powerOnAndStartRecord();
		} else {
			gopro.startRecord();
		}
		getEventBus().postSticky(new GoProStatus(GoProState.RECORDING));
	}

	@Override
	protected void stopRecord() throws Exception {
		gopro.stopRecord();
		getEventBus().postSticky(new GoProStatus(GoProState.READY));
	}

	@Override
	protected void power(final boolean poweredOn) throws Exception {
		if (poweredOn) {
			gopro.powerOn();
			getEventBus().postSticky(new GoProStatus(GoProState.TURNED_ON));
		} else {
			if (GoProState.RECORDING.equals(getState())) {
				gopro.stopRecord();
			}
			gopro.powerOff();

			getEventBus().postSticky(new GoProStatus(GoProState.TURNED_OFF));
		}

	}
}
