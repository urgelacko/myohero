package com.realsoft.gopro;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.realsoft.gopro.controller.GoProCommand;
import com.realsoft.gopro.event.GoProCommandEvent;
import com.realsoft.gopro.event.GoProState;
import com.realsoft.gopro.event.GoProStatus;
import com.realsoft.gopro.event.MyoStateEvent;
import com.realsoft.gopro.myo.GoProControllerMyoService;
import com.realsoft.gopro.ui.fragment.MyoPoseFragment;
import com.thalmic.myo.scanner.ScanActivity;

import de.greenrobot.event.EventBus;

public class GoProActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment())
                    .add(new GoProControllerFragment(), "FRAG_GOPRO")
                    .commit();
        }
        EventBus.getDefault().registerSticky(this);

        startService(new Intent(this, GoProControllerMyoService.class));
    }

    public void onEventMainThread(final GoProStatus powerEvent) {

        refreshStatusUi();

    }

    public void onEventMainThread(final MyoStateEvent myoState) {

        refreshStatusUi();

    }

    private void refreshStatusUi() {

        final GoProStatus status = getStateFromBus();
        // Power toggle
        final ToggleButton powerToggle = (ToggleButton) findViewById(R.id.btnPower);
        powerToggle.setChecked(!GoProState.TURNED_OFF.equals(status.getState()));

        // Recording toggle
        final ToggleButton recordingToggle = (ToggleButton) findViewById(R.id.btnRecord);
        recordingToggle.setChecked(GoProState.RECORDING.equals(status.getState()));

        // Status label
        final TextView findViewById = (TextView) findViewById(R.id.goProStatusText);
        findViewById.setText(status.getState().getMessageKey());

        MyoStateEvent myoState = MyoStateEvent.getCurrent();

        /**
         * MYO STATE
         */
        // Status label
        final TextView myoStatus = (TextView) findViewById(R.id.myoStatusText);
        myoStatus.setText(myoState.isConnected() ? R.string.myo_status_connected : R.string.myo_status_disconnected);
        // Locked label
        final TextView myoLockedStateText = (TextView) findViewById(R.id.myoLockedState);
        myoLockedStateText.setText(myoState.isLocked() ? R.string.myo_status_locked : R.string.myo_status_unlocked);
    }

    private GoProStatus getStateFromBus() {
        final GoProStatus status = EventBus.getDefault().getStickyEvent(GoProStatus.class);

        return status;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {

        EventBus.getDefault().unregister(this);
        stopService(new Intent(this, GoProControllerMyoService.class));

        super.onDestroy();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            final View powerToggle = rootView.findViewById(R.id.btnPower);
            powerToggle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {

                    final ToggleButton toggle = (ToggleButton) v;

                    final GoProCommandEvent goProEvent = new GoProCommandEvent(toggle.isChecked() ? GoProCommand.TURN_ON : GoProCommand.TURN_OFF);
                    EventBus.getDefault().post(goProEvent);
                    // Set checked state back, cause the success connect event will turn the state
                    toggle.setChecked(!toggle.isChecked());
                }
            });

            final View recordToggle = rootView.findViewById(R.id.btnRecord);
            recordToggle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {

                    final ToggleButton toggle = (ToggleButton) v;

                    final GoProCommandEvent goProEvent = new GoProCommandEvent(toggle.isChecked() ? GoProCommand.START_RECORD : GoProCommand.STOP_RECORD);
                    EventBus.getDefault().post(goProEvent);
                    // Set checked state back, cause the success connect event will turn the state
                    toggle.setChecked(!toggle.isChecked());
                }
            });

            final View myoScan = rootView.findViewById(R.id.btnScan);
            myoScan.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    startActivity(new Intent(getActivity(), ScanActivity.class));
                }
            });

            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction().add(R.id.myoPoseFragment, new MyoPoseFragment()).commit();
            }
            return rootView;
        }
    }
}
