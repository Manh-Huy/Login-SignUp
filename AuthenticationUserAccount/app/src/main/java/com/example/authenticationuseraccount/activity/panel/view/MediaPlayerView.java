package com.example.authenticationuseraccount.activity.panel.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.session.PlaybackState;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.common.MediaMetadata;
import androidx.media3.session.MediaController;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.MainActivity;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout;

import java.util.concurrent.TimeUnit;


public class MediaPlayerView{
    public static final int STATE_NORMAL = 0;
    public static final int STATE_PARTIAL = 1;
    private View mRootView;
    private int mState;
    private FrameLayout mBottomSheet;
    private ConstraintLayout mControlsContainer;
    private CardView m_vCardView_Art;
    private SeekBar m_vSeekBar_Main;
    private TextView m_vTextView_CurrentDuration,m_vTextView_MaxDuration, m_vTextView_Artist,m_vTextView_Title;
    private ExtendedFloatingActionButton m_vBtn_Repeat, m_vBtn_Prev,m_vBtn_Next,m_vBtn_Shuffle;
    private FloatingActionButton m_vBtn_PlayPause;
    @MediaItemHolder.RepeatType
    public int m_vRepeatType = MediaItemHolder.REPEAT_TYPE_NONE;
    private boolean m_vCanUpdateSeekbar = true;
    private MediaController mMediaController;

    public MediaPlayerView(View rootView) {
        this.mRootView = rootView;
        this.mControlsContainer = findViewById(R.id.media_player_controls_container);
        this.mRootView.setAlpha(0.0F);
        this.m_vCardView_Art = this.mControlsContainer.findViewById(R.id.card_view_artist_art_container);
        this.m_vTextView_Title = this.mControlsContainer.findViewById(R.id.text_view_song_title);
        this.m_vTextView_Artist = this.mControlsContainer.findViewById(R.id.text_view_song_artist);
        this.m_vSeekBar_Main = this.mControlsContainer.findViewById(R.id.seek_bar_main);
        this.m_vTextView_CurrentDuration = this.mControlsContainer.findViewById(R.id.text_view_song_current_duration);
        this.m_vTextView_MaxDuration = this.mControlsContainer.findViewById(R.id.text_view_song_max_duration);
        this.m_vBtn_Repeat = findViewById(R.id.btn_repeat);
        this.m_vBtn_Prev = findViewById(R.id.btn_skip_previous);
        this.m_vBtn_PlayPause = findViewById(R.id.btn_play_pause);
        this.m_vBtn_Next = findViewById(R.id.btn_skip_next);
        this.m_vBtn_Shuffle = findViewById(R.id.btn_shuffle);

    }

