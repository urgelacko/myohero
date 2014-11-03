package com.realsoft.gopro.ui.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.realsoft.gopro.R;
import com.realsoft.gopro.event.MyoPoseEvent;
import com.realsoft.gopro.event.MyoState;

import de.greenrobot.event.EventBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyoPoseFragment extends Fragment {


    public MyoPoseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        EventBus.getDefault().register(this);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_myo_pose, container, false);

    }

    public void onEventMainThread(MyoPoseEvent poseEvent) {

        ImageView image = (ImageView) getView().findViewById(R.id.myoPoseImage);

        int poseImageResourceId;
        switch (poseEvent.getPose()) {
            case FIST:
                poseImageResourceId = R.drawable.solid_blue_rh_fist;
                break;
            case THUMB_TO_PINKY:
                poseImageResourceId = R.drawable.solid_blue_rh_pinky_thumb;
                break;
            case FINGERS_SPREAD:
                poseImageResourceId = R.drawable.solid_blue_rh_spread_fingers;
                break;
            case WAVE_IN:
                poseImageResourceId = R.drawable.solid_blue_rh_wave_left;
                break;
            case WAVE_OUT:
                poseImageResourceId = R.drawable.solid_blue_rh_wave_right;
                break;
            default:
                MyoState myoState = MyoState.getCurrent();
                poseImageResourceId = !myoState.isConnected() || myoState.isLocked() ? R.drawable.lock : R.drawable.lock_open;
        }
        image.setBackgroundResource(poseImageResourceId);


        TextView comboState = (TextView) getView().findViewById(R.id.myoComboText);
        comboState.setText(poseEvent.isHold() ? R.string.myo_combo_hold : R.string.empty);

    }


}
