/*
 *
 *  Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.
 *
 *  This software is the confidential and proprietary information of
 *  Sonrisa Informatikai Kft. ("Confidential Information").
 *  You shall not disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Sonrisa.
 *
 *  SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 *  THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *  TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *  PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 *  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 *  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.realsoft.gopro.controller;

import com.realsoft.gopro.R;

public enum GoProCommand {

	CONNECT(R.string.gopro_command_connect),
	DISCONNECT(R.string.gopro_command_disconnect),
	TURN_ON(R.string.gopro_command_turn_on),
	TURN_OFF(R.string.gopro_command_turn_off),
	START_RECORD(R.string.gopro_command_start_record),
	STOP_RECORD(R.string.gopro_command_stop_record),
	START_PREVIEW(R.string.gopro_command_start_preview),
	STOP_PREVIEW(R.string.gopro_command_stop_preview),
	TAKE_PICTURE(R.string.gopro_command_take_picture),
	CAMERA_MODE(R.string.gopro_command_camera_mode);

	private int messageKey;

	private GoProCommand(final int messageKey) {
		this.messageKey = messageKey;
	}

	public int getMessageKey() {
		return messageKey;
	}
}
