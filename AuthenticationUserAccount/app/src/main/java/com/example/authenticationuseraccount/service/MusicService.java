package com.example.authenticationuseraccount.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

public class MusicService extends MediaSessionService {
    private ExoPlayer mPlayer;
    private MediaSession mediaSession = null;

    @UnstableApi
    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer = new ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, true)
                .setHandleAudioBecomingNoisy(true)
                .setTrackSelector(new DefaultTrackSelector(this))
                .build();

        mediaSession = new MediaSession.Builder(this, mPlayer).build();

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null && intent.hasExtra("mp3Url")) {
            String mp3Url = intent.getStringExtra("mp3Url");
            // Here, you can use ExoPlayer to play the song from the provided URL
            // Example: mPlayer.setMediaItem(MediaItem.fromUri(mp3Url));
            // Make sure to handle buffering, playback state, etc.
            MediaItem mediaItem = MediaItem.fromUri(mp3Url);
            MediaItem mediaItem2 = MediaItem.fromUri("https://drive.google.com/uc?id=1syP2bZhjIxuUW32kxoXY_HnC1mgdgw79&export=download");
            mPlayer.addMediaItem(mediaItem);
            mPlayer.addMediaItem(mediaItem2);
            mPlayer.prepare();
            mPlayer.play();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Override
    public void onDestroy() {
        mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;
        super.onDestroy();
    }
}
