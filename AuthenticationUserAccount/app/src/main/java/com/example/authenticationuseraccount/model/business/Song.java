package com.example.authenticationuseraccount.model.business;

import java.io.Serializable;

public class Song implements Serializable {
    private String createdAt;

    private String artist;

    private String album;

    private String imageURL;

    private String name;

    private String genre;

    private String songID;

    private String views;

    private String songURL;

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Song(String artist, String album, String imageURL, String name, String genre, String songID, String views, String songURL) {
        this.artist = artist;
        this.album = album;
        this.imageURL = imageURL;
        this.name = name;
        this.genre = genre;
        this.songID = songID;
        this.views = views;
        this.songURL = songURL;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return this.genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSongID() {
        return this.songID;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }

    public String getViews() {
        return this.views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getSongURL() {
        return this.songURL;
    }

    public void setSongURL(String songURL) {
        this.songURL = songURL;
    }
}
