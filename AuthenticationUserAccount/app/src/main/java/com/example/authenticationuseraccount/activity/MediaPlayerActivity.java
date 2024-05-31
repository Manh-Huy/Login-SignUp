package com.example.authenticationuseraccount.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.media3.common.MediaMetadata;
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
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.fragment.DiscFragment;
import com.example.authenticationuseraccount.model.ListenHistory;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.service.MusicService;
import com.example.authenticationuseraccount.service.UIThread;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@UnstableApi
public class MediaPlayerActivity extends AppCompatActivity {
    private static final String TAG = "MediaPlayerActivity23";
    private MediaController mMediaController;
    MaterialCheckBox materialCheckBox;
    public static DiscViewPagerAdapter adapterDisc;
    ViewPager viewPagerDisc;
    private SeekBar seekBar;
    private TextView tvDurationPlayed, tvDurationTotal, tvSongName, tvArtistName;
    private ImageView imgCoverArt, imgShuffle, imgPrev, imgPlayPause, imgNext, imgRepeat;
    private DiscFragment discFragment;
    private boolean isSeekBarSetMax, isSeeking, isSetupMetaData, isSaveUserHistoryTriggered;
    private Handler handler = new Handler();
    SimpleDateFormat mFormatTime = new SimpleDateFormat("mm:ss");
    Song mSong;
    Disposable mDisposable;

    Player.Listener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.ApplicationLogI("onCreat Called!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        initView();

        if (mMediaController == null) {
            mMediaController = MediaItemHolder.getInstance().getMediaController();
            initMediaController();
            LogUtils.ApplicationLogD("Itssss Aliveeeeeeeee!!!!!!");
        } else {
            mMediaController.removeListener(mListener);
            initMediaController();
            LogUtils.ApplicationLogD("co bien roi dai vuong oi");
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mSong = (Song) bundle.getSerializable("SongObject");
            int number = 0;
            if (mSong != null) {
                MediaItem mediaItem = MediaItem.fromUri(mSong.getSongURL());
                mMediaController.addMediaItem(mediaItem);
                MediaItemHolder.getInstance().getListMediaItem().add(mediaItem);
                MediaItemHolder.getInstance().getListSongs().add(mSong);

                number = MediaItemHolder.getInstance().getMediaController().getMediaItemCount();
                LogUtils.ApplicationLogD("Number of MediaItem: " + mMediaController.getMediaItemCount());
                LogUtils.ApplicationLogD("Number of MediaItem SingleTon: " + number);
                LogUtils.ApplicationLogD("Number of Song: " + MediaItemHolder.getInstance().getListSongs().size());
            } else {
                number = MediaItemHolder.getInstance().getMediaController().getMediaItemCount();
                LogUtils.ApplicationLogD("Number of MediaItem: " + mMediaController.getMediaItemCount());
                LogUtils.ApplicationLogD("Number of MediaItem SingleTon: " + number);
                LogUtils.ApplicationLogD("Number of Song: " + MediaItemHolder.getInstance().getListSongs().size());
            }
        }

    }

