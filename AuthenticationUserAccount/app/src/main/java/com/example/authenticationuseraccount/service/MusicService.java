package com.example.authenticationuseraccount.service;

import static com.example.authenticationuseraccount.MyApplication.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.session.MediaNotification;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import androidx.media3.ui.PlayerNotificationManager;

import com.example.authenticationuseraccount.activity.MainActivity;
import com.example.authenticationuseraccount.activity.MediaPlayerActivity;

@UnstableApi
public class MusicService extends MediaSessionService {
    private ExoPlayer mPlayer;
    private MediaSession mediaSession ;

    @UnstableApi
    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer = new ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, true)
                .setHandleAudioBecomingNoisy(true)
                .setTrackSelector(new DefaultTrackSelector(this))
                .build();

        

        Intent intent = new Intent(this, MediaPlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        mediaSession = new MediaSession.Builder(this, mPlayer)
                .setSessionActivity(pendingIntent)
                .build();


        //setMediaNotificationProvider();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null && intent.hasExtra("mp3Url")) {
            String mp3Url = intent.getStringExtra("mp3Url");
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
