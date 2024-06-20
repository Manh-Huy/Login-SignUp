package com.example.authenticationuseraccount.model;

public class Message {
    private String message;
    private String userName;
    private String messsageTimeStamp;
    private String imgUrl;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMesssageTimeStamp() {
        return messsageTimeStamp;
    }

    public void setMesssageTimeStamp(String messsageTimeStamp) {
        this.messsageTimeStamp = messsageTimeStamp;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Message(String message) {
        this.message = message;
    }

    public Message(String userName, String imgUrl, String message, String messsageTimeStamp) {
        this.message = message;
        this.userName = userName;
        this.messsageTimeStamp = messsageTimeStamp;
        this.imgUrl = imgUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
