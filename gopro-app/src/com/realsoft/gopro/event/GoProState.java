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
package com.realsoft.gopro.event;

import com.realsoft.gopro.R;

public enum GoProState {

	TURNED_OFF(R.string.gopro_status_power_off), TURNED_ON(R.string.gopro_status_power_on), RECORDING(R.string.gopro_status_recording), READY(R.string.gopro_status_ready);

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
