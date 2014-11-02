package com.realsoft.gopro.event;

import com.thalmic.myo.Pose;

import de.greenrobot.event.EventBus;

/**
 * Created by szabol on 2014.11.01..
 */
public class MyoPoseEvent {


    private Pose pose;


    public MyoPoseEvent(Pose pose) {
        this.pose = pose;
    }

    public Pose getPose() {
        return pose;
    }

    public void setPose(Pose pose) {
        this.pose = pose;
    }
}
