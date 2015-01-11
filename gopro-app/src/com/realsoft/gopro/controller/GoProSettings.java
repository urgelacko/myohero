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

public class GoProSettings {

	private GoProSettings() {

	}

	public enum Mode implements GoProSetting {
		CAMERA(0),
		PHOTO(1),
		BURST(3),
		TIMELAPSE(3),
		SELF_TIMER(4);

		private int value;

		private Mode(final int value) {
			this.value = value;
		}

		@Override
		public int getValue() {
			return value;
		}
	}

}
