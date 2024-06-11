package com.example.authenticationuseraccount.model.business;


import android.net.Uri;

public class LocalSong {
    private long id;
    private String title;
    private long duration;
    private String data;
    private String albumName;
    private String artistName;
    private String displayName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public LocalSong(long id, String title, long duration, String data, String albumName, String artistName, String displayName) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.data = data;
        this.albumName = albumName;
        this.artistName = artistName;
        this.displayName = displayName;
    }
}
