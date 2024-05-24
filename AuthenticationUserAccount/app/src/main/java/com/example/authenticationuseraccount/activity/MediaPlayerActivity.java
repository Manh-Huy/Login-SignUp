package com.example.authenticationuseraccount.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.media3.ui.PlayerControlView;
import androidx.media3.ui.PlayerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.Song;
import com.example.authenticationuseraccount.service.MusicService;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutionException;

@UnstableApi
public class MediaPlayerActivity extends AppCompatActivity {
    Song mSong;
    private MediaController mediaController;
    private Button playButton;
    private Button pauseButton;
    private SeekBar seekBar;
    private boolean isSeeking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        playButton = findViewById(R.id.play_button);
        pauseButton = findViewById(R.id.pause_button);
        seekBar = findViewById(R.id.seek_bar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mSong = (Song) bundle.getSerializable("SongObject");
            if (mSong != null) {
                LogUtils.d(mSong.getSongURL());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, MusicService.class));


        MediaController.Builder builder = new MediaController.Builder(this, sessionToken);
        ListenableFuture<MediaController> controllerFuture = builder.buildAsync();

        controllerFuture.addListener(() -> {
            try {
                MediaController mediaController = controllerFuture.get();
                playButton.setOnClickListener(v -> mediaController.play());
                pauseButton.setOnClickListener(v -> mediaController.pause());
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            isSeeking = true;
                            long seekPosition = (progress * mediaController.getDuration()) / 100;
                            mediaController.seekTo(seekPosition);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        isSeeking = true;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        isSeeking = false;
                    }
                });

                // Update seek bar as the audio plays
                mediaController.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        //if (!isSeeking && (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)) {
                        if (!isSeeking ) {
                            long duration = mediaController.getDuration();
                            long currentPosition = mediaController.getCurrentPosition();
                            int progress = 0;
                            if (duration > 0) {
                                progress = (int) ((currentPosition * 100) / duration);
                            }
                            seekBar.setProgress(progress);
                            LogUtils.e("changingggg");
                        }else {
                            LogUtils.e("notchanggginggg");
                        }
                    }


                });




                Intent intent = new Intent(this, MusicService.class);
                intent.putExtra("mp3Url", mSong.getSongURL());
                startService(intent);

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, MoreExecutors.directExecutor());
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (mediaController != null) {
            mediaController.release();
            mediaController = null;
        }
    }
}
