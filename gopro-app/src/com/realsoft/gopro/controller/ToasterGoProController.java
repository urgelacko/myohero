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

import com.realsoft.gopro.event.GoProCommandEvent;
import com.realsoft.gopro.event.GoProState;
import com.realsoft.gopro.event.GoProStatus;

public final class ToasterGoProController extends EventBusGoProController {
	@Override
	protected void execute(final GoProCommandEvent command) throws Exception {
		try {
			Thread.sleep(1500L);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		super.execute(command);
	}

	@Override
	protected void stopPreview() {

	}

	@Override
	protected void setCameraMode(final GoProSetting mode) {

	}

	@Override
	protected void startPreview() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void startRecord() {
		getEventBus().postSticky(new GoProStatus(GoProState.RECORDING));

	}

	@Override
	protected void stopRecord() {
		getEventBus().postSticky(new GoProStatus(GoProState.READY));
	}

	@Override
	protected void power(final boolean poweredOn) throws Exception {
		getEventBus().postSticky(poweredOn ? new GoProStatus(GoProState.TURNED_ON) : new GoProStatus(GoProState.TURNED_OFF));

	}
}
