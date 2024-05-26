package com.example.authenticationuseraccount.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.palette.graphics.Palette;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.DiscViewPagerAdapter;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.fragment.DiscFragment;
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

    public static DiscViewPagerAdapter adapterDisc;

    ViewPager viewPagerDisc;
    private SeekBar seekBar;

    private TextView tvDurationPlayed, tvDurationTotal, tvSongName, tvArtistName;
    private ImageView imgCoverArt, imgShuffle, imgPrev, imgPlayPause, imgNext, imgRepeat;

    private DiscFragment discFragment;

    private boolean isSeekBarSetMax, isSeeking;
    private Handler handler = new Handler();
    SimpleDateFormat mFormatTime = new SimpleDateFormat("mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        initView();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mSong = (Song) bundle.getSerializable("SongObject");
            if (mSong != null) {
                handleSongMetaData(mSong);
            }
        }
    }

    private void handleSongMetaData(Song song) {
        tvSongName.setText(song.getName());
        tvArtistName.setText(song.getArtist());
        Glide.with(this)
                .asBitmap()
                .load(song.getImageURL())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@Nullable Palette palette) {
                                Palette.Swatch swatch = palette.getDominantSwatch();
                                if (swatch != null) {
                                    ImageView gredient = findViewById(R.id.imageViewGredient);
                                    NestedScrollView mContainer = findViewById(R.id.mContainer);
                                    gredient.setBackgroundResource(R.drawable.gredient_bg);
                                    mContainer.setBackgroundResource(R.drawable.main_bg);
                                    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                            new int[]{swatch.getRgb(), 0x00000000});
                                    gredient.setBackground(gradientDrawable);
                                    GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                            new int[]{swatch.getRgb(), swatch.getRgb()});
                                    mContainer.setBackground(gradientDrawableBg);
                                    tvSongName.setTextColor(swatch.getTitleTextColor());
                                    tvArtistName.setTextColor(swatch.getBodyTextColor());
                                } else {
                                    ImageView gredient = findViewById(R.id.imageViewGredient);
                                    NestedScrollView mContainer = findViewById(R.id.mContainer);
                                    gredient.setBackgroundResource(R.drawable.gredient_bg);
                                    mContainer.setBackgroundResource(R.drawable.main_bg);
                                    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                            new int[]{0xff000000, 0x00000000});
                                    gredient.setBackground(gradientDrawable);
                                    GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                            new int[]{0xff000000, 0xff000000});
                                    mContainer.setBackground(gradientDrawableBg);
                                    tvSongName.setTextColor(Color.WHITE);
                                    tvArtistName.setTextColor(Color.DKGRAY);
                                }
                                ImageAnimation(MediaPlayerActivity.this,resource);
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public void ImageAnimation(Context context, Bitmap bitmap) {
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                discFragment.setAnim(context, bitmap);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                discFragment.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        discFragment.startAnimation(animOut);
    }
    private void initView() {
        viewPagerDisc = findViewById(R.id.viewPagerdianhac);
        tvSongName = findViewById(R.id.tv_song_name);
        tvArtistName = findViewById(R.id.tv_song_artist);
        tvDurationTotal = findViewById(R.id.tv_duration_total);
        tvDurationPlayed = findViewById(R.id.tv_duration_played);
        seekBar = findViewById(R.id.seekBar);
        imgNext = findViewById(R.id.img_next);
        imgPrev = findViewById(R.id.img_prev);
        imgRepeat = findViewById(R.id.img_repeat);
        imgShuffle = findViewById(R.id.img_shuffle);
        imgPlayPause = findViewById(R.id.img_play_pause);
        imgCoverArt = findViewById(R.id.imageViewGredient);

        viewPagerDisc = findViewById(R.id.viewPagerdianhac);
        discFragment = new DiscFragment();
        adapterDisc = new DiscViewPagerAdapter(getSupportFragmentManager());
        adapterDisc.AddFragment(discFragment);
        viewPagerDisc.setAdapter(adapterDisc);
        discFragment = (DiscFragment) adapterDisc.getItem(0);

        isSeekBarSetMax = false;
        isSeeking = false;
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

                imgPlayPause.setOnClickListener(v -> {
                    if (mediaController.isPlaying()) {
                        mediaController.pause();
                    } else {
                        mediaController.play();
                    }
                });

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
