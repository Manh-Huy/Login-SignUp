package com.example.authenticationuseraccount.service;

import androidx.media3.common.MediaItem;

import com.example.authenticationuseraccount.model.business.Song;

import java.util.ArrayList;
import java.util.List;

public class MediaItemHolder {
    private static MediaItemHolder instance;
    private List<MediaItem> listMediaItem;
    private List<Song> listSongs;

    public static void setInstance(MediaItemHolder instance) {
        MediaItemHolder.instance = instance;
    }

    private boolean isShuffle;
    private boolean isRepeatSingle;
    private boolean isRepeatPlaylist;

    private MediaItemHolder() {
        listMediaItem = new ArrayList<>();
        listSongs = new ArrayList<>();
        isShuffle = false;
        isRepeatSingle = false;
        isRepeatPlaylist = false;
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


}
