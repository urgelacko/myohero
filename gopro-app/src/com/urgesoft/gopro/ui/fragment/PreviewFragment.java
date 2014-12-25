package com.urgesoft.gopro.ui.fragment;


import android.app.Fragment;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.urgesoft.gopro.MyoHeroApplication;
import com.urgesoft.gopro.R;
import com.urgesoft.gopro.event.GoProStatus;

import de.greenrobot.event.EventBus;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreviewFragment extends Fragment implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback {

    private static final String TAG = "MediaPlayerDemo";

    private static final String DEV_STREAM_URL = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
    private static final String STREAM_URL = "http://10.5.5.9:8080/live/amba.m3u8";

    private int mVideoWidth;
    private int mVideoHeight;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mPreview;
    private RelativeLayout mPreviewContainer;
    private SurfaceHolder holder;
    private Bundle extras;
    private static final String MEDIA = "media";
    private static final int LOCAL_AUDIO = 1;
    private static final int STREAM_AUDIO = 2;
    private static final int RESOURCES_AUDIO = 3;
    private static final int LOCAL_VIDEO = 4;
    private static final int STREAM_VIDEO = 5;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
    private TextView previewToggleLabel;
    private ProgressBar progress;

    private boolean previewOn;

    private PreviewStateChangeListener listener;
//    private boolean initialized;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_preview, container, false);
        return inflate;
    }

    public void onEventMainThread(GoProStatus status) {

        if (previewOn && status.getBackPackStatus().isTurnedOn() && status.getBackPackStatus().isPreviewEnabled()) {
            if (!isPlaying()) {
                prepareMediaPlayer();
            }
        } else {
            stopVideo();
            previewOn = false;
        }

//        if (GoProState.CONNECTING.equals(status.getState()) || GoProState.DISCONNECTED.equals(status.getState()) || GoProState.CONNECTED.equals(status.getState())) {

        if (!status.getBackPackStatus().isTurnedOn()) {
            this.getView().setVisibility(View.INVISIBLE);
        } else {
            this.getView().setVisibility(View.VISIBLE);
        }
    }

    private boolean isPlaying() {
        return null != mMediaPlayer && mMediaPlayer.isPlaying();
    }

    @Override
    public void onStart() {

        super.onStart();

        if (!LibsChecker.checkVitamioLibs(this.getActivity())) {
            return;
        }

        mPreview = (SurfaceView) getActivity().findViewById(R.id.previewVideo);
        mPreviewContainer = (RelativeLayout) getActivity().findViewById(R.id.previewContainer);
        mPreviewContainer.setVisibility(View.INVISIBLE);

        holder = mPreview.getHolder();
        holder.setFormat(PixelFormat.RGBA_8888);
        holder.addCallback(this);

        previewToggleLabel = (TextView) getActivity().findViewById(R.id.previewToggleLabel);
        previewToggleLabel.setVisibility(View.GONE);

        progress = (ProgressBar) getActivity().findViewById(R.id.previewProgress);
        progress.setVisibility(View.GONE);

        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onStop() {

        EventBus.getDefault().unregister(this);

        super.onStop();
    }


    private void prepareMediaPlayer() {

        stopVideo();

        try {
            mMediaPlayer = new MediaPlayer(this.getActivity());
            mMediaPlayer.setDataSource(!MyoHeroApplication.DEV_MODE ? STREAM_URL : DEV_STREAM_URL);
            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

            progress.setVisibility(View.VISIBLE);
            previewToggleLabel.setText(R.string.gopro_preview_loading);
            if (null != listener) {
                listener.onPreviewStarted();
            }
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
    }


    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.v(TAG, "onVideoSizeChanged called");
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void onCompletion(MediaPlayer arg0) {
        Log.v(TAG, "onCompletion called");
        stopVideo();
    }


    public void onPrepared(MediaPlayer mediaplayer) {
        Log.d(TAG, "onPrepared called");
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        Log.v(TAG, "surfaceChanged called");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(TAG, "surfaceCreated called");
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.v(TAG, "surfaceDestroyed called");
    }

    @Override
    public void onPause() {

        holder.removeCallback(this);

        stopVideo();
        previewOn = false;

        super.onPause();
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;

        mPreviewContainer.setVisibility(View.INVISIBLE);

        progress.setVisibility(View.INVISIBLE);
        previewToggleLabel.setVisibility(View.VISIBLE);
        previewToggleLabel.setText(R.string.gopro_preview_disabled);
    }

    private void startVideoPlayback() {
        Log.v(TAG, "startVideoPlayback");

        adjustAspectRatio(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
        mMediaPlayer.start();

        mPreviewContainer.setVisibility(View.VISIBLE);

        previewToggleLabel.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.INVISIBLE);
    }

    private void stopVideo() {
        releaseMediaPlayer();
        doCleanUp();
        if (null != listener) {
            listener.onPreviewStopped();
        }
    }

    public void setPreviewOn(boolean previewOn) {
        this.previewOn = previewOn;
    }

    public boolean isPreviewOn() {
        return previewOn;
    }

    /**
     * Sets the TextureView transform to preserve the aspect ratio of the video.
     */
    private void adjustAspectRatio(int videoWidth, int videoHeight) {
        int viewWidth = this.getView().getWidth();
        int viewHeight = this.getView().getHeight();
        double aspectRatio = (double) videoHeight / videoWidth;

        int newWidth, newHeight;
        if (viewHeight > (int) (viewWidth * aspectRatio)) {
            // limited by narrow width; restrict height
            newWidth = viewWidth;
            newHeight = (int) (viewWidth * aspectRatio);
        } else {
            // limited by short height; restrict width
            newWidth = (int) (viewHeight / aspectRatio);
            newHeight = viewHeight;
        }
        int xoff = (viewWidth - newWidth) / 2;
        int yoff = (viewHeight - newHeight) / 2;
        Log.v(TAG, "video=" + videoWidth + "x" + videoHeight + " view=" + viewWidth + "x" + viewHeight
                + " newView=" + newWidth + "x" + newHeight + " off=" + xoff + "," + yoff);

        holder.setFixedSize(newWidth, newHeight);
    }


    public void setListener(PreviewStateChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    public interface PreviewStateChangeListener {

        void onPreviewStarted();

        void onPreviewStopped();

    }
}
