package com.example.authenticationuseraccount;

import android.content.Context;
import android.net.Uri;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

public class AudioPlayer {
    private final ExoPlayer player;

    public AudioPlayer(Context context) {
        player = new ExoPlayer.Builder(context).build();
    }

    public void play(Uri uri) {
        MediaItem mediaItem = MediaItem.fromUri(uri);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    public void stop() {
        player.stop();
    }

    public void release() {
        player.release();
    }
}

