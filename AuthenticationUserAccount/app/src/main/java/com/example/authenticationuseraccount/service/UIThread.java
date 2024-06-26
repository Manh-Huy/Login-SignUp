package com.example.authenticationuseraccount.service;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;
import androidx.media3.session.MediaController;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.MainActivity;
import com.example.authenticationuseraccount.activity.panel.RootMediaPlayerPanel;
import com.example.authenticationuseraccount.activity.panel.RootNavigationBarPanel;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.fragment.FragmentQueueBottomSheet;
import com.example.authenticationuseraccount.model.ListenHistory;
import com.example.authenticationuseraccount.model.Message;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.theme.AsyncPaletteBuilder;
import com.example.authenticationuseraccount.theme.interfaces.PaletteStateListener;
import com.google.firebase.auth.FirebaseAuth;
import com.realgear.multislidinguppanel.MultiSlidingPanelAdapter;
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout;


import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UIThread implements MainActivity.OnMediaControllerConnect, PaletteStateListener {
    private static UIThread instance;
    private MainActivity m_vMainActivity;

    public MainActivity getM_vMainActivity() {
        return m_vMainActivity;
    }

    private MultiSlidingUpPanelLayout m_vMultiSlidingPanel;
    private boolean m_vCanUpdatePanelsUI;
    public List<OnPanelStateChanged> m_vOnPanelStateListeners;
    private Player.Listener mListener;
    private FragmentQueueBottomSheet mFragmentQueueBottomSheet;
    private AsyncPaletteBuilder mAsyncPaletteBuilder;
    private MultiSlidingPanelAdapter mMultiSlidingPanelAdapter;

    public UIThread(MainActivity activity) {
        LogUtils.ApplicationLogI("UIThread onCreate");
        instance = this;
        this.m_vOnPanelStateListeners = new ArrayList<>();
        this.m_vMainActivity = activity;
        this.mAsyncPaletteBuilder = new AsyncPaletteBuilder(this);
        this.mFragmentQueueBottomSheet = new FragmentQueueBottomSheet();
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

    public void onPanelStateChanged(Class<?> panel, int state) {
        //LogUtils.ApplicationLogI("UIThread onPanelStateChanged");
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
            public void onPlaylistMetadataChanged(MediaMetadata mediaMetadata) {
                Player.Listener.super.onPlaylistMetadataChanged(mediaMetadata);
            }

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
                    updateRecent();
                }
                if (playbackState == Player.STATE_ENDED) {
                    LogUtils.ApplicationLogD("Player is Ended");
                }
            }

            @Override
            public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
                Player.Listener.super.onMediaMetadataChanged(mediaMetadata);
                LogUtils.ApplicationLogD("MetaDataChanged");

                byte[] art = mediaMetadata.artworkData;
                Bitmap bitmap = null;
                if (art != null) {
                    bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                }
                UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onUpdateMetadata(mediaMetadata, bitmap);
                UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onSetupSeekBar();
                mAsyncPaletteBuilder.onStartAnimation(bitmap);
            }

            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                Player.Listener.super.onTimelineChanged(timeline, reason);
                if (reason == Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
                    //LogUtils.ApplicationLogD("TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED");
                    LogUtils.ApplicationLogD("Song in Playlist: " + MediaItemHolder.getInstance().getMediaController().getMediaItemCount());
                }
            }
        };
        MediaItemHolder.getInstance().getMediaController().addListener(this.mListener);
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onMediaControllerReady(controller);
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).giveUiThreadInstance(this);
    }

    private void updateRecent() {
        int currentSongIndex = MediaItemHolder.getInstance().getMediaController().getCurrentMediaItemIndex();
        Song song = MediaItemHolder.getInstance().getListSongs().get(currentSongIndex);
        if (song.getImageURL() != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            ListenHistory listenHistory = getSongHistory(FirebaseAuth.getInstance().getCurrentUser().getUid(), 0, song);
            triggerAPICall(listenHistory);
            LogUtils.ApplicationLogD("Player is play " + MediaItemHolder.getInstance().getMediaController().getMediaMetadata().title + " at position: " + MediaItemHolder.getInstance().getMediaController().getCurrentMediaItemIndex());
        }else{
            LogUtils.ApplicationLogI("LocalSong!!!");
        }

    }

    private ListenHistory getSongHistory(String uid, int count, Song songFromItem) {
        String songID = songFromItem.getSongID();
        String songName = songFromItem.getName();
        LogUtils.ApplicationLogD("Song about to add to recent: " + songName);
        DateTimeFormatter formatter = null;
        String formattedDate = "yyyy-MM-dd";
        return new ListenHistory(uid, songID, count, false, formattedDate);
    }

    @SuppressLint("CheckResult")
    private void triggerAPICall(ListenHistory listenHistory) {

        ApiService.apiService.addUserListenHistory(listenHistory)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    LogUtils.ApplicationLogD("Update User Recent! " + listenHistory.getSongID());
                    getUserListenHistory(FirebaseAuth.getInstance().getCurrentUser().getUid());
                }, throwable -> {
                    LogUtils.ApplicationLogE("Upload User Recent Failed!");
                });
    }

    private void getUserListenHistory(String userID) {
        ApiService.apiService.getUserListenHistory(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Song> songs) {
                        LogUtils.ApplicationLogI("FragmentHome | getUserListenHistory | api size: " + songs.size());
                        MediaItemHolder.getInstance().setListRecentSong(songs);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        LogUtils.ApplicationLogE("Call api user history error");
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.ApplicationLogE("Call api listen history complete");
                        LogUtils.ApplicationLogI("FragmentHome | getUserListenHistory | onComplete | api size: " + MediaItemHolder.getInstance().getListRecentSong().size());
                        UIThread.getInstance().onUpdateHistory(MediaItemHolder.getInstance().getListRecentSong().size());
                    }
                });
    }


    @Override
    public void onUpdateUIOnRestar(MediaController mediaController) {
        LogUtils.ApplicationLogI("UIThread onUpdateUIOnRestar");
        onMediaControllerConnect(mediaController);

        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onUpdateUIOnRestar(MediaItemHolder.getInstance().getMediaController().getMediaMetadata());
        byte[] art = MediaItemHolder.getInstance().getMediaController().getMediaMetadata().artworkData;
        Bitmap bitmap = null;
        if (art != null) {
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
        }
        mAsyncPaletteBuilder.onStartAnimation(bitmap);


    }

    public void release() {
        if (mListener != null && MediaItemHolder.getInstance().getMediaController() != null) {
            MediaItemHolder.getInstance().getMediaController().removeListener(mListener);
        }

        this.mListener = null;
        this.mMultiSlidingPanelAdapter = null;

        if (this.m_vMultiSlidingPanel != null) {
            this.m_vMultiSlidingPanel.removeAllViewsInLayout();
            this.m_vMultiSlidingPanel.removeAllViews();
            this.m_vMultiSlidingPanel = null;
        }

        this.m_vMainActivity = null;
        this.m_vOnPanelStateListeners = null;
    }

    @Override
    public void onUpdateVibrantColor(int vibrantColor) {
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onUpdateVibrantColor(vibrantColor);
    }

    @Override
    public void onUpdateVibrantDarkColor(int vibrantDarkColor) {
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onUpdateVibrantDarkColor(vibrantDarkColor);
        m_vMainActivity.getWindow().setStatusBarColor(vibrantDarkColor);
        m_vMainActivity.getWindow().setNavigationBarColor(vibrantDarkColor);
    }

    @Override
    public void onUpdateVibrantLightColor(int vibrantLightColor) {
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onUpdateVibrantLightColor(vibrantLightColor);
    }

    @Override
    public void onUpdateMutedColor(int mutedColor) {
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onUpdateMutedColor(mutedColor);
    }

    @Override
    public void onUpdateMutedDarkColor(int mutedDarkColor) {
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onUpdateMutedDarkColor(mutedDarkColor);
    }

    public void openQueue() {
        UIThread.this.mFragmentQueueBottomSheet.setCurrentMediaItem(MediaItemHolder.getInstance().getMediaController().getMediaMetadata());
        UIThread.this.mFragmentQueueBottomSheet.setListItems(MediaItemHolder.getInstance().getListSongs());
        UIThread.this.mFragmentQueueBottomSheet.show(m_vMainActivity.getSupportFragmentManager(), mFragmentQueueBottomSheet.getTag());
    }

    public void updateQueue() {
        UIThread.this.mFragmentQueueBottomSheet.setCurrentMediaItem(MediaItemHolder.getInstance().getMediaController().getMediaMetadata());
        UIThread.this.mFragmentQueueBottomSheet.setListItems(MediaItemHolder.getInstance().getListSongs());
        if (UIThread.this.mFragmentQueueBottomSheet.isAdded()) {
            UIThread.this.mFragmentQueueBottomSheet.notifyChanges();
        } else {
            UIThread.this.mFragmentQueueBottomSheet.show(m_vMainActivity.getSupportFragmentManager(), mFragmentQueueBottomSheet.getTag());
        }
    }


    public void onUpdateLoveSong(boolean isLove) {
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onUpdateLoveSong(isLove);
    }

    public void onUpdateLoveSongFromBarView(boolean isLove) {
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onUpdateLoveSongFromBarView(isLove);
    }

    public void onRoomCreate(String roomID) {
        LogUtils.ApplicationLogD("UIThread | onRoomCreate: " + roomID);
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onRoomCreate();
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootNavigationBarPanel.class).onRoomCreate(roomID);
    }

    public void onRoomJoined(String roomId) {
        LogUtils.ApplicationLogD("UIThread | onRoomJoined");
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onRoomCreate();
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootNavigationBarPanel.class).onRoomJoined(roomId);
    }

    public void onMessageReceived(Message message) {
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootNavigationBarPanel.class).onMessageReceived(message);
    }

    public void onOutRoom() {
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onOutRoom();
    }

    public void onUpdateHistory(int size) {
        LogUtils.ApplicationLogI("UIThread | onUpdateHistory");
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootNavigationBarPanel.class).onUpdateHistory(size);
    }

    public void onUpdateProfileImage() {
        LogUtils.ApplicationLogI("UIThread | onUpdateProfileImage");
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootNavigationBarPanel.class).onUpdateProfileImage();
    }

    public void onUpdateLoveSongSize(int size) {
        LogUtils.ApplicationLogI("UIThread | onUpdateLoveSongSize");
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootNavigationBarPanel.class).onUpdateLoveSongSize(size);
    }

    public void onRepeateModeSet(int repeatMode) {
        UIThread.this.m_vMultiSlidingPanel.getAdapter().getItem(RootMediaPlayerPanel.class).onRepeateModeSet(repeatMode);
    }
}
