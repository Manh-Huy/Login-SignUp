package com.example.authenticationuseraccount.model;

import java.util.List;

public class SearchHistory {
    private String userID;
    private List<String> history;

    public SearchHistory() {}

    public SearchHistory(String userID, List<String> history) {
        this.userID = userID;
        this.history = history;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
        this.history = history;
    }
}
