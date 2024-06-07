package com.example.authenticationuseraccount.service;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.TaskStackBuilder;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import com.example.authenticationuseraccount.activity.MainActivity;
import com.example.authenticationuseraccount.activity.MediaPlayerActivity;
import com.example.authenticationuseraccount.common.LogUtils;

@UnstableApi
public class MusicService extends MediaSessionService {
    private ExoPlayer mPlayer;
    private MediaSession mediaSession;

    @UnstableApi
    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer = new ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, true)
                .setHandleAudioBecomingNoisy(true)
                .setTrackSelector(new DefaultTrackSelector(this))
                .build();

        Intent intent = new Intent(this, MainActivity.class);
        /*intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);*/
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        //PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        MediaSession.Callback callback = new MediaSession.Callback() {
            @Override
            public MediaSession.ConnectionResult onConnect(MediaSession session, MediaSession.ControllerInfo controller) {
                //LogUtils.ApplicationLogD("Callback controller info: " + controller.toString());
                return MediaSession.Callback.super.onConnect(session, controller);
            }
        };

        mediaSession = new MediaSession.Builder(this, mPlayer)
                .setSessionActivity(pendingIntent)
                .setCallback(callback)
                .build();

    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        //LogUtils.ApplicationLogD("onGetSession Controller Info: "+ controllerInfo.toString());
        return mediaSession;
    }

    @Override
    public void onDestroy() {
        LogUtils.ApplicationLogI("OnDestroy Service Killed");
        MediaItemHolder.getInstance().getMediaController().release();
        MediaItemHolder.getInstance().destroy();
        mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(@Nullable Intent rootIntent) {
        LogUtils.ApplicationLogI("MusicService: App is dissmised");
        Player player = mediaSession.getPlayer();
        if (!player.getPlayWhenReady()
                || player.getMediaItemCount() == 0
                || player.getPlaybackState() == Player.STATE_ENDED) {
            // Stop the service if not playing, continue playing in the background
            // otherwise.
            stopSelf();
        }
    }


}
