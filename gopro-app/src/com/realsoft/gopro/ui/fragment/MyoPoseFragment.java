package com.realsoft.gopro.ui.fragment;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.realsoft.gopro.R;
import com.realsoft.gopro.event.MyoComboEvent;
import com.realsoft.gopro.event.MyoPoseEvent;
import com.thalmic.myo.Pose;

import java.util.List;

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

        if (!Pose.REST.equals(poseEvent.getPose())) {
            LinearLayout imageList = (LinearLayout) getView().findViewById(R.id.myoPoseImages);
            imageList.removeAllViews();
            ImageView imageView = new ImageView(this.getActivity());
            updatePoseImageResource(poseEvent.getPose(), imageView);
            imageList.addView(imageView);

            TextView comboState = (TextView) getView().findViewById(R.id.myoHoldText);
            comboState.setText(poseEvent.isHold() ? R.string.myo_combo_hold : R.string.empty);
        }

    }


    public void onEventMainThread(MyoComboEvent comboEvent) {
        LinearLayout imageList = (LinearLayout) getView().findViewById(R.id.myoPoseImages);
        imageList.removeAllViews();
        for (Pose pose : comboEvent.getCombo().getPoseCombo().getComboChain()) {
            ImageView imageView = new ImageView(this.getActivity());
            updatePoseImageResource(pose, imageView);
            imageList.addView(imageView);
        }
    }

    private void updatePoseImageResource(Pose pose, ImageView image) {
        int poseImageResourceId;
        switch (pose) {
            case FIST:
                poseImageResourceId = R.drawable.solid_blue_rh_fist;
                image.setBackgroundResource(poseImageResourceId);
                break;
            case THUMB_TO_PINKY:
                poseImageResourceId = R.drawable.solid_blue_rh_pinky_thumb;
                image.setBackgroundResource(poseImageResourceId);
                break;
            case FINGERS_SPREAD:
                poseImageResourceId = R.drawable.solid_blue_rh_spread_fingers;
                image.setBackgroundResource(poseImageResourceId);
                break;
            case WAVE_IN:
                poseImageResourceId = R.drawable.solid_blue_rh_wave_left;
                image.setBackgroundResource(poseImageResourceId);
                break;
            case WAVE_OUT:
                poseImageResourceId = R.drawable.solid_blue_rh_wave_right;
                image.setBackgroundResource(poseImageResourceId);
                break;
        }
    }

    private class ItemsAdapter extends ArrayAdapter<Pose> {

        private List<Pose> items;

        public ItemsAdapter(Context context, List<Pose> items) {
            super(context, R.layout.myoposeimage_layout, items.toArray(new Pose[]{}));
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.myoposeimage_layout, null);
            }

            Pose actualItem = items.get(position);

            ImageView image = (ImageView) v.findViewById(R.id.myoPoseImage);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            image.setBackgroundColor(Color.BLUE);

            int poseImageResourceId;
            switch (actualItem) {
                case FIST:
                    poseImageResourceId = R.drawable.solid_blue_rh_fist;
                    image.setBackgroundResource(poseImageResourceId);
                    break;
                case THUMB_TO_PINKY:
                    poseImageResourceId = R.drawable.solid_blue_rh_pinky_thumb;
                    image.setBackgroundResource(poseImageResourceId);
                    break;
                case FINGERS_SPREAD:
                    poseImageResourceId = R.drawable.solid_blue_rh_spread_fingers;
                    image.setBackgroundResource(poseImageResourceId);
                    break;
                case WAVE_IN:
                    poseImageResourceId = R.drawable.solid_blue_rh_wave_left;
                    image.setBackgroundResource(poseImageResourceId);
                    break;
                case WAVE_OUT:
                    poseImageResourceId = R.drawable.solid_blue_rh_wave_right;
                    image.setBackgroundResource(poseImageResourceId);
                    break;
            }

            return v;
        }
    }

}
