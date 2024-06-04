package com.example.authenticationuseraccount.service;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Metadata;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.MainActivity;
import com.example.authenticationuseraccount.activity.panel.RootMediaPlayerPanel;
import com.example.authenticationuseraccount.activity.panel.RootNavigationBarPanel;
import com.example.authenticationuseraccount.common.LogUtils;
import com.realgear.multislidinguppanel.MultiSlidingPanelAdapter;
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout;


import java.util.ArrayList;
import java.util.List;

public class UIThread implements MainActivity.OnMediaControllerConnect {
    private static UIThread instance;
    private MainActivity m_vMainActivity;
    private MultiSlidingUpPanelLayout m_vMultiSlidingPanel;
    private boolean m_vCanUpdatePanelsUI;
    public List<OnPanelStateChanged> m_vOnPanelStateListeners;
    private Player.Listener mListener;

    public UIThread(MainActivity activity) {
        LogUtils.ApplicationLogI("UIThread onCreate");
        instance = this;
        this.m_vOnPanelStateListeners = new ArrayList<>();
        this.m_vMainActivity = activity;
        onCreate();

        //LibraryManager.initLibrary(activity.getApplicationContext());
    }

    public Player.Listener getListener() {
        return this.mListener;
    }

    public static UIThread getInstance() {
        return instance;
    }

    public void onCreate() {
        this.m_vMultiSlidingPanel = findViewById(R.id.root_sliding_up_panel);
        List<Class<?>> items = new ArrayList<>();
        items.add(RootMediaPlayerPanel.class);
        items.add(RootNavigationBarPanel.class);
        this.m_vMultiSlidingPanel.setAdapter(new MultiSlidingPanelAdapter(this.m_vMainActivity, items));
    }

    public void addOnPanelStateChangedListener(OnPanelStateChanged listener) {
        if (this.m_vOnPanelStateListeners.contains(listener))
            return;
        this.m_vOnPanelStateListeners.add(listener);
    }

    public void removeOnPanelStateChangedListener(OnPanelStateChanged listener) {
        if (this.m_vOnPanelStateListeners.contains(listener))
            this.m_vOnPanelStateListeners.remove(listener);
    }

    public void onPanelStateChanged(Class<?> panel, int state) {
        LogUtils.ApplicationLogI("UIThread onPanelStateChanged");
        this.m_vCanUpdatePanelsUI = state != MultiSlidingUpPanelLayout.DRAGGING;

        for (OnPanelStateChanged listener : this.m_vOnPanelStateListeners) {
            listener.onStateChanged(panel, state);
        }
    }

    public boolean canUpdatePanelUI() {
        return this.m_vCanUpdatePanelsUI;
    }

    public <T extends android.view.View> T findViewById(@IdRes int id) {
        return this.m_vMainActivity.findViewById(id);
    }

    @Override
    public void onMediaControllerConnect(MediaController controller) {
        this.mListener = new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                if (isPlaying) {
                    UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onPlaybackStateChanged(true);
                } else {
                    UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onPlaybackStateChanged(false);
                }
            }

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                    LogUtils.ApplicationLogD("MEDIA_ITEM_TRANSITION_REASON_AUTO");
                    MediaItemHolder.getInstance().setSetupMetaData(false);
                    MediaItemHolder.getInstance().setSaveUserHistoryTriggered(false);
                }
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT) {
                    LogUtils.ApplicationLogD("MEDIA_ITEM_TRANSITION_REASON_REPEAT");
                    MediaItemHolder.getInstance().setSetupMetaData(false);
                    MediaItemHolder.getInstance().setSaveUserHistoryTriggered(false);
                }
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED) {
                    LogUtils.ApplicationLogD("MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED");
                    MediaItemHolder.getInstance().getMediaController().prepare();
                    MediaItemHolder.getInstance().setSetupMetaData(false);
                    MediaItemHolder.getInstance().setSaveUserHistoryTriggered(false);
                }
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) {
                    LogUtils.ApplicationLogD("MEDIA_ITEM_TRANSITION_REASON_SEEK");
                    MediaItemHolder.getInstance().setSetupMetaData(false);
                    MediaItemHolder.getInstance().setSaveUserHistoryTriggered(false);
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
                    MediaItemHolder.getInstance().getMediaController().play();
                    LogUtils.ApplicationLogD("Player is play " + MediaItemHolder.getInstance().getMediaController().getMediaMetadata().title + " at position: " + MediaItemHolder.getInstance().getMediaController().getCurrentMediaItemIndex());
                }
                if (playbackState == Player.STATE_ENDED) {
                    LogUtils.ApplicationLogD("Player is Ended");
                }
            }

            @Override
            public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
                Player.Listener.super.onMediaMetadataChanged(mediaMetadata);
                LogUtils.ApplicationLogD("MetaDataChanged");
                UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onUpdateMetadata(mediaMetadata);
                UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onSetupSeekBar();
            }
        };
        MediaItemHolder.getInstance().getMediaController().addListener(this.mListener);
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onMediaControllerReady(controller);
    }

    @Override
    public void onUpdateUIOnRestar(MediaItem currentMediaItem) {
        LogUtils.ApplicationLogI("UIThread onUpdateUIOnRestar");
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onUpdateUIOnRestar(currentMediaItem);
    }

    public void release() {
        this.mListener = null;
        this.m_vMultiSlidingPanel = null;
        this.m_vMainActivity = null;
        this.m_vOnPanelStateListeners = null;
    }

}