    private void initMediaController() {
        mListener = new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                    LogUtils.ApplicationLogD("MEDIA_ITEM_TRANSITION_REASON_AUTO");
                    isSetupMetaData = false;
                    isSaveUserHistoryTriggered = false;
                }
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT) {
                    LogUtils.ApplicationLogD("MEDIA_ITEM_TRANSITION_REASON_REPEAT");
                    isSetupMetaData = false;
                    isSaveUserHistoryTriggered = false;
                }
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED) {
                    LogUtils.ApplicationLogD("MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED");
                    mMediaController.prepare();
                    isSetupMetaData = false;
                    isSaveUserHistoryTriggered = false;
                }
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) {
                    LogUtils.ApplicationLogD("MEDIA_ITEM_TRANSITION_REASON_SEEK");
                    isSetupMetaData = false;
                    isSaveUserHistoryTriggered = false;
                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState == Player.STATE_IDLE) {
                    LogUtils.ApplicationLogD("Player is IDLE");
                }
                if (playbackState == Player.STATE_BUFFERING) {
                    LogUtils.ApplicationLogD("Player is Buffering");
                }
                if (playbackState == Player.STATE_READY) {
                    LogUtils.ApplicationLogD("Player is Ready");
                    mMediaController.play();
                    LogUtils.ApplicationLogD("Player is play " + mMediaController.getMediaMetadata().title + " at position: " + mMediaController.getCurrentMediaItemIndex());
                }
                if (playbackState == Player.STATE_ENDED) {
                    LogUtils.ApplicationLogD("Player is Ended");
                }
            }

            @Override
            public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
                Player.Listener.super.onMediaMetadataChanged(mediaMetadata);
                LogUtils.ApplicationLogD("MetaDataChanged");
                prepareSongMetaData(mediaMetadata);
                setupSeekBar();
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                if (isPlaying) {
                    //LogUtils.ApplicationLogD("Player is Playing");
                    imgPlayPause.setImageResource(R.drawable.ic_pause);
                } else {
                    //LogUtils.ApplicationLogD("Player is Freezing");
                    imgPlayPause.setImageResource(R.drawable.ic_pause);

                }
            }
        };
        mMediaController.addListener(mListener);
        mMediaController.addListener(UIThread.getInstance().getListener());
        initButton();
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
        materialCheckBox = findViewById(R.id.img_love);

        //Rotation Disc ViewPager
        viewPagerDisc = findViewById(R.id.viewPagerdianhac);
        discFragment = new DiscFragment();
        adapterDisc = new DiscViewPagerAdapter(getSupportFragmentManager());
        adapterDisc.AddFragment(discFragment);
        viewPagerDisc.setAdapter(adapterDisc);
        discFragment = (DiscFragment) adapterDisc.getItem(0);

        isSetupMetaData = false;
    }

    @Override
    protected void onStart() {
        LogUtils.ApplicationLogI("onStart: Called");
        super.onStart();
    }
    @Override
    protected void onPause() {
        LogUtils.ApplicationLogI("onPause: ");
        mMediaController = MediaItemHolder.getInstance().getMediaController();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MediaItemHolder.getInstance().getMediaController() != null) {
            if (MediaItemHolder.getInstance().getMediaController().getCurrentMediaItem().mediaMetadata != null) {
                prepareSongMetaData(MediaItemHolder.getInstance().getMediaController().getCurrentMediaItem().mediaMetadata);
                setupContinueSeekBar();
            }

        } else {
            LogUtils.ApplicationLogE("Media Null");
        }
    }

    private void setupContinueSeekBar() {
        if (mMediaController.getDuration() != C.TIME_UNSET) {
            int totalDuration = (int) mMediaController.getDuration();
            seekBar.setMax(totalDuration / 1000);
            tvDurationTotal.setText(mFormatTime.format(totalDuration));
        }
        MediaPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMediaController != null) {
                    int currentPosition = (int) (mMediaController.getCurrentPosition() / 1000);
                    seekBar.setProgress(currentPosition);
                    tvDurationPlayed.setText(mFormatTime.format(mMediaController.getCurrentPosition()));
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void initButton() {
        imgPlayPause.setOnClickListener(v -> {
            if (mMediaController.isPlaying()) {
                mMediaController.pause();
                imgPlayPause.setImageResource(R.drawable.ic_play);
            } else {
                mMediaController.play();
                imgPlayPause.setImageResource(R.drawable.ic_pause);
            }
        });

        imgNext.setOnClickListener(v -> {
            mMediaController.seekToNextMediaItem();
        });

        imgPrev.setOnClickListener(v -> {
            mMediaController.seekToPreviousMediaItem();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int seekPosition = progress * 1000;
                    //LogUtils.i(formatDuration(seekPosition));
                    mMediaController.seekTo(seekPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mMediaController != null)
                    mMediaController.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mMediaController != null)
                    mMediaController.play();
            }
        });
    }

    private void setupSeekBar() {
        int totalDuration = 0;
        //Reset SeekBar
        if (mMediaController.getDuration() != C.TIME_UNSET) {
            totalDuration = (int) mMediaController.getDuration();
            seekBar.setMax(totalDuration / 1000);
            tvDurationTotal.setText(mFormatTime.format(totalDuration));
            tvDurationPlayed.setText("00:00");
        }

        //Update SeekBar Continuosly
        MediaPlayerActivity.this
                .runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMediaController != null) {
                    int currentPosition = (int) (mMediaController.getCurrentPosition() / 1000);
                    int totalDuration = (int) (mMediaController.getDuration() / 1000);
                    seekBar.setProgress(currentPosition);
                    tvDurationPlayed.setText(mFormatTime.format(mMediaController.getCurrentPosition()));

                    // Update user History
                    if (currentPosition > totalDuration / 2 && !isSaveUserHistoryTriggered) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            ListenHistory listenHistory = getSongHistory(user.getUid());
                            LogUtils.ApplicationLogI("Trigger Call Update History!");
                            triggerAPICall(listenHistory);
                            isSaveUserHistoryTriggered = true;
                        } else {
                            //triggerSaveLocal();
                            isSaveUserHistoryTriggered = true;
                        }

                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private ListenHistory getSongHistory(String uid) {

        int currentIndex = mMediaController.getCurrentMediaItemIndex();
        String songID = MediaItemHolder.getInstance().getListSongs().get(currentIndex).getSongID();
        String songName = MediaItemHolder.getInstance().getListSongs().get(currentIndex).getName();
        LogUtils.ApplicationLogD("Song about to saved: " + songName);
        DateTimeFormatter formatter = null;
        String formattedDate = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate currentDate = LocalDate.now();
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            formattedDate = currentDate.format(formatter);
        }

        return new ListenHistory(uid, songID, 1, materialCheckBox.isChecked(), formattedDate);
    }

    @SuppressLint("CheckResult")
    private void triggerAPICall(ListenHistory listenHistory) {

        ApiService.apiService.addUserListenHistory(listenHistory)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    LogUtils.ApplicationLogD("Update User History! " + listenHistory.getSongID());
                }, throwable -> {
                    LogUtils.ApplicationLogE("Upload Failed");
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.ApplicationLogI("onStop");
        /*if (mMediaController != null) {
            mMediaController.release();
            mMediaController = null;
        }*/
    }

    @Override
    protected void onDestroy() {
        LogUtils.ApplicationLogI("OnDestroy");
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        /*if (mMediaController != null) {
            mMediaController.release();
            mMediaController = null;
        }*/
        super.onDestroy();
    }

    private void populateMediaPlayerBackground(int swatch, int swatch1, int swatch2) {
        ImageView gredient = findViewById(R.id.imageViewGredient);
        NestedScrollView mContainer = findViewById(R.id.mContainer);
//        gredient.setBackgroundResource(R.drawable.gredient_bg);
//        mContainer.setBackgroundResource(R.drawable.main_bg);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{swatch, 0x00000000});
        gredient.setBackground(gradientDrawable);
        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{swatch, swatch});
        mContainer.setBackground(gradientDrawableBg);
        tvSongName.setTextColor(swatch1);
        tvArtistName.setTextColor(swatch2);
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

    private void prepareSongMetaData(MediaMetadata metadata) {
        tvSongName.setText(metadata.title);
        tvArtistName.setText(metadata.artist);
        byte[] art = metadata.artworkData;
        if (art != null) {

            Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();

                    if (swatch != null) {
                        populateMediaPlayerBackground(swatch.getRgb(), swatch.getTitleTextColor(), swatch.getBodyTextColor());
                    } else {
                        populateMediaPlayerBackground(0xff000000, Color.WHITE, Color.DKGRAY);
                    }
                    ImageAnimation(MediaPlayerActivity.this, bitmap);
                }
            });
        }
/*        Glide.get(MediaPlayerActivity.this).clearMemory();
        Glide.with(MediaPlayerActivity.this)
                .asBitmap()
                .load(metadata.artworkData)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@Nullable Palette palette) {
                                Palette.Swatch swatch = palette.getDominantSwatch();
                                if (swatch != null) {
                                    populateMediaPlayerBackground(swatch.getRgb(), swatch.getTitleTextColor(), swatch.getBodyTextColor());
                                } else {
                                    populateMediaPlayerBackground(0xff000000, Color.WHITE, Color.DKGRAY);
                                }
                                ImageAnimation(MediaPlayerActivity.this, resource);
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });*/
    }
}
