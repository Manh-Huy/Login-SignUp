package com.example.authenticationuseraccount.activity.panel.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.session.PlaybackState;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.media3.common.MediaMetadata;
import androidx.media3.session.MediaController;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.MainActivity;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout;

public class MediaPlayerBarView {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_PARTIAL = 1;

    private final View mRootView;

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

    public MediaPlayerBarView(View rootView) {
        this.mRootView = rootView;

        this.mBackgroundView = findViewById(R.id.media_player_bar_bg);
        this.mControlsContainer = findViewById(R.id.media_player_bar_controls_container);
        this.mProgressIndicator = findViewById(R.id.media_player_bar_progress_indicator);
        this.mImageView_Art = this.mControlsContainer.findViewById(R.id.image_view_album_art);
        View textContainer = this.mControlsContainer.findViewById(R.id.text_view_container);
        this.mTextView_SongTitle = this.mControlsContainer.findViewById(R.id.text_view_song_title);
        this.mTextView_SongArtist = this.mControlsContainer.findViewById(R.id.text_view_song_artist);
        this.mImageBtn_Fav = this.mControlsContainer.findViewById(R.id.btn_favorite);
        this.mImageBtn_PlayPause = this.mControlsContainer.findViewById(R.id.btn_play_pause);
        this.mRootView.setAlpha(1.0F);

    }

    public View getRootView() {
        return this.mRootView;
    }

    public void onInit() {
        if (MediaItemHolder.getInstance().getMediaController() != null) {
            mMediaController = MediaItemHolder.getInstance().getMediaController();
            LogUtils.ApplicationLogD("onInit: MediaBar");
            LogUtils.ApplicationLogD("IT'sssssssssssss Aliveeeeeeeeeeee");
        } else {
            LogUtils.ApplicationLogD("onInit: MediaBar");
            LogUtils.ApplicationLogD("Co Biennnnnnnnnnnnnnnnnnnnnnnnnnnn");
        }

        this.mImageBtn_PlayPause.setOnClickListener((v) -> {
            if (mMediaController.isPlaying()) {
                mMediaController.pause();
            } else {
                mMediaController.play();
            }
            LogUtils.ApplicationLogI("MediaPlayerBar" + " Play Pause Btn Clicked");
        });
    }

    public void onUpdateMetadata(MediaMetadata mediaMetadata, Bitmap album_art) {
        this.mTextView_SongTitle.setText(mediaMetadata.title);
        this.mTextView_SongArtist.setText(mediaMetadata.artist);
        this.mProgressIndicator.setMax((int) mMediaController.getDuration());

        Log.i("Media Player Bar View", "Is bitmap null ? " + (album_art == null));

        if (album_art != null) {
            this.mImageView_Art.setImageBitmap(album_art);
        } else
            this.mImageView_Art.setImageDrawable(ResourcesCompat.getDrawable(this.mRootView.getResources(), leveldown.kyle.icon_packs.R.drawable.ic_album_24px, this.mRootView.getContext().getTheme()));
    }

    public void onPlaybackStateChanged(boolean isPlaying) {
        if (mMediaController == null) {
            mMediaController = MediaItemHolder.getInstance().getMediaController();
        }
        this.mProgressIndicator.setProgress((int) mMediaController.getCurrentPosition());
        this.mImageBtn_PlayPause.setImageIcon(Icon.createWithResource(this.getContext(), isPlaying ? leveldown.kyle.icon_packs.R.drawable.ic_pause_24px : leveldown.kyle.icon_packs.R.drawable.ic_play_arrow_24px));
    }

    public void onMediaControllerCreate(MediaController mediaController) {
        LogUtils.ApplicationLogI("onMediaControllerCreate: BarView create");
        if (mMediaController != null) {
            return;
        }
        mMediaController = mediaController;
        this.onInit();
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

    public void onPanelStateChanged(int panelSate) {
        if (panelSate == MultiSlidingUpPanelLayout.COLLAPSED) {
            this.mRootView.setVisibility(View.VISIBLE);
        }
    }

    public void onUpdateVibrantColor(int vibrantColor) {

        //this.mImageBtn_PlayPause.setBackgroundColor(vibrantColor);
    }

    public void onUpdateVibrantDarkColor(int vibrantDarkColor) {
        this.mBackgroundView.setBackgroundColor(vibrantDarkColor);
    }

    public void onUpdateMutedColor(int mutedColor) {

    }

    public void onUpdateMutedDarkColor(int mutedDarkColor) {
        this.mProgressIndicator.setTrackColor(mutedDarkColor);
    }

    public void onMediaControllerConnect(MediaController controller) {
        LogUtils.ApplicationLogD("onMediaControllerConnect MediaViewBar Called");
        this.onMediaControllerCreate(controller);
    }
}
