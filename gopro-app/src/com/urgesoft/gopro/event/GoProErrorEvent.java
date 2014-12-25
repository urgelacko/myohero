package com.urgesoft.gopro.event;

import com.urgesoft.gopro.controller.GoProCommand;
import com.urgesoft.gopro.controller.GoProSetting;

public class GoProErrorEvent {

	private final String message;

	private final GoProCommand command;

	private GoProSetting setting;

	public GoProErrorEvent(final String message, final GoProCommand command) {
		this.message = message;
		this.command = command;
	}

	public GoProErrorEvent(final Throwable cause, final GoProCommand command) {
		this(cause.getMessage(), command);
	}

	public GoProCommand getCommand() {
		return command;
	}

	public String getMessage() {
		return message;
	}

	public GoProSetting getSetting() {
		return setting;
	}

	public void setSetting(GoProSetting setting) {
		this.setting = setting;
	}
}
