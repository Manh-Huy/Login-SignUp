package com.example.authenticationuseraccount.model.business;

import java.util.Date;

public class User {
    private String userID;
    private String username;
    private String email;
    private String role;
    private Date expiredDatePremium;
    private String signInMethod;
    private String imageURL;

    public User() {}

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getExpiredDatePremium() {
        return expiredDatePremium;
    }

    public void setExpiredDatePremium(Date expiredDatePremium) {
        this.expiredDatePremium = expiredDatePremium;
    }

    public String getSignInMethod() {
        return signInMethod;
    }

    public void setSignInMethod(String signInMethod) {
        this.signInMethod = signInMethod;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
