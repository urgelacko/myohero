/*
 *
GoProEvent.java *  Copyright (c) 2014 RealSoft Informatikai Kft. All Rights Reserved.
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

import com.realsoft.gopro.controller.GoProCommand;
import com.realsoft.gopro.controller.GoProSetting;

public class GoProCommandEvent {

	private GoProCommand command;

	private GoProSetting setting;

	public GoProCommandEvent(final GoProCommand command) {
		this(command, null);
	}

	public GoProCommandEvent(final GoProCommand command, final GoProSetting setting) {
		this.command = command;
		this.setting = setting;
	}

	public GoProCommand getCommand() {
		return command;
	}

	public void setCommand(final GoProCommand command) {
		this.command = command;
	}

	public GoProSetting getSetting() {
		return setting;
	}

	public void setSetting(final GoProSetting setting) {
		this.setting = setting;
	}

}
