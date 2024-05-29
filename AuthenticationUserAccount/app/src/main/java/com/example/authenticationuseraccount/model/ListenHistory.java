package com.example.authenticationuseraccount.model;

public class ListenHistory {

    private String userID;
    private String songID;
    private int count;
    private boolean isLove;
    private String lastListen;

    public ListenHistory(String userID, String songID, int count, boolean isLove, String lastListen) {
        this.userID = userID;
        this.songID = songID;
        this.count = count;
        this.isLove = isLove;
        this.lastListen = lastListen;
    }

    public ListenHistory() {

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSongID() {
        return songID;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isLove() {
        return isLove;
    }

    public void setLove(boolean love) {
        isLove = love;
    }

    public String getLastListen() {
        return lastListen;
    }

    public void setLastListen(String lastListen) {
        this.lastListen = lastListen;
    }
}
