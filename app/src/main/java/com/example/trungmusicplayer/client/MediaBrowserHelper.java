package com.example.trungmusicplayer.client;

import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.example.trungmusicplayer.service.MediaPlayerService;

public class MediaBrowserHelper {
    private static final String TAG = "MediaBrowserHelper";
    private Context mContext;
    private MediaBrowserCompat mMediaBrowser;
    private MediaControllerCompat mMediaController;
    private MediaBrowserCompat.ConnectionCallback mConnectionCallback;
    private MediaControllerCompat.Callback mControllerCallback;

    public MediaBrowserHelper(Context context) {
        Log.d(TAG, "MediaBrowserHelper: ");
        mContext = context;
        mConnectionCallback = new MediaBrowserConnectionCallback();
    }

    public MediaControllerCompat.TransportControls getTransportControls() {
        return mMediaController.getTransportControls();
    }

    public void setControllerCallback(MediaControllerCompat.Callback callback) {
        mControllerCallback = callback;
    }

    public void onStart() {
        if (mMediaBrowser == null) {
            mMediaBrowser = new MediaBrowserCompat(mContext,
                new ComponentName(mContext, MediaPlayerService.class),
                mConnectionCallback,
                null);
            mMediaBrowser.connect();
        }
    }

    public void onStop() {
        if (mMediaBrowser != null && mMediaBrowser.isConnected()) {
            mMediaBrowser.disconnect();
            mMediaBrowser = null;
            Log.d(TAG, "disconnect: ");
        }
    }

    private final class MediaBrowserConnectionCallback
        extends MediaBrowserCompat.ConnectionCallback {
        @Override
        public void onConnected() {
            Log.d(TAG, "onConnected: ");
            super.onConnected();
            try {
                mMediaController = new MediaControllerCompat(mContext,
                    mMediaBrowser.getSessionToken());
                mMediaController.registerCallback(mControllerCallback);
                PlaybackStateCompat playbackState = mMediaController.getPlaybackState();
                if (playbackState != null &&
                    playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    mMediaController.getTransportControls().play();
                } else {
                    mMediaController.getTransportControls().pause();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
