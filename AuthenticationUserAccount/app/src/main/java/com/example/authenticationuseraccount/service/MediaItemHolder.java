package com.example.authenticationuseraccount.service;

import androidx.annotation.IntDef;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.MediaSession;

import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.business.LocalSong;
import com.example.authenticationuseraccount.model.business.Song;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MediaItemHolder {
    public static final int REPEAT_TYPE_NONE = Player.REPEAT_MODE_OFF;
    public static final int REPEAT_TYPE_ONE = Player.REPEAT_MODE_ONE;
    public static final int REPEAT_TYPE_ALL = Player.REPEAT_MODE_ALL;

    private List<Song> listLoveSong;
    private List<Song> listRecentSong;

    private List<LocalSong> listLocalSong;

    public List<Song> getListLoveSong() {
        return listLoveSong;
    }

    public void setListLoveSong(List<Song> listLoveSong) {
        this.listLoveSong = listLoveSong;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({REPEAT_TYPE_NONE, REPEAT_TYPE_ONE, REPEAT_TYPE_ALL})
    public @interface RepeatType {
    }

    public MediaController getMediaController() {
        return mediaController;
    }

    public void setMediaController(MediaController mediaController) {
        MediaItemHolder.mediaController = mediaController;
    }

    private static MediaController mediaController;
    private static MediaItemHolder instance;
    private List<MediaItem> listMediaItem;
    private List<Song> listSongs;
    private boolean isSetupMetaData;
    private boolean isSaveUserHistoryTriggered;
    private boolean isShuffle;

    public static void setInstance(MediaItemHolder instance) {
        MediaItemHolder.instance = instance;
    }

    public boolean isSetupMetaData() {
        return isSetupMetaData;
    }

    public void setSetupMetaData(boolean setupMetaData) {
        isSetupMetaData = setupMetaData;
    }

    public boolean isSaveUserHistoryTriggered() {
        return isSaveUserHistoryTriggered;
    }

    public void setSaveUserHistoryTriggered(boolean saveUserHistoryTriggered) {
        isSaveUserHistoryTriggered = saveUserHistoryTriggered;
    }

    private boolean isRepeatSingle;
    private boolean isRepeatPlaylist;

    public List<LocalSong> getListLocalSong() {
        return listLocalSong;
    }

    public void setListLocalSong(List<LocalSong> listLocalSong) {
        this.listLocalSong = listLocalSong;
    }

    public List<Song> getListRecentSong() {
        return listRecentSong;
    }

    public void setListRecentSong(List<Song> listRecentSong) {
        this.listRecentSong = listRecentSong;
    }

    private MediaItemHolder() {
        listMediaItem = new ArrayList<>();
        listSongs = new ArrayList<>();
        listLoveSong = new ArrayList<>();
        listLocalSong = new ArrayList<>();
        listRecentSong = new ArrayList<>();
        isShuffle = false;
        isRepeatSingle = false;
        isRepeatPlaylist = false;
        isSetupMetaData = false;
        isSaveUserHistoryTriggered = false;
    }

    public static MediaItemHolder getInstance() {
        if (instance == null) {
            synchronized (MediaItemHolder.class) {
                if (instance == null) {
                    instance = new MediaItemHolder();
                }
            }
        }
        return instance;
    }

    public void destroy() {
        mediaController = null;
        instance = null;
    }

    public List<MediaItem> getListMediaItem() {
        return listMediaItem;
    }

    public void setListSongs(List<Song> listSongs) {
        this.listSongs = listSongs;
    }

    public List<Song> getListSongs() {
        return listSongs;
    }

    public void setListMediaItem(List<MediaItem> listMediaItem) {
        this.listMediaItem = listMediaItem;
    }

    public boolean isShuffle() {
        return isShuffle;
    }

    public void setShuffle(boolean shuffle) {
        isShuffle = shuffle;
    }

    public boolean isRepeatSingle() {
        return isRepeatSingle;
    }

    public void setRepeatSingle(boolean repeatSingle) {
        isRepeatSingle = repeatSingle;
    }

    public boolean isRepeatPlaylist() {
        return isRepeatPlaylist;
    }

    public void setRepeatPlaylist(boolean repeatPlaylist) {
        isRepeatPlaylist = repeatPlaylist;
    }

    public void setMediaItem(Song song) {
        this.listSongs.clear();
        this.listSongs.add(song);
        MediaItem mediaItem = MediaItem.fromUri(song.getSongURL());
        instance.getMediaController().setMediaItem(mediaItem);
    }

    public void addMediaItemToQueue(Song song) {
        instance.getListSongs().add(song);
        MediaItem mediaItem = MediaItem.fromUri(song.getSongURL());
        instance.getMediaController().addMediaItem(mediaItem);
    }

    public void playMediaItemNext(Song song) {
        if (instance.getListSongs().isEmpty()) {
            instance.getListSongs().add(song);
            MediaItem mediaItem = MediaItem.fromUri(song.getSongURL());
            instance.getMediaController().addMediaItem(mediaItem);
        } else {
            int currentSongIndex = instance.getMediaController().getCurrentMediaItemIndex();
            instance.getListSongs().add(currentSongIndex + 1, song);
            MediaItem mediaItem = MediaItem.fromUri(song.getSongURL());
            instance.getMediaController().addMediaItem(currentSongIndex + 1, mediaItem);
        }
    }

    public void addListMediaItem(List<Song> songs) {
        for (Song song : songs) {
            this.listSongs.add(song);
            MediaItem mediaItem = MediaItem.fromUri(song.getSongURL());
            instance.getMediaController().addMediaItem(mediaItem);
        }
    }

    public void setListAlbumMediaItem(List<Song> songs) {
        this.listSongs.clear();
        instance.getMediaController().clearMediaItems();
        int i = 0;
        for (Song song : songs) {
            this.listSongs.add(song);
            MediaItem mediaItem = MediaItem.fromUri(song.getSongURL());
            instance.getMediaController().addMediaItem(mediaItem);
        }

    }

    public void setListAlbumMediaItemRandom(List<Song> songs) {
        this.listSongs.clear();
        instance.getMediaController().clearMediaItems();
        for (Song song : songs) {
            this.listSongs.add(song);
            MediaItem mediaItem = MediaItem.fromUri(song.getSongURL());
            MediaItemHolder.getInstance().getMediaController().addMediaItem(mediaItem);
        }
        instance.getMediaController().setShuffleModeEnabled(true);
    }

}
