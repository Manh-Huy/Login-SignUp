package com.example.authenticationuseraccount.activity.panel;

import static com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout.EXPANDED;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;

import androidx.annotation.NonNull;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.panel.view.MediaPlayerBarView;
import com.example.authenticationuseraccount.activity.panel.view.MediaPlayerView;
import com.realgear.multislidinguppanel.BasePanelView;
import com.realgear.multislidinguppanel.IPanel;
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout;


public class RootMediaPlayerPanel extends BasePanelView {
    private MediaPlayerView mMediaPlayerView;
    private MediaPlayerBarView mMediaPlayerBarView;

    public RootMediaPlayerPanel(@NonNull Context context, MultiSlidingUpPanelLayout panelLayout) {
        super(context, panelLayout);
        getContext().setTheme(R.style.Theme_AuthenticationUserAccount);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_root_mediaplayer, this, true);
    }

    @Override
    public void onCreateView() {
        this.setPanelState(MultiSlidingUpPanelLayout.COLLAPSED);
        this.setPanelState(EXPANDED);
        this.setSlideDirection(MultiSlidingUpPanelLayout.SLIDE_VERTICAL);
        this.setPeakHeight(getNavigationBarHeight() + 46);
    }

    @Override
    public void onBindView() {
        mMediaPlayerView = new MediaPlayerView(findViewById(R.id.media_player_view));
        mMediaPlayerBarView = new MediaPlayerBarView(findViewById(R.id.media_player_bar_view));
    }

    @Override
    public void onPanelStateChanged(int panelSate) {

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
}