    private void setOnListener() {
        if (MediaItemHolder.getInstance().getMediaController() != null) {
            mMediaController = MediaItemHolder.getInstance().getMediaController();
            LogUtils.ApplicationLogD("IT'sssssssssssss Aliveeeeeeeeeeee");
        } else {
            LogUtils.ApplicationLogD("Co Biennnnnnnnnnnnnnnnnnnnnnnnnnnn");
        }
        this.m_vSeekBar_Main.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int final_value;
            boolean isUser;

            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                this.final_value = value;
                this.isUser = fromUser;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                m_vCanUpdateSeekbar = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isUser) {
                    mMediaController.seekTo(final_value);
                }
                m_vCanUpdateSeekbar = true;
            }
        });

        this.m_vBtn_Repeat.setOnClickListener((v) -> {
            if (m_vRepeatType < 2) {
                m_vRepeatType++;
            } else {
                m_vRepeatType = MediaItemHolder.REPEAT_TYPE_NONE;
            }

            switch (m_vRepeatType) {
                case MediaItemHolder.REPEAT_TYPE_NONE:
                    this.m_vBtn_Repeat.setIconResource(leveldown.kyle.icon_packs.R.drawable.ic_repeat_24px);
                    this.m_vBtn_Repeat.setAlpha(0.5F);
                    break;

                case MediaItemHolder.REPEAT_TYPE_ONE:
                    this.m_vBtn_Repeat.setIconResource(leveldown.kyle.icon_packs.R.drawable.ic_repeat_one_24px);
                    this.m_vBtn_Repeat.setAlpha(1F);
                    break;

                case MediaItemHolder.REPEAT_TYPE_ALL:
                    this.m_vBtn_Repeat.setIconResource(leveldown.kyle.icon_packs.R.drawable.ic_repeat_24px);
                    this.m_vBtn_Repeat.setAlpha(1F);
                    break;
            }

            MediaItemHolder.getInstance().getMediaController().setRepeatMode(this.m_vRepeatType);
        });

        this.m_vBtn_Prev.setOnClickListener((v) -> {
            mMediaController.seekToPreviousMediaItem();
        });
        this.m_vBtn_PlayPause.setOnClickListener((v) -> {
            if (mMediaController.isPlaying()) {
                mMediaController.pause();
            } else {
                mMediaController.play();
            }

        });
        this.m_vBtn_Next.setOnClickListener((v) -> {
            mMediaController.seekToNextMediaItem();
        });
        this.m_vBtn_Shuffle.setOnClickListener((v) -> {
            if (mMediaController.getShuffleModeEnabled()) {
                this.m_vBtn_Shuffle.setIconResource(R.drawable.ic_shuffle_off);
                mMediaController.setShuffleModeEnabled(false);
            } else {
                this.m_vBtn_Shuffle.setIconResource(leveldown.kyle.icon_packs.R.drawable.ic_shuffle_on_24px);
                mMediaController.setShuffleModeEnabled(true);
            }
        });
    }

    public void onUpdateMetadata(MediaMetadata mediaMetadata, Bitmap bitmap) {
        int totalDuration = (int) mMediaController.getDuration();
        this.m_vTextView_Title.setText(mediaMetadata.title);
        this.m_vTextView_Artist.setText(mediaMetadata.artist);
        this.m_vSeekBar_Main.setProgress(0);
        this.m_vSeekBar_Main.setMax((int) totalDuration);

        ImageView imgView = (ImageView) this.m_vCardView_Art.getChildAt(0);
        if (imgView != null) {
            Glide.with(this.getRootView().getContext())
                    .load(bitmap)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(leveldown.kyle.icon_packs.R.drawable.ic_album_24px)
                    .into(imgView);
        }

        this.m_vTextView_MaxDuration.setText(getTimeFormat(totalDuration));
    }

    public void onPlaybackStateChanged(boolean isPlaying) {
        if (mMediaController == null) {
            mMediaController = MediaItemHolder.getInstance().getMediaController();
        }

        if (m_vCanUpdateSeekbar) {
            this.m_vSeekBar_Main.setProgress((int) mMediaController.getCurrentPosition());
        }
        this.m_vBtn_PlayPause.setImageResource(!isPlaying ? leveldown.kyle.icon_packs.R.drawable.ic_play_arrow_24px : leveldown.kyle.icon_packs.R.drawable.ic_pause_24px);
        this.m_vTextView_CurrentDuration.setText(getTimeFormat(mMediaController.getCurrentPosition()));
    }

    public View getRootView() {
        return this.mRootView;
    }

    public void onSliding(float slideOffset, int state) {
        float fadeStart = 0.25f;
        float alpha = (slideOffset - fadeStart) * (1F / (1F - fadeStart));

        if (state == STATE_NORMAL) {
            this.mRootView.setAlpha(alpha);
            this.mControlsContainer.setAlpha(1F);
        } else {
            this.mControlsContainer.setAlpha(1F - alpha);
        }
        this.mState = state;
        //LogUtils.ApplicationLogI("MEDIA Slide: " + slideOffset + " State: " + state + " Alpha: " + alpha);
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return this.mRootView.findViewById(id);
    }

    public void onPanelStateChanged(int panelSate) {
        if (panelSate == MultiSlidingUpPanelLayout.COLLAPSED) {
            this.mRootView.setVisibility(View.INVISIBLE);
        } else
            this.mRootView.setVisibility(View.VISIBLE);
    }

    @SuppressLint("DefaultLocale")
    public String getTimeFormat(long ms) {
        long hours = TimeUnit.MILLISECONDS.toHours(ms);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(ms - TimeUnit.HOURS.toMillis(hours));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(ms - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes));

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public void onMediaControllerConnect(MediaController controller) {
        LogUtils.ApplicationLogD("onMediaControllerConnect MediaView Called");
        if (this.mMediaController != null) {
            return;
        }
        mMediaController = controller;
        setOnListener();
    }
}
