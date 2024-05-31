package com.example.authenticationuseraccount.service;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
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
    private final MainActivity m_vMainActivity;
    private MultiSlidingUpPanelLayout m_vMultiSlidingPanel;
    private boolean m_vCanUpdatePanelsUI;
    public List<OnPanelStateChanged> m_vOnPanelStateListeners;
    private Player.Listener mListener;

    public UIThread(MainActivity activity) {
        instance = this;
        this.m_vOnPanelStateListeners = new ArrayList<>();
        this.m_vMainActivity = activity;
        this.mListener = uiThreadCallBackListener;
        onCreate();

        //LibraryManager.initLibrary(activity.getApplicationContext());
    }

    public Player.Listener getListener(){
        return this.mListener;
    }

    public static UIThread getInstance() { return instance; }

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

    Player.Listener uiThreadCallBackListener = new Player.Listener() {

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            Player.Listener.super.onIsPlayingChanged(isPlaying);
            if (isPlaying) {
                UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onPlaybackStateChanged(true);
            } else {
                UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onPlaybackStateChanged(false);
            }
        };


    };

    @Override
    public void onMediaControllerConnect(MediaController controller) {
        LogUtils.ApplicationLogD("onMediaControllerConnect UI Thread Call");
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onMediaControllerReady(controller);
    }
}
