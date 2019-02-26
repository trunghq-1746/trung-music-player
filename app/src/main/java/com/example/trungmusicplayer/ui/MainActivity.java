package com.example.trungmusicplayer.ui;

import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.trungmusicplayer.R;
import com.example.trungmusicplayer.client.MediaBrowserHelper;

public class MainActivity extends AppCompatActivity {
    private MediaBrowserHelper mMediaBrowserHelper;
    private boolean mIsPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button playButton = findViewById(R.id.button_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlaying) {
                    mMediaBrowserHelper.getTransportControls().pause();
                } else {
                    mMediaBrowserHelper.getTransportControls().play();
                }
            }
        });
        Button stopButton = findViewById(R.id.button_stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaBrowserHelper.getTransportControls().stop();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMediaBrowserHelper = new MediaBrowserHelper(this);
        mMediaBrowserHelper.setControllerCallback(new MediaControllerCompatCallback());
        mMediaBrowserHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaBrowserHelper != null) mMediaBrowserHelper.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaBrowserHelper != null) mMediaBrowserHelper.onStop();
    }

    private void changeUIState(int state) {
        Button playButton = findViewById(R.id.button_play);
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playButton.setText("Pause");
            mIsPlaying = true;
        } else {
            playButton.setText("Play");
            mIsPlaying = false;
        }
    }

    private final class MediaControllerCompatCallback extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {
            super.onPlaybackStateChanged(playbackStateCompat);
            changeUIState(playbackStateCompat.getState());
        }
    }
}
