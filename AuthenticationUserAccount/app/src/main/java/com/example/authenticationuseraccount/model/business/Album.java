package com.example.authenticationuseraccount.model.business;

import java.util.List;

public class Album {
    private String albumID;
    private String artist;
    private String imageURL;
    private List<Song> listSong;
    private String name;

    public Album(String albumID, String artist, String imageURL, List<Song> listSong, String name) {
        this.albumID = albumID;
        this.artist = artist;
        this.imageURL = imageURL;
        this.listSong = listSong;
        this.name = name;
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public List<Song> getListSong() {
        return listSong;
    }

    public void setListSong(List<Song> listSong) {
        this.listSong = listSong;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
