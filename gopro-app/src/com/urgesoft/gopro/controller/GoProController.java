package com.urgesoft.gopro.controller;

import com.urgesoft.gopro.event.GoProCommandEvent;

public interface GoProController {

    void executeCommand(GoProCommandEvent command);

    void setPassword(String password);

    BackPackStatus queryBackPackStatus();
}
