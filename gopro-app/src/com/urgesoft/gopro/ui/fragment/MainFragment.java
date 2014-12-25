package com.urgesoft.gopro.ui.fragment;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.urgesoft.gopro.R;
import com.urgesoft.gopro.controller.BackPackStatus;
import com.urgesoft.gopro.controller.GoProCommand;
import com.urgesoft.gopro.controller.GoProSettings;
import com.urgesoft.gopro.event.GoProCommandEvent;
import com.urgesoft.gopro.event.GoProCommandResultEvent;
import com.urgesoft.gopro.event.GoProConnectCommandEvent;
import com.urgesoft.gopro.event.GoProConnectionChangeEvent;
import com.urgesoft.gopro.event.GoProErrorEvent;
import com.urgesoft.gopro.event.GoProState;
import com.urgesoft.gopro.event.GoProStatus;
import com.urgesoft.gopro.event.MyoStateEvent;
import com.urgesoft.gopro.event.ToastEvent;
import com.urgesoft.gopro.myo.GoProControllerMyoService;
import com.urgesoft.gopro.myo.MyoPoseFragment;
import com.thalmic.myo.scanner.ScanActivity;

import de.greenrobot.event.EventBus;
import io.vov.vitamio.utils.Log;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements PreviewFragment.PreviewStateChangeListener {

    private static final String T = "GoProActivity";
    public static final float DISABLED_ALPHA = 0.2f;

    private PreviewFragment preview;
    private ImageButton cameraModeBtn;
    private ToggleButton btnPreview;
    private View connectBtn;
    private ToggleButton powerToggle;
    private ToggleButton recordToggle;
    private View myoScan;
    private TextView disconnectedLabel;
    private View goProProgressBar;
    private ImageView myoLockedStateIcon;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        ComponentName componentName = getActivity().startService(new Intent(getActivity(), GoProControllerMyoService.class));
    }

    /**
     * EVENTS
     */
    public void onEventMainThread(final GoProStatus statusEvent) {
        refreshGoProStatusUi(statusEvent);
    }

    public void onEventMainThread(final GoProCommandEvent commmand) {
        goProProgressBar.setVisibility(View.VISIBLE);
    }

    public void onEventMainThread(final GoProCommandResultEvent event) {
        enableButtonsOnCommandResult(event.getCommand());

    }

    public void onEventMainThread(final GoProErrorEvent error) {
        goProProgressBar.setVisibility(View.INVISIBLE);
        refreshGoProStatusUi(EventBus.getDefault().getStickyEvent(GoProStatus.class));

        enableButtonsOnCommandResult(error.getCommand());
    }

    private void enableButtonsOnCommandResult(GoProCommand command) {
        switch (command) {
            case START_PREVIEW:
            case STOP_PREVIEW:
                btnPreview.setEnabled(true);
                break;
            case START_RECORD:
            case STOP_RECORD:
                recordToggle.setEnabled(true);
                break;
            case TURN_OFF:
            case TURN_ON:
                powerToggle.setEnabled(true);
                break;
        }
        //On connection, specific events are posted by WifiNetworkManager and progressBar is hidden there
        if (!GoProCommand.CONNECT.equals(command)) {
            goProProgressBar.setVisibility(View.INVISIBLE);
        }

    }

    public void onEventMainThread(final GoProConnectionChangeEvent event) {
        goProProgressBar.setVisibility(GoProState.CONNECTING.equals(event.getNewState()) ? View.VISIBLE : View.INVISIBLE);
    }

    public void onEventMainThread(final MyoStateEvent myoState) {
        refreshMyoStatusUi(myoState);
    }

    public void onEventMainThread(final ToastEvent message) {
        Toast.makeText(this.getActivity(), message.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void refreshGoProStatusUi(GoProStatus status) {

        boolean isNotConnected = GoProState.DISCONNECTED.equals(status.getState()) || GoProState.CONNECTING.equals(status.getState());
        // Connect btn
        connectBtn.setEnabled(isNotConnected);
        connectBtn.setAlpha(isNotConnected ? DISABLED_ALPHA : 1f);

        disconnectedLabel.setVisibility(isNotConnected ? View.VISIBLE : View.INVISIBLE);
        disconnectedLabel.setText(GoProState.DISCONNECTED.equals(status.getState()) ? R.string.gopro_tap_to_connect : status.getState().getMessageKey());

        int connectedStateComponentsVisibility = !GoProState.DISCONNECTED.equals(status.getState()) && !GoProState.CONNECTING.equals(status.getState()) ? View.VISIBLE : View.INVISIBLE;

        // Power toggle
        powerToggle.setVisibility(connectedStateComponentsVisibility);
        powerToggle.setChecked(status.getBackPackStatus().isTurnedOn());

        // Recording toggle
        recordToggle.setChecked(GoProState.RECORDING.equals(status.getState()));
        recordToggle.setVisibility(connectedStateComponentsVisibility);

        BackPackStatus backPackStatus = status.getBackPackStatus();
        btnPreview.setVisibility(backPackStatus.isTurnedOn() ? View.VISIBLE : View.GONE);

        cameraModeBtn.setVisibility(backPackStatus.isTurnedOn() ? View.VISIBLE : View.GONE);
        if (status.getBackPackStatus().isTurnedOn()) {
            switch (backPackStatus.getMode()) {
                case CAMERA:
                    cameraModeBtn.setImageResource(R.drawable.ic_action_video);
                    break;
                case PHOTO:
                    cameraModeBtn.setImageResource(R.drawable.ic_action_camera);
                    break;
                case BURST:
                    cameraModeBtn.setImageResource(R.drawable.ic_action_flags);
                    break;
                case TIMELAPSE:
                    cameraModeBtn.setImageResource(R.drawable.ic_action_timelapse);
                    break;
                default:
                    cameraModeBtn.setImageResource(R.drawable.ic_action_video);

            }
        }
        cameraModeBtn.setEnabled(!GoProState.RECORDING.equals(status.getState()));
        cameraModeBtn.setAlpha(cameraModeBtn.isEnabled() ? 1f : DISABLED_ALPHA);

        if (isNotConnected) {
            EventBus.getDefault().post(new GoProCommandEvent(GoProCommand.LOCK));
        }
    }

    private void refreshMyoStatusUi(MyoStateEvent myoState) {

        float alpha = MyoStateEvent.getCurrent().isConnected() ? 1f : 0.2f;
        // Locked icon
        myoLockedStateIcon.setImageResource(myoState.isLocked() ? R.drawable.ic_action_lock_closed_red : R.drawable.ic_lock_open);
        myoLockedStateIcon.setAlpha(alpha);
        //Logo
        myoScan.setEnabled(!MyoStateEvent.getCurrent().isConnected());
        myoScan.setAlpha(alpha);
    }


    @Override
    public void onPreviewStarted() {
        btnPreview.setChecked(true);
    }

    @Override
    public void onPreviewStopped() {
        btnPreview.setChecked(false);
    }

    @Override
    public void onStart() {

        super.onStart();

        initComponents();

        EventBus.getDefault().registerSticky(this);
    }

    private void initComponents() {
        myoScan = (ImageView) getActivity().findViewById(R.id.myoLogo);
        myoLockedStateIcon = (ImageView) getActivity().findViewById(R.id.myoLockedIcon);

        disconnectedLabel = (TextView) getActivity().findViewById(R.id.goProDisconnectedLabel);
        goProProgressBar = getActivity().findViewById(R.id.goproProgressBar);

        connectBtn = getActivity().findViewById(R.id.goProLogo);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                EventBus.getDefault().post(new GoProConnectCommandEvent());
            }
        });

        powerToggle = (ToggleButton) getActivity().findViewById(R.id.btnPower);
        powerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final ToggleButton toggle = (ToggleButton) v;
                toggle.setEnabled(false);

                final GoProCommandEvent goProEvent = new GoProCommandEvent(toggle.isChecked() ? GoProCommand.TURN_ON : GoProCommand.TURN_OFF);
                EventBus.getDefault().post(goProEvent);
                // Set checked state back, cause the success connect event will turn the state
                toggle.setChecked(!toggle.isChecked());
            }
        });

        recordToggle = (ToggleButton) getActivity().findViewById(R.id.btnRecord);
        recordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final ToggleButton toggle = (ToggleButton) v;
                toggle.setEnabled(false);

                final GoProCommandEvent goProEvent = new GoProCommandEvent(toggle.isChecked() ? GoProCommand.START_RECORD : GoProCommand.STOP_RECORD);
                EventBus.getDefault().post(goProEvent);
                // Set checked state back, cause the success connect event will turn the state
                toggle.setChecked(!toggle.isChecked());
            }
        });

        myoScan = getActivity().findViewById(R.id.myoLogo);
        myoScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!btAdapter.enable()) {
                    Log.e(T, "Bluetooth enable error!");
                    return;
                }

                startActivity(new Intent(getActivity(), ScanActivity.class));
            }
        });
        btnPreview = (ToggleButton) getActivity().findViewById(R.id.btnGoProPreview);
        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final ToggleButton toggle = (ToggleButton) v;
                toggle.setEnabled(false);

                preview.setPreviewOn(toggle.isChecked());

                final GoProCommandEvent goProEvent = new GoProCommandEvent(toggle.isChecked() ? GoProCommand.START_PREVIEW : GoProCommand.STOP_PREVIEW);
                EventBus.getDefault().post(goProEvent);
            }
        });

        cameraModeBtn = (ImageButton) getActivity().findViewById(R.id.cameraModeBtn);
        cameraModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getActivity(), cameraModeBtn) {

                };
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.camera_mode, popup.getMenu());


                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        GoProSettings.Mode mode = GoProSettings.Mode.forResourceId(item.getItemId());
                        if (null != mode) {
                            EventBus.getDefault().post(new GoProCommandEvent(GoProCommand.SET_CAMERA_MODE, mode));
                        }

                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });

        preview = (PreviewFragment) getFragmentManager().findFragmentById(R.id.previewFragment);
        preview.setListener(this);
    }

    @Override
    public void onStop() {

        preview.setListener(null);
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    public void onDestroy() {

        getActivity().stopService(new Intent(this.getActivity(), GoProControllerMyoService.class));

        super.onDestroy();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.myoPoseFragment, new MyoPoseFragment()).commit();
        }

        return rootView;
    }
}
