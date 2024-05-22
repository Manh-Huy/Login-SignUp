package com.example.authenticationuseraccount.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.media3.ui.PlayerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.Song;
import com.example.authenticationuseraccount.service.MusicService;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutionException;

public class MediaPlayerActivity extends AppCompatActivity {

    PlayerView playerView;

    Song mSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        playerView = findViewById(R.id.media3);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mSong  = (Song) bundle.getSerializable("SongObject");
            if(mSong != null){
                LogUtils.d(mSong.getSongURL());
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, MusicService.class));
        ListenableFuture<MediaController> controllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();
        controllerFuture.addListener(() -> {
            // Call controllerFuture.get() to retrieve the MediaController.
            // MediaController implements the Player interface, so it can be
            // attached to the PlayerView UI component.
            try {
                MediaController mediaController = controllerFuture.get();
                playerView.setPlayer(controllerFuture.get());
                Intent intent = new Intent(this, MusicService.class);
                intent.putExtra("mp3Url", mSong.getSongURL());
                startService(intent);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, MoreExecutors.directExecutor());
    }
}