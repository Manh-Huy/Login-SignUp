package com.example.authenticationuseraccount.activity.panel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.session.PlaybackState;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.session.MediaController;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.panel.view.MediaPlayerBarView;
import com.example.authenticationuseraccount.activity.panel.view.MediaPlayerView;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.service.BackEventHandler;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.service.UIThread;
import com.example.authenticationuseraccount.theme.AsyncPaletteBuilder;
import com.example.authenticationuseraccount.theme.interfaces.PaletteStateListener;
import com.realgear.multislidinguppanel.BasePanelView;
import com.realgear.multislidinguppanel.IPanel;
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout;


public class RootMediaPlayerPanel extends BasePanelView {
    private MediaPlayerView mMediaPlayerView;
    private MediaPlayerBarView mMediaPlayerBarView;
    private View mParentView;
    public final Runnable m_vOnBackPressed = this::collapsePanel;

    public RootMediaPlayerPanel(@NonNull Context context, MultiSlidingUpPanelLayout panelLayout) {
        super(context, panelLayout);
        LogUtils.ApplicationLogE("RootMedia: Constructor");
        getContext().setTheme(R.style.Theme_AuthenticationUserAccount);
        mParentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_root_mediaplayer, this, true);
    }

    @Override
    public void onCreateView() {
        LogUtils.ApplicationLogE("RootMedia: onCreateView");
        this.setPanelState(MultiSlidingUpPanelLayout.HIDDEN);
        this.setSlideDirection(MultiSlidingUpPanelLayout.SLIDE_VERTICAL);
        this.setPeakHeight(getNavigationBarHeight() + 102);
    }

    @Override
    public void onBindView() {
        LogUtils.ApplicationLogE("RootMedia: OnBindView");
        mMediaPlayerView = new MediaPlayerView(findViewById(R.id.media_player_view));
        mMediaPlayerBarView = new MediaPlayerBarView(findViewById(R.id.media_player_bar_view));
    }

    @Override
    public void onPanelStateChanged(int panelSate) {
        LogUtils.ApplicationLogE("RootMedia: onPanelStateChanged: " + panelSate);
        UIThread.getInstance().onPanelStateChanged(this.getClass(), panelSate);
        if (panelSate == MultiSlidingUpPanelLayout.HIDDEN) {
            mParentView.setVisibility(INVISIBLE);
        } else {
            mParentView.setVisibility(VISIBLE);
        }

        if (this.mMediaPlayerView != null)
            this.mMediaPlayerView.onPanelStateChanged(panelSate);
        if (this.mMediaPlayerBarView != null)
            this.mMediaPlayerBarView.onPanelStateChanged(panelSate);

    }

    public void onMediaControllerReady(MediaController mediaController) {
        LogUtils.ApplicationLogE("RootMedia: onMediaControllerReady");
        this.mMediaPlayerView.onMediaControllerConnect(mediaController);
        this.mMediaPlayerBarView.onMediaControllerCreate(mediaController);
    }

    public void onUpdateUIOnRestar(MediaMetadata currentMetaData) {
        this.setPanelState(MultiSlidingUpPanelLayout.EXPANDED);
        byte[] art = currentMetaData.artworkData;
        Bitmap bitmap = null;
        if (art != null) {
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
        }
        onUpdateMetadata(currentMetaData, bitmap);
        onSetupSeekBar();
    }

    public void onUpdateUI(MediaMetadata currentMetaData) {
        LogUtils.ApplicationLogE("RootMedia: onUpdateMetadata");
        mParentView.setVisibility(VISIBLE);
        byte[] art = currentMetaData.artworkData;
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);

        this.mMediaPlayerBarView.onUpdateMetadata(currentMetaData, bitmap);
        this.mMediaPlayerView.onUpdateMetadata(currentMetaData, bitmap);

    }

    public void onUpdateMetadata(MediaMetadata mediaMetadata, Bitmap bitmap) {
        LogUtils.ApplicationLogE("RootMedia: onUpdateMetadata");
        mParentView.setVisibility(VISIBLE);

        this.mMediaPlayerBarView.onUpdateMetadata(mediaMetadata, bitmap);
        this.mMediaPlayerView.onUpdateMetadata(mediaMetadata, bitmap);

    }

    public void onSetupSeekBar() {
        this.mMediaPlayerBarView.onSetupSeekBar();
        this.mMediaPlayerView.onSetupSeekBar();
    }

    public void onPlaybackStateChanged(boolean isPlaying) {

        if (this.getPanelState() == MultiSlidingUpPanelLayout.HIDDEN)
            this.collapsePanel();
        //this.expandPanel();

        if (UIThread.getInstance().canUpdatePanelUI()) {
            this.mMediaPlayerView.onPlaybackStateChanged(isPlaying);
            this.mMediaPlayerBarView.onPlaybackStateChanged(isPlaying);
        }
    }

    @Override
    public void onSliding(@NonNull IPanel<View> panel, int top, int dy, float slidingOffset) {
        super.onSliding(panel, top, dy, slidingOffset);
        mMediaPlayerView.onSliding(slidingOffset, MediaPlayerView.STATE_NORMAL);
        mMediaPlayerBarView.onSliding(slidingOffset, MediaPlayerBarView.STATE_NORMAL);
    }

    public int getNavigationBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsets windowInsets = getRootWindowInsets();
            if (windowInsets != null) {
                return windowInsets.getInsets(WindowInsets.Type.navigationBars()).bottom;
            }
        } else {
            int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return getResources().getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }

    public void onUpdateVibrantColor(int vibrantColor) {
        this.mMediaPlayerBarView.onUpdateVibrantColor(vibrantColor);
        this.mMediaPlayerView.onUpdateVibrantColor(vibrantColor);
    }

    public void onUpdateVibrantDarkColor(int vibrantDarkColor) {
        this.mMediaPlayerBarView.onUpdateVibrantDarkColor(vibrantDarkColor);
        this.mMediaPlayerView.onUpdateVibrantDarkColor(vibrantDarkColor);
    }

    public void onUpdateVibrantLightColor(int vibrantLightColor) {
        this.mMediaPlayerBarView.onUpdateVibrantLightColor(vibrantLightColor);
        //this.mMediaPlayerView.onUpdateVibrantLightColor(vibrantLightColor);
    }

    public void onUpdateMutedColor(int mutedColor) {
        this.mMediaPlayerBarView.onUpdateMutedColor(mutedColor);
        //this.mMediaPlayerView.onUpdateMutedColor(mutedColor);
    }

    public void onUpdateMutedDarkColor(int mutedDarkColor) {
        this.mMediaPlayerBarView.onUpdateMutedDarkColor(mutedDarkColor);
        //this.mMediaPlayerView.onUpdateMutedDarkColor(mutedDarkColor);
    }

    public void giveUiThreadInstance(UIThread uiThread){
        this.mMediaPlayerView.onReceiveUiThread(uiThread);
    }
}
