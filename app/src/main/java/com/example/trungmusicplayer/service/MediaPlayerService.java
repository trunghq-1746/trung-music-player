package com.example.trungmusicplayer.service;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.example.trungmusicplayer.R;
import com.example.trungmusicplayer.service.notifications.MediaNotificationManager;

import java.util.List;

public class MediaPlayerService extends MediaBrowserServiceCompat {
    private static final String TAG = "MediaPlayerService";
    private MediaSessionCompat mMediaSession;
    private MediaPlayer mMediaPlayer;
    private PlaybackStateCompat.Builder mPlaybackStateBuilder;
    private MediaNotificationManager mMediaNotificationManager;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        initMediaPlayer();
        initMediaSession();
        initPlaybackState();
        mMediaNotificationManager = new MediaNotificationManager(this, this);
        startForeground(MediaNotificationManager.NOTIFICATION_ID,
            mMediaNotificationManager.buildNotification(false));
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String s, int i, @Nullable Bundle bundle) {
        if (TextUtils.equals(s, getPackageName())) {
            return new BrowserRoot("MediaPlayer", null);
        }
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String s,
                               @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        mMediaPlayer.release();
        mMediaSession.release();
    }

    private void initMediaPlayer() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.emlacodau);
        mMediaPlayer.setVolume(1.0f, 1.0f);
    }

    private void initMediaSession() {
        mMediaSession = new MediaSessionCompat(this, "Music Service");
        MediaSessionCompatCallback sessionCompatCallback = new MediaSessionCompatCallback();
        mMediaSession.setCallback(sessionCompatCallback);
        setSessionToken(mMediaSession.getSessionToken());
    }

    private void initPlaybackState() {
        mPlaybackStateBuilder = new PlaybackStateCompat.Builder();
        mPlaybackStateBuilder
            .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY);
    }

    private void setMediaPlaybackState(int state) {
        switch (state) {
            case PlaybackStateCompat.STATE_PLAYING:
                mPlaybackStateBuilder.setActions(
                    PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE);
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                mPlaybackStateBuilder.setActions(
                    PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY);
        }
        mPlaybackStateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1);
        mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());
    }

    private final class MediaSessionCompatCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            Log.d(TAG, "onPlay: ");
            super.onPlay();
            ContextCompat.startForegroundService(getApplicationContext(),
                new Intent(getApplicationContext(), MediaPlayerService.class));
            mMediaSession.setActive(true);
            startForeground(MediaNotificationManager.NOTIFICATION_ID,
                mMediaNotificationManager.buildNotification(true));
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            mMediaPlayer.start();
        }

        @Override
        public void onPause() {
            Log.d(TAG, "onPause: ");
            super.onPause();
            startForeground(MediaNotificationManager.NOTIFICATION_ID,
                mMediaNotificationManager.buildNotification(false));
            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
            if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
        }

        @Override
        public void onStop() {
            Log.d(TAG, "onStop: ");
            super.onStop();
            if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
            stopSelf();
            setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
        }
    }
}
