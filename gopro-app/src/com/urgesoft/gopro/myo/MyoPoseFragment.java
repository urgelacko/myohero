package com.urgesoft.gopro.myo;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.urgesoft.gopro.R;
import com.urgesoft.gopro.event.MyoComboEvent;
import com.urgesoft.gopro.event.MyoPoseEvent;
import com.urgesoft.myo.states.combo.MyoComboItem;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.vov.vitamio.widget.CenterLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyoPoseFragment extends Fragment {


    private LinearLayout imageList;

    public MyoPoseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_myo_pose, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        imageList = (LinearLayout) getView().findViewById(R.id.myoPoseImages);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {

        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    public void onEventMainThread(MyoPoseEvent poseEvent) {
        if (!Pose.REST.equals(poseEvent.getPose()) || imageList.getAnimation() == null) {
            cancelFadeOutAnimation(imageList);

            ImageView imageView = new ImageView(this.getActivity());
            imageList.addView(imageView);

            updatePoseImageResource(poseEvent.getArm(), poseEvent.getPose(), imageView);

            if (!poseEvent.isHold()) {
                resetAndStartPoseFadeOut();
            }
        }
    }


    public void onEventMainThread(MyoComboEvent comboEvent) {

        cancelFadeOutAnimation(imageList);

        //Show the pose icon for every combo item
        List<MyoComboItem> comboChain = comboEvent.getCombo().getComboChain();

        for (MyoComboItem comboItem : comboChain) {
            ImageView imageView = new ImageView(this.getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, CenterLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setAdjustViewBounds(true);

            //FIXME the actual arm should be handled cause if not only "same hand" combos will be defined, the individual items should contain their arm property also
            Arm arm = comboItem.getArm() == null ? comboEvent.getArm() : comboEvent.getArm();
            updatePoseImageResource(arm, comboItem.getPose(), imageView);

            imageList.addView(imageView);
        }

        resetAndStartPoseFadeOut();
    }


    private void updatePoseImageResource(Arm arm, Pose pose, ImageView image) {
        int poseImageResourceId;
        switch (pose) {
            case FIST:
                poseImageResourceId = Arm.LEFT.equals(arm) ? R.drawable.solid_blue_lh_fist : R.drawable.solid_blue_rh_fist;
                image.setImageResource(poseImageResourceId);
                break;
            case DOUBLE_TAP:
                poseImageResourceId = Arm.LEFT.equals(arm) ? R.drawable.solid_blue_lh_double_tap : R.drawable.solid_blue_rh_double_tap;
                image.setImageResource(poseImageResourceId);
                break;
            case FINGERS_SPREAD:
                poseImageResourceId = Arm.LEFT.equals(arm) ? R.drawable.solid_blue_lh_spread_fingers : R.drawable.solid_blue_rh_spread_fingers;
                image.setImageResource(poseImageResourceId);
                break;
            case WAVE_IN:
                poseImageResourceId = Arm.LEFT.equals(arm) ? R.drawable.solid_blue_lh_wave_right : R.drawable.solid_blue_rh_wave_left;
                image.setImageResource(poseImageResourceId);
                break;
            case WAVE_OUT:
                poseImageResourceId = Arm.LEFT.equals(arm) ? R.drawable.solid_blue_lh_wave_left : R.drawable.solid_blue_rh_wave_right;
                image.setImageResource(poseImageResourceId);
                break;
        }
    }

    private void resetAndStartPoseFadeOut() {

        final Animation animationFadeOut = AnimationUtils.loadAnimation(this.getActivity(), R.anim.fadeout);
        animationFadeOut.reset();
        animationFadeOut.setFillAfter(true);
        imageList.startAnimation(animationFadeOut);
    }

    private void cancelFadeOutAnimation(LinearLayout imageList) {
        //Cancel fadeout if running
        imageList.clearAnimation();
        imageList.removeAllViews();
    }

}
