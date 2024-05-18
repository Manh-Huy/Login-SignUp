package com.example.authenticationuseraccount.model.homepagemodel;

public class PlayList {
    private int image;
    private String namePlayList;

    public PlayList(int image, String namePlayList) {
        this.image = image;
        this.namePlayList = namePlayList;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getNamePlayList() {
        return namePlayList;
    }

    public void setNamePlayList(String namePlayList) {
        this.namePlayList = namePlayList;
    }
}
