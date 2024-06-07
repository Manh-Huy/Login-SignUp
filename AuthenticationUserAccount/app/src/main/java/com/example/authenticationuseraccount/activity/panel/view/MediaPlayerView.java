package com.example.authenticationuseraccount.activity.panel.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.session.MediaController;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.fragment.FragmentQueueBottomSheet;
import com.example.authenticationuseraccount.model.ListenHistory;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.service.UIThread;
import com.example.authenticationuseraccount.utils.DataLocalManager;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MediaPlayerView {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_PARTIAL = 1;
    private final View mRootView;
    private int mState;
    private ProgressBar mProgressBar;
    private Handler handler = new Handler();
    private ConstraintLayout mControlsContainer;
    private ImageView mImageViewThumbNail;
    private ImageView mImageViewQueue;
    private CardView m_vCardView_Art;
    private SeekBar m_vSeekBar_Main;
    private TextView m_vTextView_CurrentDuration, m_vTextView_MaxDuration, m_vTextView_Artist, m_vTextView_Title;
    private ExtendedFloatingActionButton m_vBtn_Repeat, m_vBtn_Prev, m_vBtn_Next, m_vBtn_Shuffle;
    private MaterialCheckBox materialCheckBox;
    private FloatingActionButton m_vBtn_PlayPause;
    @MediaItemHolder.RepeatType
    public int m_vRepeatType = MediaItemHolder.REPEAT_TYPE_NONE;
    private boolean m_vCanUpdateSeekbar = true;
    private MediaController mMediaController;

    public MediaPlayerView(View rootView) {
        LogUtils.ApplicationLogE("MediaPlayerView Constructor");
        this.mRootView = rootView;
        this.mControlsContainer = findViewById(R.id.media_player_controls_container);
        this.mRootView.setAlpha(0.0F);
        this.mProgressBar = findViewById(R.id.progress_bar);
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
        this.materialCheckBox = findViewById(R.id.btn_favorite);
        this.mImageViewThumbNail = findViewById(R.id.img_thumb_song);
        this.mProgressBar.setIndeterminateDrawable(new Wave());
        this.mImageViewQueue = findViewById(R.id.img_queue);
        this.mProgressBar.setVisibility(View.VISIBLE);
        this.mImageViewThumbNail.setVisibility(View.INVISIBLE);
    }

    public void onMediaControllerConnect(MediaController controller) {
        LogUtils.ApplicationLogE("MediaPlayerView onMediaControllerConnect");
        if (this.mMediaController != null) {
            return;
        }
        mMediaController = controller;
        setOnListener();
    }

    public void onPanelStateChanged(int panelSate) {
        //LogUtils.ApplicationLogE("MediaPlayerView onPanelStateChanged: " + panelSate);
        mState = panelSate;
        if (panelSate == MultiSlidingUpPanelLayout.COLLAPSED) {
            this.mRootView.setVisibility(View.INVISIBLE);
        } else
            this.mRootView.setVisibility(View.VISIBLE);

        if (panelSate == MultiSlidingUpPanelLayout.EXPANDED) {
            this.mRootView.setAlpha(1F);
            this.mControlsContainer.setAlpha(1F);
        }
    }

    private void setOnListener() {

        this.m_vSeekBar_Main.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int final_value;
            boolean isUser;

            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                this.final_value = value;
                this.isUser = fromUser;
                int seekPosition = value * 1000;
                if (fromUser) {
                    mMediaController.seekTo(seekPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mMediaController != null)
                    mMediaController.pause();
                m_vCanUpdateSeekbar = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isUser) {
                    //mMediaController.seekTo(final_value);
                    if (mMediaController != null)
                        mMediaController.play();
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

        this.mImageViewQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiThread.openQueue();
            }
        });
        this.materialCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    materialCheckBox.setChecked(false);
                    ErrorUtils.showError(mRootView.getContext(), "Please Login to Like Song");
                } else {
                    if (materialCheckBox.isChecked()) {
                        ListenHistory listenHistory = getSongHistory(user.getUid(), true);
                        LogUtils.ApplicationLogI("Trigger Call Update History When Click Love!");
                        triggerAPICall(listenHistory);
                        MediaItemHolder.getInstance().setSaveUserHistoryTriggered(true);
                    } else {
                        Toast.makeText(mRootView.getContext(), "You have unliked the song", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void onUpdateUI() {

    }

    private UIThread uiThread;

    public void onReceiveUiThread(UIThread uiThread) {
        this.uiThread = uiThread;
    }

    public void onUpdateMetadata(MediaMetadata mediaMetadata, Bitmap bitmap) {
        this.materialCheckBox.setChecked(false);
        this.m_vTextView_Title.setText(mediaMetadata.title);
        this.m_vTextView_Artist.setText(mediaMetadata.artist);
        this.mProgressBar.setVisibility(View.VISIBLE);
        m_vTextView_Artist.setSelected(true);
        m_vTextView_Title.setSelected(true);
        ImageView imgView = (ImageView) this.m_vCardView_Art.getChildAt(0);
        Glide.get(this.getRootView().getContext()).clearMemory();
        Glide.with(this.getRootView().getContext())
                .load(bitmap)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        LogUtils.ApplicationLogE("onLoadFailed");
                        imgView.setImageResource(leveldown.kyle.icon_packs.R.drawable.ic_album_24px);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        LogUtils.ApplicationLogE("onResourceReady");
                        mProgressBar.setVisibility(View.GONE);
                        mImageViewThumbNail.setVisibility(View.VISIBLE);
                        return false;
                    }
                }).into(imgView);
    }

    public void onSetupSeekBar() {
        int totalDuration = 0;
        //Reset SeekBar
        if (MediaItemHolder.getInstance().getMediaController().getDuration() != C.TIME_UNSET) {
            totalDuration = (int) mMediaController.getDuration();
            m_vSeekBar_Main.setMax(totalDuration / 1000);
            m_vTextView_MaxDuration.setText(getTimeFormat(totalDuration));
            m_vTextView_CurrentDuration.setText("00:00");
        }
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                int currentPosition = (int) (mMediaController.getCurrentPosition() / 1000);
                int totalDuration = (int) (mMediaController.getDuration() / 1000);
                m_vSeekBar_Main.setProgress(currentPosition);
                m_vTextView_CurrentDuration.setText(getTimeFormat(mMediaController.getCurrentPosition()));

                // Update user History
                boolean isSaveUserHistoryTriggered = MediaItemHolder.getInstance().isSaveUserHistoryTriggered();
                if (currentPosition > totalDuration / 2 && !isSaveUserHistoryTriggered) {
                    updateUserHistory();
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

    private void updateUserHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            ListenHistory listenHistory = getSongHistory(user.getUid(), false);
            LogUtils.ApplicationLogI("Trigger Call Update History!");
            triggerAPICall(listenHistory);
            MediaItemHolder.getInstance().setSaveUserHistoryTriggered(true);
        } else {
            //triggerSaveLocal();
            MediaItemHolder.getInstance().setSaveUserHistoryTriggered(true);
        }
    }

    private void triggerSaveLocal() {
        LogUtils.ApplicationLogI("Trigger Local Call Update History!");
        ListenHistory listenHistory = getSongHistory("Local", false);
        DataLocalManager.setListenHistory(listenHistory);
    }

    public void onPlaybackStateChanged(boolean isPlaying) {
        if (mMediaController == null) {
            mMediaController = MediaItemHolder.getInstance().getMediaController();
        }
        this.m_vBtn_PlayPause.setImageResource(!isPlaying ? leveldown.kyle.icon_packs.R.drawable.ic_play_arrow_24px : leveldown.kyle.icon_packs.R.drawable.ic_pause_24px);
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

    private ListenHistory getSongHistory(String uid, boolean isClickLove) {

        String currentSongName = (String) mMediaController.getMediaMetadata().title;
        String currentSongArtist = (String) mMediaController.getMediaMetadata().artist;
        String songID = "-1";
        String songName = "-1";

        for (Song song : MediaItemHolder.getInstance().getListSongs()) {
            LogUtils.ApplicationLogI("song in list: " + song.getName() + " song in media: " + currentSongName);
            LogUtils.ApplicationLogI("artist in list: " + song.getArtist() + " artist in media: " + currentSongArtist);
            if (song.getName().equals(currentSongName) && song.getArtist().equals(currentSongArtist)) {
                songID = song.getSongID();
                songName = song.getName();
            }
        }

        LogUtils.ApplicationLogD("Song about to saved: " + songName);

        DateTimeFormatter formatter = null;
        String formattedDate = "";
        LocalDate currentDate = LocalDate.now();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        formattedDate = currentDate.format(formatter);

        if (isClickLove) {
            return new ListenHistory(uid, songID, 3, materialCheckBox.isChecked(), formattedDate);
        }
        else {
            return new ListenHistory(uid, songID, 1, materialCheckBox.isChecked(), formattedDate);
        }
    }

    public void onUpdateVibrantColor(int vibrantColor) {
        //this.mImageBtn_PlayPause.setBackgroundColor(vibrantColor);
    }

    public void onUpdateVibrantDarkColor(int vibrantDarkColor) {
        this.mControlsContainer.setBackgroundColor(vibrantDarkColor);
    }
}
