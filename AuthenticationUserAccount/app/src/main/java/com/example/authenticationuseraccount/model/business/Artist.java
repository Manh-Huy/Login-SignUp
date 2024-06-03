package com.example.authenticationuseraccount.model.business;

import java.util.List;

public class Artist {
    private String artistID;
    private String description;
    private String imageURL;
    private List<Album> listAlbum;
    private List<Song> listSong;
    private String name;

    public Artist(String artistID, String description, String imageURL, List<Album> listAlbum, List<Song> listSong, String name) {
        this.artistID = artistID;
        this.description = description;
        this.imageURL = imageURL;
        this.listAlbum = listAlbum;
        this.listSong = listSong;
        this.name = name;
    }

    public String getArtistID() {
        return artistID;
    }

    public void setArtistID(String artistID) {
        this.artistID = artistID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public List<Album> getListAlbum() {
        return listAlbum;
    }

    public void setListAlbum(List<Album> listAlbum) {
        this.listAlbum = listAlbum;
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
