package com.example.authenticationuseraccount.utils;

import com.example.authenticationuseraccount.model.business.Song;

import java.util.ArrayList;
import java.util.List;

public class ChillCornerRoomManager {
    private String roomId;

    public List<Song> getListSongs() {
        return listSongs;
    }

    public void setListSongs(List<Song> listSongs) {
        this.listSongs = listSongs;
    }

    private String currentUserId;
    private List<String> listUser;
    private boolean isCreated;
    private static ChillCornerRoomManager instance;
    private int currentPlaySongIndex;

    private List<Song> listSongs;

    private ChillCornerRoomManager() {
        listUser = new ArrayList<>();
        isCreated = false;
    }

    public static synchronized ChillCornerRoomManager getInstance() {
        if (instance == null) {
            instance = new ChillCornerRoomManager();
        }
        return instance;
    }

    public static synchronized void release() {
        instance = null;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public boolean isCreated() {
        return isCreated;
    }

    public void setCreated(boolean created) {
        isCreated = created;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public boolean isCurrentUserHost() {
        if (roomId == null) {
            return false;
        }
        return roomId.equals(currentUserId);
    }

    public List<String> getListUser() {
        return listUser;
    }

    public void setListUser(List<String> listUser) {
        this.listUser = listUser;
    }

    public int getCurrentPlaySongIndex() {
        return currentPlaySongIndex;
    }

    public void setCurrentPlaySongIndex(int currentPlaySong) {
        this.currentPlaySongIndex = currentPlaySong;
    }
}
