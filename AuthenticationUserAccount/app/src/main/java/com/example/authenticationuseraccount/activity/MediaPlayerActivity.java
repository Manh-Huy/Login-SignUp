package com.example.authenticationuseraccount.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
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

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

@UnstableApi
public class MediaPlayerActivity extends AppCompatActivity {
    Song mSong;
    private MediaController mediaController;
    private Button playButton;
    private Button pauseButton;
    private SeekBar seekBar;

    Boolean isSeekBarSetMax;

    private TextView tvDurationPlayed, tvDurationTotal;
    private boolean isSeeking = false;
    private Handler handler = new Handler();
    SimpleDateFormat mFormatTime = new SimpleDateFormat("mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        playButton = findViewById(R.id.play_button);
        pauseButton = findViewById(R.id.pause_button);
        seekBar = findViewById(R.id.seek_bar);
        tvDurationTotal = findViewById(R.id.tv_duration_total);
        tvDurationPlayed = findViewById(R.id.tv_duration_played);

        isSeekBarSetMax = false;

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
                            int seekPosition = progress * 1000;
                            LogUtils.i(formatDuration(seekPosition));
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

                MediaPlayerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaController != null) {

                            int currentPosition = (int) (mediaController.getCurrentPosition() / 1000);
                            seekBar.setProgress(currentPosition);
                            tvDurationPlayed.setText(mFormatTime.format(mediaController.getCurrentPosition()));

                            if (mediaController.getDuration() != C.TIME_UNSET && !isSeekBarSetMax) {
                                isSeekBarSetMax = true;
                                seekBar.setMax((int) (mediaController.getDuration() / 1000));
                                tvDurationTotal.setText(mFormatTime.format(mediaController.getDuration()));
                            }
                        }
                        handler.postDelayed(this, 1000);
                    }
                });


                mediaController.addListener(new Player.Listener() {
                    @Override
                    public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                        Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                        isSeekBarSetMax = false;
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

    private String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
