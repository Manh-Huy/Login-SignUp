package com.example.authenticationuseraccount.model.homepagemodel;

public class Banner {
    private String bannerID;
    private String imageURL;
    private String link;

    public Banner(String bannerID, String imageURL, String link) {
        this.bannerID = bannerID;
        this.imageURL = imageURL;
        this.link = link;
    }

    public String getBannerID() {
        return bannerID;
    }

    public void setBannerID(String bannerID) {
        this.bannerID = bannerID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
