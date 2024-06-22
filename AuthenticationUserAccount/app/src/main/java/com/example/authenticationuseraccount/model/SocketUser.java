package com.example.authenticationuseraccount.model;

public class SocketUser {
    private String socketID;
    private String userName;

    public SocketUser() {
    }

    public SocketUser(String socketID, String userName) {
        this.socketID = socketID;
        this.userName = userName;
    }

    public String getSocketID() {
        return socketID;
    }

    public void setSocketID(String socketID) {
        this.socketID = socketID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
