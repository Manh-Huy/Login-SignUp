package com.example.authenticationuseraccount.utils;

import com.example.authenticationuseraccount.model.SocketUser;
import com.example.authenticationuseraccount.model.business.Song;

import java.util.ArrayList;
import java.util.List;

public class ChillCornerRoomManager {
    private static ChillCornerRoomManager instance;

    private String roomId;
    private String currentUserId;
    private boolean isCreated;
    private int currentPlaySongIndex;
    private List<SocketUser> listUser;
    private List<Song> listSongs;

    private ChillCornerRoomManager() {
        listUser = new ArrayList<>();
        isCreated = false;
        currentPlaySongIndex = -1;
        currentUserId = null;
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

    public void setCreated(boolean isCreated) {
        this.isCreated = isCreated;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public boolean isCurrentUserHost() {
        return roomId != null && roomId.equals(currentUserId);
    }

    public List<SocketUser> getListUser() {
        return listUser;
    }

    public void setListUser(List<SocketUser> listUser) {
        this.listUser = listUser;
    }

    public List<Song> getListSongs() {
        return listSongs;
    }

    public void setListSongs(List<Song> listSongs) {
        this.listSongs = listSongs;
    }

    public int getCurrentPlaySongIndex() {
        return currentPlaySongIndex;
    }

    public void setCurrentPlaySongIndex(int currentPlaySongIndex) {
        this.currentPlaySongIndex = currentPlaySongIndex;
    }
}
