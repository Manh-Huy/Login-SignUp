package com.example.authenticationuseraccount.model.business;

import java.util.Date;

public class User {
    private String userID;
    private String username;
    private String email;
    private String role;
    private String expiredDatePremium;
    private String signInMethod;
    private String imageURL;

    // Step 2: Create a private static instance of the class
    private static User instance;

    // Step 1: Make the constructor private to prevent instantiation
    private User() {}

    // Step 3: Provide a public static method to return the single instance of the class
    public static User getInstance() {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = new User();
                }
            }
        }
        return instance;
    }

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

    public String getExpiredDatePremium() {
        return expiredDatePremium;
    }

    public void setExpiredDatePremium(String expiredDatePremium) {
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
