package com.example.authenticationuseraccount.model;

import java.io.Serializable;

public class Genre implements Serializable {
    private String genreID;
    private String name;

    private String imageURL;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Genre(String genreID, String name) {
        this.genreID = genreID;
        this.name = name;
    }

    public String getGenreID() {
        return genreID;
    }

    public void setGenreID(String genreID) {
        this.genreID = genreID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
