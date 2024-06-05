package com.example.authenticationuseraccount.activity.panel.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.media3.common.C;
import androidx.media3.common.MediaMetadata;
import androidx.media3.session.MediaController;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.MainActivity;
import com.example.authenticationuseraccount.activity.MediaPlayerActivity;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.ListenHistory;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout;

public class MediaPlayerBarView {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_PARTIAL = 1;
    private final View mRootView;
    private Handler handler = new Handler();
    private int mState;
    private FrameLayout mBackgroundView;
    private LinearProgressIndicator mProgressIndicator;
    private ConstraintLayout mControlsContainer;
    private ImageView mImageView_Art;
    private TextView mTextView_SongTitle;
    private TextView mTextView_SongArtist;
    MediaController mMediaController;
    private ImageButton mImageBtn_Fav;
    private ImageButton mImageBtn_PlayPause;
    private ProgressBar mProgressBar;

    public MediaPlayerBarView(View rootView) {
        LogUtils.ApplicationLogE("MediaPlayerBarView Constructor");
        this.mRootView = rootView;
        this.mBackgroundView = findViewById(R.id.media_player_bar_bg);
        this.mControlsContainer = findViewById(R.id.media_player_bar_controls_container);
        this.mProgressIndicator = findViewById(R.id.media_player_bar_progress_indicator);
        this.mImageView_Art = this.mControlsContainer.findViewById(R.id.image_view_album_art);
        this.mTextView_SongTitle = this.mControlsContainer.findViewById(R.id.text_view_song_title);
        this.mTextView_SongArtist = this.mControlsContainer.findViewById(R.id.text_view_song_artist);
        this.mImageBtn_Fav = this.mControlsContainer.findViewById(R.id.btn_favorite);
        this.mImageBtn_PlayPause = this.mControlsContainer.findViewById(R.id.btn_play_pause);
        this.mRootView.setAlpha(1.0F);
        this.mProgressBar = findViewById(R.id.progress_bar);
        this.mProgressBar.setIndeterminateDrawable(new Wave());
        this.mImageView_Art.setVisibility(View.INVISIBLE);
        this.mProgressBar.setVisibility(View.VISIBLE);
    }

    public void onPanelStateChanged(int panelSate) {
        LogUtils.ApplicationLogE("MediaPlayerBarView onPanelStateChanged: " + panelSate);
        if (panelSate == MultiSlidingUpPanelLayout.COLLAPSED) {
            this.mRootView.setVisibility(View.VISIBLE);
        }

        if (panelSate == MultiSlidingUpPanelLayout.EXPANDED){
            this.mRootView.setAlpha(0F);
            this.mBackgroundView.setAlpha(0F);
            this.mProgressIndicator.setAlpha(0F);
        }

    }

    public void onUpdateMetadata(MediaMetadata mediaMetadata, Bitmap album_art) {
        //mImageBtn_Fav.setImageResource(R.drawable.baseline_favorite_24);
        mImageBtn_Fav.setImageResource(leveldown.kyle.icon_packs.R.drawable.favorite_24px);
        LogUtils.ApplicationLogE("MediaPlayerBarView onUpdateMetadata");
        this.mTextView_SongTitle.setText(mediaMetadata.title);
        this.mTextView_SongArtist.setText(mediaMetadata.artist);
        this.mTextView_SongArtist.setSelected(true);
        this.mTextView_SongTitle.setSelected(true);

        this.mProgressIndicator.setMax((int) MediaItemHolder.getInstance().getMediaController().getDuration());
        if (album_art != null) {
            this.mImageView_Art.setImageBitmap(album_art);
            this.mImageView_Art.setVisibility(View.VISIBLE);
            this.mProgressBar.setVisibility(View.INVISIBLE);
        } else{
            this.mProgressBar.setVisibility(View.VISIBLE);
            this.mImageView_Art.setVisibility(View.INVISIBLE);
            //this.mImageView_Art.setImageDrawable(ResourcesCompat.getDrawable(this.mRootView.getResources(), leveldown.kyle.icon_packs.R.drawable.ic_album_24px, this.mRootView.getContext().getTheme()));
        }
    }

    public void onPlaybackStateChanged(boolean isPlaying) {

        this.mImageBtn_PlayPause.setImageIcon(Icon.createWithResource(this.getContext(), isPlaying ? leveldown.kyle.icon_packs.R.drawable.ic_pause_24px : leveldown.kyle.icon_packs.R.drawable.ic_play_arrow_24px));
    }

    public void onMediaControllerCreate(MediaController mediaController) {
        LogUtils.ApplicationLogE("MediaPlayerBarView onMediaControllerCreate");
        if (mMediaController != null) {
            return;
        }
        mMediaController = mediaController;
        this.onInit();
    }

    public void onInit() {
        this.mImageBtn_PlayPause.setOnClickListener((v) -> {
            if (mMediaController.isPlaying()) {
                mMediaController.pause();
            } else {
                mMediaController.play();
            }
        });
    }

    public void onSetupSeekBar() {
        int totalDuration = 0;
        //Reset SeekBar
        if (MediaItemHolder.getInstance().getMediaController().getDuration() != C.TIME_UNSET) {
            totalDuration = (int) MediaItemHolder.getInstance().getMediaController().getDuration();
            this.mProgressIndicator.setMax(totalDuration / 1000);
        }
        //Update SeekBar Continuosly
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                if (mMediaController != null) {
                    int currentPosition = (int) (mMediaController.getCurrentPosition() / 1000);
                    mProgressIndicator.setProgress(currentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        });

    }

    public void onSliding(float slideOffset, int state) {
        float fadeStart = 0.25F;
        float alpha = (slideOffset / fadeStart);

        if (state == STATE_NORMAL) {
            this.mRootView.setAlpha(1F - alpha);
            this.mBackgroundView.setAlpha(1F);
            this.mProgressIndicator.setAlpha(1F);
            this.mControlsContainer.setAlpha(1F);
        } else {
            this.mRootView.setAlpha(alpha);
            this.mBackgroundView.setAlpha(0F);
            this.mProgressIndicator.setAlpha(0F);
            this.mControlsContainer.setAlpha(1F);
        }

        this.mState = state;
    }

    public Context getContext() {
        return this.mRootView.getContext();
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return this.mRootView.findViewById(id);
    }

    public void onUpdateVibrantColor(int vibrantColor) {
        this.mImageBtn_PlayPause.setBackgroundColor(vibrantColor);
    }

    public void onUpdateVibrantLightColor(int vibrantLightColor) {
        this.mProgressIndicator.setIndicatorColor(vibrantLightColor);
    }

    public void onUpdateVibrantDarkColor(int vibrantDarkColor) {
        this.mBackgroundView.setBackgroundColor(vibrantDarkColor);
    }

    public void onUpdateMutedColor(int mutedColor) {

    }

    public void onUpdateMutedDarkColor(int mutedDarkColor) {
        this.mProgressIndicator.setTrackColor(mutedDarkColor);
    }

}
