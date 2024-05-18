package com.example.authenticationuseraccount.model.homepagemodel;

public class NewRelease {
    private int image;
    private String nameSong;
    private String artist;

    public NewRelease(int image, String nameSong, String artist) {
        this.image = image;
        this.nameSong = nameSong;
        this.artist = artist;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getNameSong() {
        return nameSong;
    }

    public void setNameSong(String nameSong) {
        this.nameSong = nameSong;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
