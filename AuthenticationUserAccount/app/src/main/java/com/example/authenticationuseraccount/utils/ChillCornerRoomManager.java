package com.example.authenticationuseraccount.utils;

import com.example.authenticationuseraccount.model.business.Song;

import java.util.ArrayList;
import java.util.List;

public class ChillCornerRoomManager {
    private String roomId;
    private String currentUserId;
    private List<String> listUser;
    private boolean isCreated;
    private static ChillCornerRoomManager instance;
    private Song currentPlaySong;

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

    public Song getCurrentPlaySong() {
        return currentPlaySong;
    }

    public void setCurrentPlaySong(Song currentPlaySong) {
        this.currentPlaySong = currentPlaySong;
    }
}
