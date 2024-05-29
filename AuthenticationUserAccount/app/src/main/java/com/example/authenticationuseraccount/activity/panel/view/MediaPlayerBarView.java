package com.example.authenticationuseraccount.activity.panel.view;

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.authenticationuseraccount.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class MediaPlayerBarView {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_PARTIAL = 1;
    private final View mRootView;
    private int mState;

    private FrameLayout mBackgroundView;
    private LinearProgressIndicator mProgressIndicator;
    private ConstraintLayout mControlsContainer;

    public MediaPlayerBarView(View RootView) {
        this.mRootView = RootView;

        this.mBackgroundView = findViewById(R.id.media_player_bar_bg);
        this.mControlsContainer = findViewById(R.id.media_player_bar_controls_container);
        this.mProgressIndicator = findViewById(R.id.media_player_bar_progress_indicator);

        this.mRootView.setAlpha(1.0f);
    }

    public void onSliding(float slideOffset, int state){
        float fadeStart = 0.25f;
        float alpha = (slideOffset / fadeStart);

        if(state == STATE_NORMAL){
            this.mRootView.setAlpha(1F - alpha);
            this.mBackgroundView.setAlpha(1F);
            this.mProgressIndicator.setAlpha(1F);
            this.mControlsContainer.setAlpha(1F);
        }else {
            this.mRootView.setAlpha(alpha);
            this.mBackgroundView.setAlpha(0F);
            this.mProgressIndicator.setAlpha(0F);
            this.mControlsContainer.setAlpha(1F);
        }
        this.mState = state;
    }

    public <T extends View> T  findViewById(@IdRes int id){
        return this.mRootView.findViewById(id);
    }
}
