package com.urgesoft.gopro.ui.fragment;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreviewFragmentSetSurface extends Fragment implements SurfaceHolder.Callback, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private static final String TAG = "MediaPlayerDemo";
    private int mVideoWidth;
    private int mVideoHeight;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceView;
    private String path;

    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
    private SurfaceHolder vidHolder;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(com.urgesoft.gopro.R.layout.fragment_preview, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!LibsChecker.checkVitamioLibs(this.getActivity()))
            return;
        mSurfaceView = (SurfaceView) getActivity().findViewById(com.urgesoft.gopro.R.id.previewVideo);
        vidHolder = mSurfaceView.getHolder();
        vidHolder.addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        playVideo(holder.getSurface());
    }

    @SuppressLint("NewApi")
    private void playVideo(Surface surfaceTexture) {
        doCleanUp();
        try {

//            path = "http://10.5.5.9:8080/live/amba.m3u8";
            path = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
            if (path == "") {
                // Tell the user to provide a media file URL.
                Toast.makeText(
                        this.getActivity(),
                        "Please edit MediaPlayerDemo_setSurface Activity, "
                                + "and set the path variable to your media file path."
                                + " Your media file must be stored on sdcard.", Toast.LENGTH_LONG).show();
                return;
            }
            // Create a new media player and set the listeners
            mMediaPlayer = new MediaPlayer(this.getActivity(), true);
//            mMediaPlayer.
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setSurface(surfaceTexture);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
//
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
    }

    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
    }

    public void onCompletion(MediaPlayer arg0) {
        Log.d(TAG, "onCompletion called");
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        Log.d(TAG, "onPrepared called");
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed) {
            startVideoPlayback();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseMediaPlayer();
        doCleanUp();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        doCleanUp();
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
    }

    private void startVideoPlayback() {
        Log.v(TAG, "startVideoPlayback");
        adjustAspectRatio(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
        mMediaPlayer.start();
    }

    /**
     * Sets the TextureView transform to preserve the aspect ratio of the video.
     */
    private void adjustAspectRatio(int videoWidth, int videoHeight) {
        int viewWidth = mSurfaceView.getWidth();
        int viewHeight = mSurfaceView.getHeight();
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

        vidHolder.setFixedSize(newWidth, newHeight);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}